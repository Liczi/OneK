from pyqlearning.qlearning.greedy_q_learning import GreedyQLearning


class OneKQlearning(GreedyQLearning):

    def extract_possible_actions(self, state_key) -> list:
        return

    def observe_reward_value(self, state_key, action_key) -> float:
        return

    def update_state(self, state_key, action_key) -> str:
        return

    def check_the_end_flag(self, state_key) -> str:
        return

    def visualize_learned_result(self, state_key):
        return
