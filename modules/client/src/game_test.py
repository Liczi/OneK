from mcts_agent import MCTSAgent
from wrapping_random_agent import WrappingRandomAgent
from qlearning_agent import QLearningAgent
from random_agent import RandomAgent
from experiment_utils import test_agents
from game import OneKGame
from state_utils import MinimalStateWrapper

game = OneKGame

TEST_GAMES = 1000


def run(player_names, player_agents):
    # state = game.initial_state(player_names)
    # player_uuids = [player.uuid for player in extract_players(get_actual_state(state).order)]
    # players = [{'agent': agent, 'name': name, 'uuid': uuid} for (name, agent), uuid in
    #            zip(list(zip(player_names, player_agents)), player_uuids)]

    # win_ratio, avg_moves, point_stats = test_agents(player_names, player_agents, TEST_GAMES, game)
    win_ratio, avg_moves, point_stats = test_agents(player_names, player_agents, TEST_GAMES)
    print(f"Tested step, win ratio: {win_ratio}, avg moves: {avg_moves}, points (mean, std): {point_stats}")


OneKGame.randomize = False
if __name__ == '__main__':
    qlearning_strong = QLearningAgent("data/qlearning-working-simple-r/qlearning-epoch-450000.csv", MinimalStateWrapper)
    run(["Qlearning-strong", "Random"], [qlearning_strong, RandomAgent()])
    qlearning_weak = QLearningAgent("data/qlearning-working-simple-r/qlearning-epoch-10000.csv", MinimalStateWrapper)
    run(["Qlearning-weak", "Random"], [qlearning_weak, RandomAgent()])
    run(["Qlearning-strong", "Qlearning-weak"], [qlearning_strong, qlearning_weak])
# main(["MCTS", "Random"], [MCTSAgent(game, 2), WrappingRandomAgent(game)])
