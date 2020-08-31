from server import Server


class QLearningAgent:

    def __init__(self, game, epsilon):
        self.game = game
        self.epsilon = epsilon

    def choose_move(self, state):
        return Server  # TODO
