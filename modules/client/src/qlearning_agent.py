import random
from state_utils import ActionWrapper

from proto import Server
import pandas as pd


class QLearningAgent:

    def __init__(self, q_table_path, state_wrapper):
        self.q_df = pd.read_csv(q_table_path)
        self.state_wrapper = state_wrapper

    def choose_move(self, state):
        actions = Server.get_moves(state)
        return self.predict_next_action(self.state_wrapper(state), [ActionWrapper(it) for it in actions]).action

    def predict_next_action(self, state_key, next_action_list):
        simple_actions = [it.simple_action for it in next_action_list]
        next_action_q_df = self.q_df[
            (self.q_df.state_key == state_key.simple_state) & (self.q_df.action_key.isin(simple_actions))]
        if next_action_q_df.shape[0] == 0:
            return random.choice(next_action_list)
        else:
            if next_action_q_df.shape[0] == 1:
                max_q_action = next_action_q_df["action_key"].values[0]
            else:
                next_action_q_df = next_action_q_df.sort_values(by=["q_value"], ascending=False)
                max_q_action = next_action_q_df.iloc[0, :]["action_key"]
            return [it for it in next_action_list if it.simple_action == max_q_action][0]
