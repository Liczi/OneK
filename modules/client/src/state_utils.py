from card_utils import compute_potential
from utils import extract_player_cards, get_actual_state, extract_board, card_to_string, get_actual_action, \
    get_current_entity

# TODO extract Wrapper class
class MinimalStateWrapper:
    def __init__(self, state) -> None:
        self.state = state
        self.simple_state = to_minimal_state_string(state)
        self.hash = int.from_bytes(self.simple_state.encode("ascii"), 'big')

    def __eq__(self, o: object) -> bool:
        return self.simple_state == o.simple_state

    def __hash__(self) -> int:
        return self.hash

    def __str__(self) -> str:
        return self.simple_state


class StateWrapper:

    def __init__(self, state) -> None:
        self.state = state
        self.simple_state = to_simple_state_string(state)
        self.hash = int.from_bytes(self.simple_state.encode("ascii"), 'big')

    def __eq__(self, o: object) -> bool:
        return self.simple_state == o.simple_state

    def __hash__(self) -> int:
        return self.hash

    def __str__(self) -> str:
        return self.simple_state


class ActionWrapper:

    def __init__(self, action) -> None:
        self.action = action
        self.simple_action = to_simple_action_string(action)
        self.hash = int.from_bytes(self.simple_action.encode("ascii"), 'big')

    def __eq__(self, o: object) -> bool:
        return self.simple_action == o.simple_action

    def __hash__(self) -> int:
        return self.hash

    def __str__(self) -> str:
        return self.simple_action


# TODO: try not to include potential here (triumph points + aces, tens (28))
def to_minimal_state_string(state):
    actual_state = get_actual_state(state)
    if state.HasField("review") and not state.review.HasField("to_give"):
        return "--"
    elif state.HasField("bidding") or state.HasField("review"):
        potential = compute_potential(extract_player_cards(get_current_entity(state)))
        return f"P:{potential}"
    # elif :
    #     return
    elif state.HasField("strife"):
        board = ",".join(extract_board(actual_state))
        triumph = getattr(actual_state, 'current_triumph', "")
        return f"B:{board}|T:{triumph}"
    else:
        return "--"
# def to_minimal_state_string(state):
#     actual_state = get_actual_state(state)
#     if state.HasField("bidding") or state.HasField("review"):
#         potential = compute_potential(extract_player_cards(get_current_entity(state)))
#         return f"P:{potential}"
#     elif state.HasField("strife"):
#         board = ",".join(extract_board(actual_state))
#         triumph = getattr(actual_state, 'current_triumph', "")
#         return f"B:{board}|T:{triumph}"
#     else:
#         return "--"


def to_simple_state_string(state):
    actual_state = get_actual_state(state)
    cards = ",".join(sorted(extract_player_cards(actual_state.order[actual_state.current - 1])))
    board = ",".join(extract_board(actual_state))
    triumph = getattr(actual_state, 'current_triumph', "")
    return f"C:{cards}|B:{board}|T:{triumph[0] if triumph else '-'}"


# TODO try using it also with MCTS
def to_simple_action_string(action):
    action_payload_type = action.WhichOneof('action')
    actual = get_actual_action(action)
    type = getattr(actual, 'type', 0)

    if action_payload_type == 'bidding':
        if type == 1:
            return "FOLD"
        else:
            return f"BID{actual.amount}"

    elif action_payload_type == 'review':
        if type == 1:
            card = card_to_string(actual.payload.distribute.payload[0].card)  # TODO 2-player variant only
            return f"DISTR|{card}"
        elif type == 2:
            return f"BID|{actual.payload.new_bid}"
        elif type == 3:
            return "CONF"
        elif type == 4:
            return "RESTART"
        else:
            return "PICK"

    elif action_payload_type == 'strife':
        card = card_to_string(actual.card)
        if type == 1:
            return f"TRIUMPH|{card}"
        else:
            return f"PLAY|{card}"
    else:
        return "START"
