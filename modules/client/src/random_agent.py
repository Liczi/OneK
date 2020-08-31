import numpy as np


class RandomAgent:

    def __init__(self, game):
        self.game = game

    def choose_move(self, state):
        self.game.get_moves(state)
        return np.random.choice(self.game.get_moves(state)[1])
