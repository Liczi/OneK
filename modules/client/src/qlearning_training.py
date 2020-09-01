import json
import numpy as np
import pickle
import time
from experiment_utils import test_agents
from google.protobuf.json_format import MessageToJson
from proto import Server
from pyqlearning.qlearning.greedy_q_learning import GreedyQLearning
from random_agent import RandomAgent
from utils import extract_player_cards, get_actual_state, extract_board, get_current_entity, get_current_player

TRAINING_PLAYERS = ["Qlearning", "Random"]


def to_simple_state(state):
    actual_state = get_actual_state(state)
    return {
        'cards': sorted(extract_player_cards(actual_state.order[actual_state.current - 1])),
        'board': extract_board(actual_state),
        'triumph': getattr(actual_state, 'current_triumph', "")
    }


# TODO try using it also with MCTS
# TODO unify BID actions for bidding and review
# def to_simple_action(action):
#     action_payload_type = action.WhichOneof('action')
#     actual = getattr(state, action_payload_type)
#     if action_payload_type == 'bidding':
#
#     elif action_payload_type == 'review':
#         pass
#     elif action_payload_type == 'strife':
#         pass
#     else:
#         pass
#
#     return {}


class StateWrapper:

    def __init__(self, state) -> None:
        self.state = state
        self.simple_state = json.dumps(to_simple_state(state))

    def __eq__(self, o: object) -> bool:
        return self.simple_state == o.simple_state

    def __hash__(self) -> int:
        return hash(self.simple_state)

    def __str__(self) -> str:
        return self.simple_state


class ActionWrapper:

    def __init__(self, action) -> None:
        self.action = action
        self.simple_action = MessageToJson(action, sort_keys=True, indent=-1)

    def __eq__(self, o: object) -> bool:
        return self.simple_action == o.simple_action

    def __hash__(self) -> int:
        return hash(self.simple_action)

    def __str__(self) -> str:
        return self.simple_action


class OneKQlearningTrainer(GreedyQLearning):
    epochs = 0
    restarts = 0
    epoch_started = time.time()

    def reset_uuid(self, new_state):
        self.uuid = get_current_entity(new_state).uuid

    def extract_possible_actions(self, state_key) -> list:
        return [ActionWrapper(it) for it in Server.get_moves(state_key.state)]

    def observe_reward_value(self, state_key, last_action_key) -> float:
        if state_key.state.HasField("summary"):
            round_ranking = state_key.state.summary.round_ranking
            other_sum = sum([points for uuid, points in round_ranking.items() if uuid != self.uuid])
            return round_ranking[self.uuid] - other_sum
        else:
            return 0

    def update_state(self, state_key, action_key):
        state = Server.perform_move(state_key.state, action_key.action)
        while not (state.HasField("summary") or get_current_player(state).uuid == self.uuid):
            moves = Server.get_moves(state)
            if not moves:
                break
            else:
                action = np.random.choice(moves)
                state = Server.perform_move(state, action)
        return StateWrapper(state)

    def check_the_end_flag(self, state_key, next_action_list):
        return not next_action_list

    def visualize_learning_result(self, state_key):
        self.epochs += 1
        if self.epochs % CHECKPOINT_EACH == 0:
            print(f"Step {self.epochs}, restarts {self.restarts}, elapsed {time.time() - self.epoch_started}s")
            print(self.q_df.describe())
            win_ratio, avg_moves, point_stats = test_agents(TRAINING_PLAYERS, [self, RandomAgent()], TEST_GAMES)
            print(f"Tested step, win ratio: {win_ratio}, avg moves: {avg_moves}, points (mean, std): {point_stats}")
            self.epoch_started = time.time()
            checkpoint(self, {'win_ratio': win_ratio, 'avg_moves': avg_moves, 'point_stats': point_stats})

    def choose_move(self, state):
        moves = Server.get_moves(state)
        action_key = self.select_action(
            state_key=StateWrapper(state),
            next_action_list=[ActionWrapper(it) for it in moves]
        )
        return action_key.action


def checkpoint(agent, stats):
    name = f"data/qlearning-epoch-{agent.epochs}"
    agent.q_df.to_csv(f"{name}.csv")
    with open(f"{name}.pickle", 'wb') as handle:
        pickle.dump(stats, handle, protocol=pickle.HIGHEST_PROTOCOL)


EPOCH_LIMIT = 100_000_000_000  # TODO decrease
CHECKPOINT_EACH = 5000
TEST_GAMES = 5
# TODO revert changes in lib and run concurrent

if __name__ == '__main__':
    state = Server.initial_state(TRAINING_PLAYERS)
    agent = OneKQlearningTrainer()
    agent.reset_uuid(state)

    agent.learn(state_key=StateWrapper(state), limit=EPOCH_LIMIT)

    while agent.epochs <= EPOCH_LIMIT:
        agent.restarts += 1
        agent.learn(state_key=StateWrapper(Server.restart(state)), limit=EPOCH_LIMIT - agent.epochs)

    q_df = agent.q_df
    q_df = q_df.sort_values(by=["q_value"], ascending=False)
    print(q_df.head())

# df.max().max()
