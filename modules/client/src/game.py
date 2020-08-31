from google.protobuf.json_format import MessageToJson
from mittmcts import Draw
from utils import get_actual_state, get_stage_winner, extract_player

from server import Server


class Move:

    def __init__(self, action) -> None:
        self.action = action
        self.action_json = MessageToJson(action)

    def __hash__(self) -> int:
        return hash(self.action_json)
        # TODO change
        # return action_hash(self.action)


class OneKGame(object):
    """Standard OneK game"""
    randomize = False

    @classmethod
    def initial_state(cls, player_names):
        return Server.initial_state(player_names)

    @classmethod
    def apply_move(cls, state, move):
        return Server.perform_move(state, move.action)

    @staticmethod
    def get_moves(state):
        if OneKGame.randomize and OneKGame.current_player(state) == OneKGame.current_player:
            return True, [Move(it) for it in Server.get_moves(state)]
        return False, [Move(it) for it in Server.get_moves(state)]

    @staticmethod
    def get_winner(state):
        return get_stage_winner(state, Draw)

    @staticmethod
    def current_player(state):
        state = get_actual_state(state)
        current_ind = state.current - 1
        return extract_player(state.order[current_ind]).uuid

    @staticmethod
    def print_board(state):
        print(state)

    @classmethod
    def set_current(cls, player):
        cls.current_player = player
