import numpy as np
import pandas as pd
import pickle
import time
from card_utils import compute_potential, triumph_points
from experiment_utils import test_agents
from mcts_agent import MCTSAgent
from proto import Server
from pyqlearning.qlearning.greedy_q_learning import GreedyQLearning
from random_agent import RandomAgent
from state_utils import ActionWrapper, MinimalStateWrapper
from tqdm import tqdm
from utils import extract_player_cards, get_actual_state, get_current_entity, get_current_player, \
    get_actual_action

from game import OneKGame

TRAINING_PLAYERS = ["Qlearning", "Random"]


def sophisticated_reward(self, state_key, action_key, next_state_key, available_actions):
    if next_state_key.state.HasField("summary"):
        round_ranking = state_key.state.summary.round_ranking
        other_sum = sum([points for uuid, points in round_ranking.items() if uuid != self.uuid])
        return round_ranking[self.uuid] - other_sum
    state = state_key.state
    if len(available_actions) <= 1:
        return 0
    actual_state = get_actual_state(state)
    actual_action = get_actual_action(action_key.action)
    type = getattr(actual_action, 'type', 0)

    if state.HasField("bidding"):
        potential = compute_potential(extract_player_cards(get_current_entity(state)))
        if type == 0:  # BID
            return 10 if actual_action.amount <= potential else 0
        else:  # FOLD
            return -10 if actual_state.current <= potential else 0
    elif state.HasField("review"):
        current_potential = compute_potential(extract_player_cards(get_current_entity(state)))
        if type == 1:  # DISTRIBUTE
            next_potential = compute_potential(extract_player_cards(get_current_entity(next_state_key.state)))
            return next_potential - current_potential
        elif type == 2:  # BID
            old_bid = actual_state.initial_bid
            new_bid = actual_action.payload.new_bid
            return new_bid - old_bid if new_bid <= current_potential else 0
        elif type == 3:  # CONFIRM (no bid)
            return -10 if actual_state.initial_bid <= current_potential else 0
        elif type == 4:  # RESTART
            return -10 if actual_state.initial_bid <= current_potential else 10
        else:
            return 0
    elif state.HasField("strife"):
        if type == 1:  # PLAY
            if next_state_key.state.HasField("strife"):
                current_ind = actual_state.current - 1
                next_order = next_state_key.state.strife.order
                current_strifer = get_current_entity(state)
                current_pts = current_strifer.points
                next_pts = next_order[current_ind].points
                current_opp_pts = sum([it.points for it in actual_state.order if
                                       it.holder.player.uuid != current_strifer.holder.player.uuid])
                next_opp_pts = sum([it.points for it in next_order if
                                    it.holder.player.uuid != current_strifer.holder.player.uuid])
                return (next_pts - current_pts) - (next_opp_pts - current_opp_pts)
            else:
                raise ValueError("Should have been handled already")
            # else:  # NEXT SUMMARY
            #      return
            # board = extract_board(actual_state)
            # triumph = actual_state.current_triumph
            # card = card_to_string(actual_action.card)
            # return compute_gain(card, board, triumph) / 10
        else:  # TRIUMPH
            return triumph_points[actual_action.card.color[0]]
    else:
        return 0


def simple_reward(self, state_key, action_key, next_state_key, available_actions):
    def get_reward_from_parent_state(state):
        round_ranking = state.state.summary.round_ranking
        other_sum = sum([points for uuid, points in round_ranking.items() if uuid != self.uuid])
        return round_ranking[self.uuid] - other_sum

    if next_state_key.state.HasField("summary"):
        return get_reward_from_parent_state(next_state_key)
    elif state_key.state.HasField("summary"):
        return get_reward_from_parent_state(state_key)
    else:
        return 0


