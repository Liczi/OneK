#     qlearning_simple_q_path = "data/qlearning-epsilon-simple-all/qlearning-epsilon-0.75-simple_reward-epoch-500000.csv"
#     qlearning_simple = QLearningAgent(qlearning_simple_q_path, MinimalStateWrapper)
#     qlearning_simple_wrapper = WrappingQLearningAgent(game, qlearning_simple_q_path, MinimalStateWrapper)
#     run(["Qlearning-simple", "Random"], [qlearning_simple, RandomAgent()])
#
#     qlearning_soph_q_path = "data/qlearning-epsilon-soph-all/qlearning-epsilon-0.75-sophisticated_reward-epoch-500000.csv"
#     qlearning_soph = QLearningAgent(qlearning_soph_q_path, MinimalStateWrapper)
#     qlearning_soph_wrapper = WrappingQLearningAgent(game, qlearning_soph_q_path, MinimalStateWrapper)
#     run(["Qlearning-soph", "Random"], [qlearning_soph, RandomAgent()])
#
#     run(["Qlearning-simple", "Qlearning-soph"], [qlearning_simple, qlearning_soph])
#
#     is_mcts = MCTSAgent(game, max_time=1)
#     # run(["IS-MCTS", "Random"], [is_mcts, WrappingRandomAgent(game)], game)
#     run(["IS-MCTS", "Qlearning-simple"], [is_mcts, qlearning_simple_wrapper], game)
#     run(["IS-MCTS", "Qlearning-soph"], [is_mcts, qlearning_soph_wrapper], game)
from mcts_agent import MCTSAgent
from utils import get_actual_state, extract_players, get_stage_winner, get_game_winner, get_current_player
from state_utils import StateWrapper, ActionWrapper
from game import OneKGame

game = OneKGame
agent = MCTSAgent(game, max_time=1)
players = ['human', 'mcts']
state = game.initial_state(players)
player_uuids = [player.uuid for player in extract_players(get_actual_state(state).order)]
players = [{'agent': agent, 'name': name, 'uuid': uuid} for (name, agent), uuid in
           zip(list(zip(players, ['human', agent])), player_uuids)]
while True:
    if game.current_player(state) == players[0]['uuid']:
        moves = game.get_moves(state)[1]
        print(StateWrapper(state))
        print([str(action) for action in moves])
        action_index = int(input(f"Choose 0-{len(moves)-1}"))
        move = moves[action_index]
    else:
        move = agent.choose_move(state)
        print(move)
    state = game.apply_move(state, move)
    # print(StateWrapper(state))