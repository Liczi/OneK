import numpy as np
from proto import Server
from tqdm import tqdm
from utils import get_actual_state, extract_players, get_stage_winner, get_game_winner, get_current_player


def player_won(state, player_uuid):
    return 1 if get_actual_state(state).ranking[player_uuid] >= 1000 else 0


def run_game(players, initial_state):
    state = initial_state
    i = 0
    results = []
    while True:
        i += 1
        if get_stage_winner(state, 'D'):
            results.append(get_actual_state(state).round_ranking[players[0]['uuid']])
            #      print(
            #     [{next(player for player in players if player['uuid'] == uuid)['name']: points} for uuid, points in
            #      get_actual_state(state).ranking.items()])
        if get_game_winner(state):
            # print("Game ended")
            # print(state)
            return i, player_won(state, players[0]['uuid']), results
        else:
            current_uuid = get_current_player(state).uuid
            player = next(player for player in players if player['uuid'] == current_uuid)
            move = player['agent'].choose_move(state)
            state = Server.perform_move(state, move)


def test_agents(agent_names, agents, games):
    wins, moves, all_results = [], [], []
    for _ in tqdm(range(0, games)):
        state = Server.initial_state(agent_names)
        player_uuids = [player.uuid for player in extract_players(get_actual_state(state).order)]
        players = [{'agent': agent, 'name': name, 'uuid': uuid} for (name, agent), uuid in
                   zip(list(zip(agent_names, agents)), player_uuids)]

        move, win, results = run_game(players, state)
        wins.append(win)
        moves.append(move)
        all_results.append(results)
    joint_results = np.concatenate(all_results)
    return sum(wins) / games, np.mean(moves), (np.mean(joint_results), np.std(joint_results))