class OneKQlearningTrainer(GreedyQLearning):
    epochs = 0
    restarts = 0
    epoch_started = time.time()
    step_started = time.time()
    q_table_stats = []
    step_times = []
    pbar = None

    def __init__(self, state_wrapper, reward_fun, trainer=None):
        # super.__epsilon_greedy_rate = epsilon
        # self.__alpha_value = alpha
        self.state_wrapper = state_wrapper
        self.trainer = trainer
        self.reward_fun = reward_fun

    def reset_uuid(self, new_state):
        self.uuid = get_current_entity(new_state).uuid

    def extract_possible_actions(self, state_key) -> list:
        return [ActionWrapper(it) for it in Server.get_moves(state_key.state)]

    def observe_reward_value(self, state_key, action_key, next_state_key, available_actions) -> float:
        return self.reward_fun(self, state_key, action_key, next_state_key, available_actions)

    def update_state(self, state_key, action_key):
        state = Server.perform_move(state_key.state, action_key.action)
        while not (state.HasField("summary") or get_current_player(state).uuid == self.uuid):
            moves = Server.get_moves(state)
            if not moves:
                break
            else:
                if not self.trainer:
                    action = np.random.choice(moves)
                else:
                    action = self.trainer.choose_move(state).action
                state = Server.perform_move(state, action)
        return self.state_wrapper(state)

    def check_the_end_flag(self, state_key, next_action_list):
        return not next_action_list

    def visualize_learning_result(self, state_key):
        self.epochs += 1
        self.pbar.update(1)
        if self.epochs % STATS_EACH == 0:
            self.q_table_stats.append(self.q_df.describe())
            self.step_times.append((time.time() - self.step_started) / STATS_EACH)
            self.step_started = time.time()
            checkpoint_stats({'step_times': self.step_times}, "data/runtime_stats")
            checkpoint_pandas_stats(self.q_table_stats, "data/runtime_q_table")
        if self.epochs % PRINT_EACH == 0:
            self.pbar.close()
            self.pbar = tqdm(total=PRINT_EACH)
            print(f"Step {self.epochs}, restarts {self.restarts}, elapsed {time.time() - self.epoch_started}s")
            print(self.q_df.describe())
            print(self.q_df.nlargest(10, 'q_value'))
            print(self.q_df.nsmallest(10, 'q_value'))
            self.epoch_started = time.time()
            print("\n")
        if self.epochs % EVAL_EACH == 0:
            win_ratio, avg_moves, point_stats = test_agents(TRAINING_PLAYERS, [self, RandomAgent()], TEST_GAMES)
            print(f"Tested step, win ratio: {win_ratio}, avg moves: {avg_moves}, points (mean, std): {point_stats}")
            name = f"data/qlearning-epoch-{agent.epochs}"
            checkpoint_agent(self, name)
            checkpoint_stats({'win_ratio': win_ratio, 'avg_moves': avg_moves, 'point_stats': point_stats}, name)
            print("\n")

    def choose_move(self, state):
        moves = Server.get_moves(state)
        action_key = self.predict_next_action(
            state_key=self.state_wrapper(state),
            next_action_list=[ActionWrapper(it) for it in moves]
        )
        return action_key.action


def checkpoint_pandas_stats(pd_stats, name):
    pd.concat(pd_stats, axis=1).transpose().reset_index() \
        .drop("index", axis=1).to_pickle(f"{name}.pickle", protocol=pickle.HIGHEST_PROTOCOL)


def checkpoint_stats(stats, name):
    with open(f"{name}.pickle", 'wb') as handle:
        pickle.dump(stats, handle, protocol=pickle.HIGHEST_PROTOCOL)


def checkpoint_agent(agent, name):
    agent.q_df.to_csv(f"{name}.csv", index=False)


STATS_EACH = 50
EPOCH_LIMIT = 100_000_000_000_000  # TODO decrease
PRINT_EACH = 10_000
EVAL_EACH = 50_000
TEST_GAMES = 1

# TODO OVERRIDEN
# PRINT_EACH = 10
# EVAL_EACH = 10
# STATS_EACH = 10
# TEST_GAMES = 1

# TODO make more efficient
# profile - scalar compare
from state_utils import StateWrapper
if __name__ == '__main__':
    _state = Server.initial_state(TRAINING_PLAYERS)
    mcts_agent = MCTSAgent(OneKGame, 2)
    # agent = OneKQlearningTrainer(epsilon=0.75, trainer=)
    # q_df = pd.read_csv('data/agent/qlearning-epoch-150000.csv')
    # q_df = pd.read_csv('data/qlearning-working-simple-r/qlearning-epoch-450000.csv')

    # agent = OneKQlearningTrainer(trainer=mcts_agent)
    # _state_wrapper = StateWrapper
    _state_wrapper = StateWrapper
    # agent = OneKQlearningTrainer(state_wrapper=_state_wrapper, reward_fun=simple_reward)
    agent = OneKQlearningTrainer(state_wrapper=_state_wrapper, reward_fun=sophisticated_reward)

    agent.set_epsilon_greedy_rate(0.75)
    # agent.set_alpha_value(0.5)
    # agent.q_df = q_df
    agent.reset_uuid(_state)
    agent.pbar = tqdm(total=PRINT_EACH)

    agent.learn(state_key=_state_wrapper(_state), limit=EPOCH_LIMIT)

    while agent.epochs <= EPOCH_LIMIT:
        agent.restarts += 1
        agent.learn(state_key=_state_wrapper(Server.restart(_state)), limit=EPOCH_LIMIT - agent.epochs)
