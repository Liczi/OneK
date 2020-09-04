from mittmcts import MCTS
import math

class MCTSAgent:

    def __init__(self, game, max_time=None, max_iter=None, c=math.sqrt(2)):
        self.planner = lambda state: MCTS(game, state, c)
        self.max_time = max_time
        self.max_iter = max_iter
        self.results = []

    def choose_move(self, state):
        _, move, leaf_nodes, avg_depth, max_depth = self.planner(state).get_simulation_result(
            iterations=self.max_iter, max_seconds=self.max_time, get_leaf_nodes=True)
        self.results.append({'leaf_nodes': len(leaf_nodes), 'avg_depth': avg_depth, 'max_depth': max_depth})
        return move

    # def get_results(self):
    #     return self.results
