x = {'a': 1, 'b': 2, 'c': 2}
l = [1, 2]
a = [1, 2]
b = [3, 4]
c = [5, 6]
import pickle

# with open(f"data/qlearning-epoch-500.pickle", 'rb') as handle:
#     print(pickle.load(handle))

import pandas as pd

# q_df = pd.read_csv('data/agent/qlearning-epoch-100000.csv')
# q_df.drop(columns='to_drop').to_csv('data/agent/qlearning-epoch-100000.csv', index=False)
# q_df

with open(f"data/qlearning-alpha-0.1-runtime-stats.pickle", 'rb') as f:
    data = pickle.load(f)
print(data)

# with open(f"data/qlearning-invalid-to-plot/runtime_q_table.pickle", 'rb') as f:
#     data = pickle.load(f)
# print(data)