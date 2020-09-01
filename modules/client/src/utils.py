def get_actual_state(state):
    return getattr(state, state.WhichOneof('state'))


def get_current_entity(parent_state):
    state = get_actual_state(parent_state)
    return state.order[state.current - 1]


def get_current_player(parent_state):
    return extract_player(get_current_entity(parent_state))


def get_game_winner(state):
    ranking = get_actual_state(state).ranking
    return next(iter([uuid for uuid, points in ranking.items() if points >= 1000]), None)


def get_stage_winner(state, draw_symbol):
    if state.HasField("summary"):
        ranking = get_actual_state(state).round_ranking
        if len(set(ranking.values())) <= 1:
            return draw_symbol
        else:
            return max(ranking.keys(), key=(lambda key: ranking[key]))
    else:
        return None


def extract_players(order):
    return [extract_player(it) for it in order]


def extract_player(entity):
    if getattr(entity, 'uuid', None):
        return entity
    elif getattr(entity, 'player', None):
        return entity.player
    else:
        return entity.holder.player


def extract_player_cards(entity):
    if getattr(entity, 'cards', None):
        return [card_to_string(it) for it in entity.cards]
    elif getattr(entity, 'holder', None):
        return [card_to_string(it) for it in entity.holder.cards]
    else:
        return []


def extract_board(state):
    order = state.order
    if getattr(order[0], 'is_constrained', None):
        return [card_to_string(it.last_action.card) for it in order if
                getattr(it, 'last_action', None) and it.last_action.card.figure]
    else:
        return []


def card_to_string(card):
    return f"{card.figure} of {card.color}"
