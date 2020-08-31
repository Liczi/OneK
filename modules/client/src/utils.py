def get_actual_state(state):
    return getattr(state, state.WhichOneof('state'))


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
