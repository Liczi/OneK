import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import pickle
import seaborn as sns


def transform_df(df, fun=np.mean):
    return {k: fun(v.values[0]) for k, v in df.items()}


def transform(entry, fun=np.mean):
    return transform_df(pd.DataFrame(entry), fun)


def to_dataframe(data, extract_index, step, agent_name=None, do_transform=None, extract_first=False):
    # data_df = pd.DataFrame()
    data_dict = {}
    for name, values in data.items():
        temp = []
        for row in values[-20:]:  # TODO !!!!
            vals = row[extract_index]
            if do_transform is not None:
                vals = do_transform(pd.concat([pd.DataFrame.from_dict(dict, orient='index').transpose() for dict in vals])[agent_name])
                # if extract_first:
                #     vals = do_transform(pd.DataFrame(vals[agent_name]))
                # else:
                #     vals = do_transform(pd.DataFrame(vals))[agent_name]
            else:
                vals = vals[0]
            temp.append(vals)
        data_dict[name] = temp
    data_df = pd.DataFrame(data_dict)
    data_df.reset_index(inplace=True)
    data_df.index = (data_df.index + 1) * step
    return data_df.drop('index', axis=1)


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


def load_data(alphas):
    res = {}
    for alpha in alphas:
        # with open(f"data/qlearning-alpha-simple-all/qlearning-alpha-{alpha}-simple_reward-game-results.pickle",
        with open(f"data/qlearning-alpha-soph-all/qlearning-alpha-{alpha}-sophisticated_reward-game-results.pickle",
                  'rb') as f:
            data = pickle.load(f)
            res[alpha] = data
    return res


# TODO plot average points in time, average moves in time and average potential loss and win % (with std)
# TODO correlate average points with Q value ?

data = load_data([0.001, 0.01, 0.1, 1.])
step = 5_000

# win_ratio, moves, points, potentials, did_folds
win_ratio_df = to_dataframe(data, 0, step)*100
plot(data=win_ratio_df, title="Zależność części wygranych gier od kroku uczenia",
     x_y_labels=["krok", "stosunek wygranych gier [%]"], file_name="ql-alpha-ratio-soph.png")#, std=df_std)
plt.clf()


df_mean = to_dataframe(data, 1, step, 'Qlearning', do_transform=np.mean)
df_std = to_dataframe(data, 1, step, 'Qlearning', do_transform=np.std)
plot(data=df_mean, title="Zależność średniej liczby ruchów od kroku uczenia",
     x_y_labels=["krok", "średnia liczba ruchów"], file_name="ql-alpha-moves-soph.png")#, std=df_std)
plt.clf()



df_mean = to_dataframe(data, 2, step, 'Qlearning', do_transform=np.mean)
df_std = to_dataframe(data, 2, step, 'Qlearning', do_transform=np.std)
plot(data=df_mean, title="Zależność średniej liczby punktów od kroku uczenia",
     x_y_labels=["krok", "średnia liczba punktów"], file_name="ql-alpha-points-soph.png", std=df_std)
plt.clf()



df_mean = to_dataframe(data, 3, step, 'Qlearning', do_transform=np.mean)
df_std = to_dataframe(data, 3, step, 'Qlearning', do_transform=np.std)
plot(data=df_mean, title="Zależność średniej straty potencjału punktów od kroku uczenia",
     x_y_labels=["krok", "średnia strata potencjału"], file_name="ql-alpha-loss-soph.png")#, std=df_std)
plt.clf()

print(data)
