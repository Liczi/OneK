import matplotlib.pyplot as plt
import pandas as pd
import pickle
import seaborn as sns


def plot(data, title, x_y_labels, file_name, ci=None, std=None):
    sns.set_style("whitegrid")
    fig = sns.lineplot(data=data)
    fig.set(xlabel=x_y_labels[0], ylabel=x_y_labels[1])
    plt.title(title)
    x = data.index.values
    if ci is not None:
        for column in data.columns:
            ci_upper = ci[column][0].values
            ci_lower = ci[column][1].values
            fig.fill_between(x, ci_upper, ci_lower, alpha=.1)
    if std is not None:
        for column in data.columns:
            y = data[column].values
            y_std = std[column].values
            fig.fill_between(x, (y - y_std), (y + y_std), alpha=.1)
    fig.figure.savefig(f"plots/{file_name}")


with open(f"data/qlearning-working-soph-r/runtime_q_table.pickle", 'rb') as f:
    data = pickle.load(f)
    # data.reset_index(inplace=True)
with open(f"data/qlearning-working-simple-r/runtime_q_table.pickle", 'rb') as f:
    data2 = pickle.load(f)
    # data2.reset_index(inplace=True)

with open(f"data/qlearning-working-soph-r/runtime_stats.pickle", 'rb') as f:
    data3 = pd.DataFrame(pickle.load(f))
with open(f"data/qlearning-working-simple-r/runtime_stats.pickle", 'rb') as f:
    data4 = pd.DataFrame(pickle.load(f))

print(data)
print(data2)

first_label = 'rozszerzona'
second_label = 'podstawowa'
columns_mapping = {'mean_x': first_label, 'mean_y': second_label}
std_mappings = {'std_x': first_label, 'std_y': second_label}
ci_mappings = {first_label: ['max_x', 'min_x'], second_label: ['max_y', 'min_y']}
merged = pd.concat([data, data3], axis=1).merge(pd.concat([data2, data4], axis=1), left_index=True,
                                                right_index=True)[:100].rename(columns=columns_mapping)
# data.merge(data2.merge(pd.DataFrame(data3).merge(pd.DataFrame(data4))), left_index=True, right_index=True))

# merged = data.merge(data2, left_index=True, right_index=True)[:-1].rename(
#     columns=columns_mapping)
merged.index = merged.index * 50
print(merged)

to_print = merged[columns_mapping.values()]
print(to_print)

# plot(data=to_print, title="Zależność czasu przetworzenia jednego kroku od numeru kroku uczenia", x_y_labels=["krok", "czas [s]"],
#      file_name="full_figure.png")
std_df = merged[std_mappings.keys()].rename(columns=std_mappings)  # TODO subtract from y
ci_df = {k: [merged[it] for it in v] for k, v in ci_mappings.items()}
plot(data=to_print, title="Zależność wilekość Q-table od kroku uczenia", x_y_labels=["krok", "rozmiar Q-table"],
     file_name="full_figure.png", std=std_df) # ci=ci_df
# plot(data=data2['count'], title="Zależność wilekość Q-table od kroku uczenia",
#      x_y_labels=["krok", "rozmiar Q-table"], file_name="full_figure.png")
