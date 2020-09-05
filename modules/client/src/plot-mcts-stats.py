import math
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import pickle
import seaborn as sns


def transform_df(df, fun=np.mean):
    return {k: fun(v.values[0]) for k, v in df.items()}


def transform(entry, fun=np.mean):
    return transform_df(pd.DataFrame(entry), fun)


def get_title_name_for(param_name):
    return 'maksymalnego czasu ruchu' if 'max_time' == param_name else 'parametru C'


def get_axis_name_for(param_name):
    return 't [s]' if 'max_time' == param_name else 'C'


def to_dataframe(data, stat_name, param_name, param_values, extract_index=None, do_transform=False):
    data_df = pd.DataFrame()
    for idx, param_value in enumerate(param_values):
        vals = data[idx][stat_name]
        vals = vals[extract_index] if extract_index is not None else vals
        if do_transform:
            vals = pd.concat([pd.DataFrame.from_dict(dict, orient='index').transpose() for dict in vals])
        df = pd.DataFrame(vals)
        df[param_name] = param_value
        data_df = pd.concat([data_df, df])
    return data_df


def plot(data, title, x_y_labels, file_name, palette=None, dashes=None, ci=None, std=None):
    sns.set_style("whitegrid")
    fig = sns.lineplot(data=data)  # , palette=palette, dashes=dashes)
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


with open(f"data/mcts-game/IS-MCTS-time-c-10.pickle", 'rb') as f:
    data = pickle.load(f)
with open(f"data/mcts-game/MCTS-time-c-10.pickle", 'rb') as f:
    data2 = pickle.load(f)

# TODO CHANGE PARAMS HERE !!!!
param_values = [0.001, 0.01, 0.1, 1.0]
param_name = "max_time"
# param_values = [math.sqrt(it) for it in range(1, 5)]
# param_values = ["1", "√2", "√3", "2"]  # TODO CHANGE LABELS HERE WHEN PLOTTING !!!!
# param_name = 'c'

mcts_data_df = to_dataframe(data[param_name], 'mcts', param_name, param_values)
mcts_data_df2 = to_dataframe(data2[param_name], 'mcts', param_name, param_values)

leaf_data = mcts_data_df[['leaf_nodes', param_name]].groupby(param_name).mean().merge(
    mcts_data_df[['leaf_nodes', param_name]].groupby(param_name).std(), left_index=True,
    right_index=True, suffixes=["_mean", "_std"])

# TODO change here for table and
table_data = mcts_data_df2.drop(['leaf_nodes', 'max_depth'], axis=1).groupby(param_name).mean().merge(
    mcts_data_df2.drop(['leaf_nodes', 'max_depth'], axis=1).groupby(param_name).std(), left_index=True,
    right_index=True, suffixes=["_mean", "_std"])
print(table_data.to_latex())  # TODO COPY THIS TABLE !!!!

to_print = mcts_data_df[mcts_data_df.leaf_nodes != 0][['leaf_nodes', param_name]]
to_print['agent'] = 'IS-MCTS'
to_print2 = mcts_data_df2[mcts_data_df2.leaf_nodes != 0][['leaf_nodes', param_name]]
to_print2['agent'] = 'MCTS'


# TODO CHANGE SCALE IF NEEDED
sns.set_style("whitegrid")
plt.title(f"Wykres zależności liczby liści od {get_title_name_for(param_name)}")
fig = sns.barplot(x=param_name, y="leaf_nodes", hue="agent", data=pd.concat([to_print, to_print2]))
fig.set_yscale("log")
fig.set(xlabel=get_axis_name_for(param_name), ylabel="liczba liści")
fig.figure.savefig(f"plots/leafs-{param_name}.png")





# TODO CHANGE NAME AND COPY TABLE
player_name = 'MCTS'
data = data2 # TODO change here

game_data = data[param_name]
game_data_df = pd.DataFrame()
win_rates = []
for idx, param_value in enumerate(param_values):
    result = game_data[idx]['game']
    won, drew = result[0]
    win_rates.append(result[0])

