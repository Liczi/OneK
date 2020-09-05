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
                vals = pd.concat([pd.DataFrame.from_dict(dict, orient='index').transpose() for dict in vals])
                if agent_name not in vals.columns:
                    vals = 0
                else:
                    vals = do_transform(vals)[agent_name]
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


def plot_reg(data, title, x_y_labels, file_name, param_values, palette=None, dashes=None):
    plt.clf()
    sns.set_style("whitegrid")
    fig = sns.lmplot(x="index", y="value", hue="variable", fit_reg=True,
                     data=pd.melt(data.reset_index(), id_vars=['index'], value_vars=param_values))
    fig.set(xlabel=x_y_labels[0], ylabel=x_y_labels[1])
    plt.title(title)
    fig.savefig(f"plots/{file_name}")


def plot(data, title, x_y_labels, file_name, palette=None, dashes=None, ci=None, std=None):
    plt.clf()
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
    fig.figure.savefig(f"plots/{file_name}", bbox_inches='tight')


def load_data(params):
    res = {}
    for param in params:
        file_name = f"data/qlearning-alpha-simple-all/qlearning-alpha-{param}-simple_reward-game-results.pickle"
        # with open(f"data/qlearning-alpha-simple-all/qlearning-alpha-{alpha}-simple_reward-game-results.pickle",
        with open(file_name,'rb') as f:
            data = pickle.load(f)
            res[param] = data
    return res


fun_name = 'simple'
param_name = 'alpha'
# TODO plot average points in time, average moves in time and average potential loss and win % (with std)
# TODO correlate average points with Q value ?

# data = load_data([0.001, 0.01, 0.1, 1.])
# param_values = [0.3, 0.75, 0.9]
param_values = [0.001, 0.01, 0.1, 1.]
data = load_data(param_values)
step = 25_000

# win_ratio, moves, points, potentials, did_folds
win_ratio_df = to_dataframe(data, 0, step) * 100
plot(data=win_ratio_df, title="Zależność części wygranych gier od kroku uczenia",
     x_y_labels=["krok", "stosunek wygranych gier [%]"],
     file_name=f"ql-{param_name}-ratio-{fun_name}.png")  # , std=df_std)
plot_reg(data=win_ratio_df, title="Zależność części wygranych gier od kroku uczenia",
         x_y_labels=["krok", "stosunek wygranych gier [%]"], file_name=f"ql-{param_name}-ratio-{fun_name}-reg.png",
         param_values=param_values)

df_mean = to_dataframe(data, 1, step, 'Qlearning', do_transform=np.mean)
df_std = to_dataframe(data, 1, step, 'Qlearning', do_transform=np.std)
plot(data=df_mean, title="Zależność średniej liczby ruchów od kroku uczenia",
     x_y_labels=["krok", "średnia liczba ruchów"], file_name=f"ql-{param_name}-moves-{fun_name}.png")
plot_reg(data=df_mean, title="Zależność średniej liczby ruchów od kroku uczenia",
         x_y_labels=["krok", "średnia liczba ruchów"], file_name=f"ql-{param_name}-moves-{fun_name}-reg.png",
         param_values=param_values)

df_mean = to_dataframe(data, 2, step, 'Qlearning', do_transform=np.mean)
df_std = to_dataframe(data, 2, step, 'Qlearning', do_transform=np.std)
plot(data=df_mean, title="Zależność średniej liczby punktów od kroku uczenia",
     x_y_labels=["krok", "średnia liczba punktów"], file_name=f"ql-{param_name}-points-{fun_name}.png", std=df_std)
plot_reg(data=df_mean, title="Zależność średniej liczby punktów od kroku uczenia",
         x_y_labels=["krok", "średnia liczba punktów"], file_name=f"ql-{param_name}-points-{fun_name}-reg.png",
         param_values=param_values)

df_mean = to_dataframe(data, 3, step, 'Qlearning', do_transform=np.mean)
df_std = to_dataframe(data, 3, step, 'Qlearning', do_transform=np.std)
plot(data=df_mean, title="Zależność średniej straty potencjału punktów od kroku uczenia",
     x_y_labels=["krok", "średnia strata potencjału"], file_name=f"ql-{param_name}-loss-{fun_name}.png")
plot_reg(data=df_mean, title="Zależność średniej straty potencjału punktów od kroku uczenia",
         x_y_labels=["krok", "średnia strata potencjału"], file_name=f"ql-{param_name}-loss-{fun_name}-reg.png",
         param_values=param_values)

print(data)
