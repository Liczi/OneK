from mcts_agent import MCTSAgent
from random_agent import RandomAgent
from utils import get_game_winner, extract_players, get_actual_state, get_stage_winner

from game import OneKGame

game = OneKGame


def main(player_names, player_agents):
    state = game.initial_state(player_names)
    player_uuids = [player.uuid for player in extract_players(get_actual_state(state).order)]
    players = [{'agent': agent, 'name': name, 'uuid': uuid} for (name, agent), uuid in
               zip(list(zip(player_names, player_agents)), player_uuids)]

    i = 0
    while True:
        i += 1
        if get_stage_winner(state, 'D'):
            print([{next(player for player in players if player['uuid'] == uuid)['name']: points} for uuid, points in
                   get_actual_state(state).ranking.items()])
        if get_game_winner(state):
            print("Game ended")
            print(state)
            break  # TODO return stats
        else:
            current_uuid = game.current_player(state)
            game.set_current = current_uuid  # TODO stateful
            player = next(player for player in players if player['uuid'] == current_uuid)
            move = player['agent'].choose_move(state)
            state = game.apply_move(state, move)
            print(f"Move {i}")


OneKGame.randomize = False
if __name__ == '__main__':
    main(["MCTS", "Random"], [MCTSAgent(game, 100), RandomAgent(game)])
