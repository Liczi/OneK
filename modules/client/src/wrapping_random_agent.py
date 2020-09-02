import numpy as np


class WrappingRandomAgent:
    game = None

    def __init__(self, game) -> None:
        self.game = game

    def choose_move(self, state):
        return np.random.choice(self.game.get_moves(state)[1])
