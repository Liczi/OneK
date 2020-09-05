from collections import defaultdict
from utils import extract_player_cards, get_current_entity

triumph_points = {
    'H': 100,
    'D': 80,
    'C': 60,
    'S': 40
}

card_points = {
    'A': 11,
    'T': 10,
    'K': 4,
    'Q': 3,
    'J': 2,
    'N': 0
}


def compute_potential(card_strings):
    triumph_sum = compute_certain_triumph_points(card_strings)
    cards_by_color = defaultdict(list)
    for card in card_strings:
        cards_by_color[card[1]].append(card[0])
    card_sum = 0
    for color, cards in cards_by_color.items():
        contains_ace = any([card[0] == 'A' for card in cards])
        contains_ten = any([card[0] == 'A' for card in cards])

        if contains_ace and contains_ten:
            card_sum = 30 if len(cards) >= 4 else 28 if len(cards) >= 3 else 21
        elif contains_ace:
            card_sum = 30 if len(cards) >= 5 else 0
        else:
            card_sum = 0
    return card_sum + triumph_sum


def compute_potential_diff(state, action):
    if state.HasField("review"):
        if action.review.type == 2:  # BID
            current_bid = action.review.payload.new_bid
            potential = compute_potential(extract_player_cards(get_current_entity(state)))
            return current_bid - potential if current_bid < potential else None
        elif action.review.type == 3:  # CONFIRM = 3;
            if state.review.changed_bid <= 0:
                potential = compute_potential(extract_player_cards(get_current_entity(state)))
                current_bid = state.review.initial_bid
                return current_bid - potential if current_bid < potential else None
            return None
        else:
            return None
    else:
        return None


# returns 1 if did bid inside potential or folded outside
# returns -1 if did fold inside potential or bid outside
def did_fold_within_potential(state, action):
    if state.HasField("bidding"):
        current_bid = [it.last_action for it in state.bidding.order if it.HasField("last_action")]
        current_bid = [it.amount for it in current_bid if it.type == 0]
        current_bid = 100 if not current_bid else max(current_bid)

        potential = compute_potential(extract_player_cards(get_current_entity(state)))
        did_bid = action.bidding.type == 0
        if did_bid:
            return 1 if current_bid + 10 <= potential else 0
        else:
            return -1 if current_bid + 10 <= potential else 0
    else:
        return None


def get_triumph_colors(card_strings):
    queens_colors = [card[1] for card in card_strings if card[0] == 'Q']
    return {card[1] for card in card_strings if card[0] == 'K' and card[1] in queens_colors}


def get_triumph_points(card_strings):
    return sum([triumph_points[it[1]] for it in get_triumph_colors(card_strings)])


def compute_certain_triumph_points(card_strings):
    triumph_colors = get_triumph_colors(card_strings)
    guarded_triumph_colors = {card[1] for card in card_strings if card[0] == 'A' and card[1] in triumph_colors}
    unguarded_triumph_colors = triumph_colors - guarded_triumph_colors
    return sum([triumph_points[color] for color in guarded_triumph_colors]) + max(
        [triumph_points[color] for color in unguarded_triumph_colors]) if unguarded_triumph_colors else 0


def compute_gain(card, board, triumph):
    points = card_points[card[0]]
    if not board:
        return points
    board = board[0]  # TODO 2-player only
    board_points = card_points[board[0]]
    all_points = points + board_points

    if card[1] == triumph and board[1] == triumph:
        return -all_points if board_points > points else all_points
    elif card[1] == triumph:
        return all_points
    elif card[1] == board[1]:
        return -all_points if board_points > points else all_points
    else:
        return -all_points
