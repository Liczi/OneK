from mittmcts import Draw
from proto import Server
from state_utils import to_simple_action_string
from utils import get_actual_state, get_stage_winner, extract_player
from state_utils import ActionWrapper

#
# class Move:
#
#     def __init__(self, action) -> None:
#         self.action = action
#         self.simple_action = to_simple_action_string(action)  # MessageToJson(action, sort_keys=True, indent=-1)
#
#     def __eq__(self, o: object) -> bool:
#         return self.simple_action == o.simple_action
#
#     def __hash__(self) -> int:
#         return int.from_bytes(self.simple_action.encode("ascii"), 'big')


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
        if OneKGame.randomize and OneKGame.current_player(state) != OneKGame.current_player:
            return True, [ActionWrapper(it) for it in Server.get_moves(state)]
        return False, [ActionWrapper(it) for it in Server.get_moves(state)]

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
