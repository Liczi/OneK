import numpy as np
from proto import Server


class RandomAgent:

    def choose_move(self, state):
        return np.random.choice(Server.get_moves(state))
