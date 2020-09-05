from card_utils import compute_potential_diff, did_fold_within_potential
from collections import defaultdict
from proto import Server
from tqdm import tqdm
from utils import get_actual_state, extract_players, get_stage_winner, get_game_winner, get_current_player


def player_won(state, player_uuid):
    return 1 if get_actual_state(state).ranking[player_uuid] >= 1000 else -1


def map_uuid_key_to_name(uuid_dict, players):
    return {[it['name'] for it in players if it['uuid'] == k][0]: v for k, v in uuid_dict.items()}


def run_stateful_game(players, initial_state, game, max_moves):
    state = initial_state
    total_moves = 0
    points = defaultdict(list)
    moves = defaultdict(int)
    potentials = defaultdict(list)
    did_folds = defaultdict(list)
    while True:
        if total_moves >= max_moves:
            return 0, map_uuid_key_to_name(moves, players), map_uuid_key_to_name(points, players), map_uuid_key_to_name(
                potentials, players), map_uuid_key_to_name(did_folds, players)
        current_uuid = game.current_player(state)

        moves[current_uuid] += 1
        if get_stage_winner(state, 'D'):
            points[current_uuid].append(get_actual_state(state).round_ranking[players[0]['uuid']])
            # print([{next(player for player in players if player['uuid'] == uuid)['name']: points} for uuid, points in
            #        get_actual_state(state).ranking.items()])
        if not get_game_winner(state):
            game.set_current = current_uuid  # TODO stateful
            player = next(player for player in players if player['uuid'] == current_uuid)
            move = player['agent'].choose_move(state)

            # APPEND STATS
            potential = compute_potential_diff(state, move.action)
            if potential is not None:
                potentials[current_uuid].append(potential)
            did_fold = did_fold_within_potential(state, move.action)
            if did_fold is not None:
                did_folds[current_uuid].append(did_fold)

            state = game.apply_move(state, move)

            total_moves += 1
            continue

        moves = map_uuid_key_to_name(moves, players)
        points = map_uuid_key_to_name(points, players)
        potentials = map_uuid_key_to_name(potentials, players)
        did_folds = map_uuid_key_to_name(did_folds, players)
        return player_won(state, players[0]['uuid']), moves, points, potentials, did_folds


def run_game(players, initial_state, max_moves=10_000):
    state = initial_state
    total_moves = 0
    points = defaultdict(list)
    moves = defaultdict(int)
    potentials = defaultdict(list)
    did_folds = defaultdict(list)
    while True:
        if total_moves >= max_moves:
            return 0, map_uuid_key_to_name(moves, players), map_uuid_key_to_name(points, players), map_uuid_key_to_name(
                potentials, players), map_uuid_key_to_name(did_folds, players)
        current_uuid = get_current_player(state).uuid

        moves[current_uuid] += 1
        if get_stage_winner(state, 'D'):
            points[current_uuid].append(get_actual_state(state).round_ranking[players[0]['uuid']])
        if not get_game_winner(state):
            player = next(player for player in players if player['uuid'] == current_uuid)
            move = player['agent'].choose_move(state)

            # APPEND STATS
            potential = compute_potential_diff(state, move)
            if potential is not None:
                potentials[current_uuid].append(potential)
            did_fold = did_fold_within_potential(state, move)
            if did_fold is not None:
                did_folds[current_uuid].append(did_fold)

            state = Server.perform_move(state, move)

            total_moves += 1
            continue

        moves = map_uuid_key_to_name(moves, players)
        points = map_uuid_key_to_name(points, players)
        potentials = map_uuid_key_to_name(potentials, players)
        did_folds = map_uuid_key_to_name(did_folds, players)
        return player_won(state, players[0]['uuid']), moves, points, potentials, did_folds


def test_agents(agent_names, agents, games, stateful_game=None, max_moves=20_000):
    wins = draws = 0
    all_moves, all_point_stats, all_potentials, all_did_folds = [], [], [], []
    for _ in tqdm(range(0, games)):
        state = Server.initial_state(agent_names)
        player_uuids = [player.uuid for player in extract_players(get_actual_state(state).order)]
        players = [{'agent': agent, 'name': name, 'uuid': uuid} for (name, agent), uuid in
                   zip(list(zip(agent_names, agents)), player_uuids)]

        if stateful_game:
            res = run_stateful_game(players, state, stateful_game, max_moves)
        else:
            res = run_game(players, state, max_moves)
        win_or_draw, moves, points, potentials, did_folds = res
        if win_or_draw == 1:
            wins += 1
        elif win_or_draw == 0:
            draws += 1
        all_moves.append(moves)
        all_point_stats.append(points)
        all_potentials.append(potentials)
        all_did_folds.append(did_folds)
    return (wins / games, draws / games), all_moves, all_point_stats, all_potentials, all_did_folds
