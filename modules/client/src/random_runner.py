import sys

sys.path.append("./generated")
import grpc
import numpy as np
import time

from generated.server_pb2_grpc import GameServiceStub
from generated.server_pb2 import StartPayload, PerformPayload

channel = grpc.insecure_channel('localhost:50051')
server = GameServiceStub(channel)
GAMES_TO_PLAY = 1

all_times, all_turns = [], []
for _ in range(GAMES_TO_PLAY):
    i = 0
    now = time.time()

    payload = StartPayload()
    payload.names.extend(["RandomPlayer 1", "RandomPlayer 2"])
    state = server.start(payload)
    actions = server.actions(state).actions
    while actions:
        i += 1
        action = np.random.choice(actions)

        payload = PerformPayload()
        payload.action.CopyFrom(action)
        payload.state.CopyFrom(state)

        state = server.perform(payload)
        actions = server.actions(state).actions

    elapsedTime = (time.time() - now) * 1000
    all_turns.append(i)
    all_times.append(elapsedTime)
    print(state)
    print(f"Game ended, lasted: {i} turns, took {elapsedTime}ms, efficiency: {i / elapsedTime}[turns/ms]")

print(
    f"Test ended. Average turns: {np.mean(all_turns)}, average efficiency: {np.mean(np.array(all_turns) / np.array(all_times))}")
