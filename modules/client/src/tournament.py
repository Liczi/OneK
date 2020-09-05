import pandas as pd
import pickle
from experiment_utils import test_agents
from state_utils import MinimalStateWrapper
from random_agent import RandomAgent
from qlearning_agent import QLearningAgent, WrappingQLearningAgent
from game import OneKGame
import numpy as np
from mcts_agent import MCTSAgent
from wrapping_random_agent import WrappingRandomAgent
game = OneKGame

TEST_GAMES = 10


def run(player_names, player_agents, game=None):
    def transform(entry, fun=np.mean):
        return {k: fun(v.values[0]) for k,v in pd.DataFrame(entry).items()}
    print(f"{player_names[0]} vs {player_names[1]}")
    win_ratio, moves, points, potentials, did_folds = test_agents(player_names, player_agents, TEST_GAMES, stateful_game=game)
    print(f"\nWin ratio {win_ratio}")
    print(f"moves: {pd.DataFrame(moves).mean()}")
    print(f"points_mean: {transform(points)}; points_std: {transform(points, np.std)}")
    print(f"potentials_mean: {transform(potentials)}; potentials_std: {transform(potentials, np.std)}")
    print(f"did_folds_mean: {transform(did_folds)}; did_folds_std: {transform(did_folds, np.std)}")

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
    qlearning_simple_q_path = "data/qlearning-epsilon-simple-all/qlearning-epsilon-0.75-simple_reward-epoch-500000.csv"
    qlearning_simple = QLearningAgent(qlearning_simple_q_path, MinimalStateWrapper)
    qlearning_simple_wrapper = WrappingQLearningAgent(game, qlearning_simple_q_path, MinimalStateWrapper)
    run(["Qlearning-simple", "Random"], [qlearning_simple, RandomAgent()])

    qlearning_soph_q_path = "data/qlearning-epsilon-soph-all/qlearning-epsilon-0.75-sophisticated_reward-epoch-500000.csv"
    qlearning_soph = QLearningAgent(qlearning_soph_q_path, MinimalStateWrapper)
    qlearning_soph_wrapper = WrappingQLearningAgent(game, qlearning_soph_q_path, MinimalStateWrapper)
    run(["Qlearning-soph", "Random"], [qlearning_soph, RandomAgent()])

    run(["Qlearning-simple", "Qlearning-soph"], [qlearning_simple, qlearning_soph])

    is_mcts = MCTSAgent(game, max_time=0.5)
    # run(["IS-MCTS", "Random"], [is_mcts, WrappingRandomAgent(game)], game)
    run(["IS-MCTS", "Qlearning-simple"], [is_mcts, qlearning_simple_wrapper], game)
    run(["IS-MCTS", "Qlearning-soph"], [is_mcts, qlearning_soph_wrapper], game)




    # qlearning_simple = QLearningAgent("data/qlearning-working-simple-r/qlearning-epoch-150000.csv", MinimalStateWrapper)
    # run(["Qlearning-simple-150k", "Random"], [qlearning_simple, RandomAgent()])
    # qlearning_soph = QLearningAgent("data/qlearning-working-soph-r/qlearning-epoch-150000.csv", MinimalStateWrapper)
    # run(["Qlearning-soph-150k", "Random"], [qlearning_soph, RandomAgent()])
    # run(["Qlearning-simple-150k", "Qlearning-soph-150k"], [qlearning_simple, qlearning_soph])
