from mittmcts import MCTS


class MCTSAgent:

    def __init__(self, game, max_iter):
        self.planner = lambda state: MCTS(game, state)
        self.max_iter = max_iter

    def choose_move(self, state):
        return self.planner(state).get_simulation_result(self.max_iter).move
