import math
import pickle
from experiment_utils import test_agents
from mcts_agent import MCTSAgent
from wrapping_random_agent import WrappingRandomAgent

from game import OneKGame

game = OneKGame

TEST_GAMES = 10


def run(player_names, player_agents):
    # state = game.initial_state(player_names)
    # player_uuids = [player.uuid for player in extract_players(get_actual_state(state).order)]
    # players = [{'agent': agent, 'name': name, 'uuid': uuid} for (name, agent), uuid in
    #            zip(list(zip(player_names, player_agents)), player_uuids)]

    win_ratio, moves, points, potentials, did_folds = test_agents(player_names, player_agents, TEST_GAMES, game)
    print(f"Win ratio {win_ratio}")
    # win_ratio, avg_moves, point_stats = test_agents(player_names, player_agents, TEST_GAMES)
    # print(f"Tested step, win ratio: {win_ratio}, avg moves: {avg_moves}, points (mean, std): {point_stats}")
    return win_ratio, moves, points, potentials, did_folds


def checkpoint_stats(stats, name):
    with open(f"{name}.pickle", 'wb') as handle:
        pickle.dump(stats, handle, protocol=pickle.HIGHEST_PROTOCOL)


def run_with(param_name, params, agents_names, agents_factories):
    for param in params:
        print(f"Running experiment for {param_name}: {param_name}")
        results = run(agents_names, [it(param) for it in agents_factories])
        return results, agents_factories


OneKGame.randomize = False
if __name__ == '__main__':
    # qlearning_strong = QLearningAgent("data/qlearning-working-simple-r/qlearning-epoch-450000.csv", MinimalStateWrapper)
    # run(["Qlearning-strong", "Random"], [qlearning_strong, RandomAgent()])
    # qlearning_weak = QLearningAgent("data/qlearning-working-simple-r/qlearning-epoch-10000.csv", MinimalStateWrapper)
    # run(["Qlearning-weak", "Random"], [qlearning_weak, RandomAgent()])
    # run(["Qlearning-strong", "Qlearning-weak"], [qlearning_strong, qlearning_weak])



    all_results = {
        'max_time': [],
        'c': [],
    }
    # mcts_agent = MCTSAgent(game=game, max_iter=5, c=math.sqrt(2))
    # results = run(["MCTS", "Random"], [mcts_agent, WrappingRandomAgent(game)])
    # all_results['max_time'].append({'mcts': mcts_agent.results, 'game': results})
    # checkpoint_stats(all_results, f"data/IS-MCTS-time-c-10")

    # TODO save param value ??
    for max_time in [0.001, 0.01, 0.1, 1]:
        print(f"Running experiment for max_time: {max_time}")
        mcts_agent = MCTSAgent(game=game, max_time=max_time, c=math.sqrt(2))
        results = run(["MCTS", "Random"], [mcts_agent, WrappingRandomAgent(game)])
        all_results['max_time'].append({'mcts': mcts_agent.results, 'game': results})

    for c in [math.sqrt(it) for it in range(1, 5)]:
        print(f"Running experiment for c: {c}")
        mcts_agent1 = MCTSAgent(game=game, max_time=0.1, c=c)
        results = run(["MCTS", "Random"], [mcts_agent1, WrappingRandomAgent(game)])
        all_results['c'].append({'mcts': mcts_agent1.results, 'game': results})

    checkpoint_stats(all_results, f"data/MCTS-time-c-10")

    OneKGame.randomize = False

    for max_time in [0.001, 0.01, 0.1, 1]:
        print(f"Running experiment for max_time: {max_time}")
        mcts_agent = MCTSAgent(game=game, max_time=max_time, c=math.sqrt(2))
        results = run(["MCTS", "Random"], [mcts_agent, WrappingRandomAgent(game)])
        all_results['max_time'].append({'mcts': mcts_agent.results, 'game': results})

    for c in [math.sqrt(it) for it in range(1, 5)]:
        print(f"Running experiment for c: {c}")
        mcts_agent1 = MCTSAgent(game=game, max_time=0.1, c=c)
        results = run(["MCTS", "Random"], [mcts_agent1, WrappingRandomAgent(game)])
        all_results['c'].append({'mcts': mcts_agent1.results, 'game': results})

    checkpoint_stats(all_results, f"data/IS-MCTS-time-c-10")