# win_ratio, moves, points, potentials, did_folds
move_df = to_dataframe(data[param_name], 'game', param_name, param_values, extract_index=1)
move_df_mean = move_df.groupby(param_name).mean()[player_name]



potential_df = to_dataframe(data[param_name], 'game', param_name, param_values, extract_index=3, do_transform=True)
potential_df_mean = potential_df.groupby(param_name).mean()[player_name]
did_fold_within_potential_df = to_dataframe(data[param_name], 'game', param_name, param_values, extract_index=4,
                                            do_transform=True)
outside_potential = \
    did_fold_within_potential_df[did_fold_within_potential_df[player_name] < 0].groupby(param_name).sum()[player_name]
outside_potential_norm = outside_potential / did_fold_within_potential_df.groupby(param_name).count()[
    player_name] * -100

inside_potential = \
    did_fold_within_potential_df[did_fold_within_potential_df[player_name] > 0].groupby(param_name).sum()[player_name]
inside_potential_norm = inside_potential / did_fold_within_potential_df.groupby(param_name).count()[player_name] * 100


print(to_dataframe(data[param_name], 'game', param_name, param_values, extract_index=0))


# TODO COPY TABLE inside, outside, moves, potential + win rate !!!
potential_perc_norm = pd.DataFrame(
    [inside_potential_norm, outside_potential_norm, move_df_mean, potential_df_mean]).transpose()
print(potential_perc_norm.to_latex())


# TODO CHANGE SCALE IF NEEDED!!!
point_df = to_dataframe(data[param_name], 'game', param_name, param_values, extract_index=2, do_transform=True)[
    ['IS-MCTS', param_name]].rename(columns={'IS-MCTS': 'points'})
point_df2 = to_dataframe(data2[param_name], 'game', param_name, param_values, extract_index=2, do_transform=True)[
    ['MCTS', param_name]].rename(columns={'MCTS': 'points'})
point_df['agent'] = 'IS-MCTS'
point_df2['agent'] = 'MCTS'
to_plot = pd.concat([point_df, point_df2])

plt.clf()
sns.set_style("whitegrid")
plt.title(f"Wykres zależności średniej liczby punktów od {get_title_name_for(param_name)}")
fig = sns.barplot(x=param_name, y="points", hue="agent", data=to_plot)
# fig.set_yscale("log")
fig.set(xlabel=get_axis_name_for(param_name), ylabel="średnia liczba punktów")
fig.figure.savefig(f"plots/points-{param_name}.png")

x = 1
palette = None
dashes = None
# dashes = ["", "", (3, 1), (3, 1)]
# dashes = ["", "", "", ""]
# plot(data=to_print, title="Zależność wartości minimalnej i maksymalnej Q od kroku uczenia",
#      x_y_labels=["krok", "wartość Q"], file_name="full_figure.png", palette=palette,
#      dashes=dashes)  # , std=std_df) # ci=ci_df
# first_label = 'roz'
# second_label = 'pod'
# columns_mapping = {'mean_x': first_label, 'mean_y': second_label}  # TODO
#
# columns_mapping = {'max_x': first_label + '-max', 'min_x': first_label + '-min', 'max_y': second_label + '-max',
#                    'min_y': second_label + '-min'}
#
# std_mappings = {'std_x': first_label, 'std_y': second_label}
# ci_mappings = {first_label: ['max_x', 'min_x'], second_label: ['max_y', 'min_y']}
# merged = pd.concat([data, data3], axis=1).merge(pd.concat([data2, data4], axis=1), left_index=True,
#                                                 right_index=True)[:100].rename(columns=columns_mapping)

# merged.index = merged.index * 50
# print(merged)

# to_print = merged[columns_mapping.values()]
# print(to_print)


#
# p = sns.color_palette("Paired")
# palette = None
# dashes = None
# # dashes = ["", "", (3, 1), (3, 1)]
# palette = [p[2], p[4], p[3], p[5]]
# dashes = ["", "", "", ""]
