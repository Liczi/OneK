import numpy as np
from proto import Server
from tqdm import tqdm
from utils import get_actual_state, extract_players, get_stage_winner, get_game_winner, get_current_player


def player_won(state, player_uuid):
    return 1 if get_actual_state(state).ranking[player_uuid] >= 1000 else -1


def run_stateful_game(players, initial_state, game, max_moves=10_000):
    state = initial_state
    i = 0
    results = []
    while True:
        i += 1
        if i >= max_moves:
            return i, 0, results
        if get_stage_winner(state, 'D'):
            results.append(get_actual_state(state).round_ranking[players[0]['uuid']])
            # print([{next(player for player in players if player['uuid'] == uuid)['name']: points} for uuid, points in
            #        get_actual_state(state).ranking.items()])
        if not get_game_winner(state):
            current_uuid = game.current_player(state)
            game.set_current = current_uuid  # TODO stateful
            player = next(player for player in players if player['uuid'] == current_uuid)
            move = player['agent'].choose_move(state)
            state = game.apply_move(state, move)
            continue

        return i, player_won(state, players[0]['uuid']), results


def run_game(players, initial_state, max_moves=10_000):
    state = initial_state
    i = 0
    results = []
    while True:
        i += 1
        if i >= max_moves:
            return i, 0, results
        if get_stage_winner(state, 'D'):
            results.append(get_actual_state(state).round_ranking[players[0]['uuid']])
        if not get_game_winner(state):
            # TODO check if it's

            current_uuid = get_current_player(state).uuid
            player = next(player for player in players if player['uuid'] == current_uuid)
            move = player['agent'].choose_move(state)
            state = Server.perform_move(state, move)
            continue

        return i, player_won(state, players[0]['uuid']), results


def test_agents(agent_names, agents, games, stateful_game=None):
    wins = draws = 0
    moves, all_point_stats = [], []
    for _ in tqdm(range(0, games)):
        state = Server.initial_state(agent_names)
        player_uuids = [player.uuid for player in extract_players(get_actual_state(state).order)]
        players = [{'agent': agent, 'name': name, 'uuid': uuid} for (name, agent), uuid in
                   zip(list(zip(agent_names, agents)), player_uuids)]

        if stateful_game:
            move, result, point_stats = run_stateful_game(players, state, stateful_game)
        else:
            move, result, point_stats = run_game(players, state)
        if result == 1:
            wins += 1
        elif result == 0:
            draws += 1
        moves.append(move)
        all_point_stats.append(point_stats)
    joint_results = np.concatenate(all_point_stats)
    return (wins / games, draws / games), np.mean(moves), (np.mean(joint_results), np.std(joint_results))
