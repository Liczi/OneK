import sys

sys.path.append("./generated")
import grpc

from generated.server_pb2_grpc import GameServiceStub
from generated.server_pb2 import StartPayload, PerformPayload


class Server(object):
    channel = grpc.insecure_channel('localhost:50051')
    server = GameServiceStub(channel)

    @classmethod
    def initial_state(cls, player_names):
        print(player_names)
        payload = StartPayload()
        payload.names.extend(player_names)
        return cls.server.start(payload)

    @classmethod
    def get_moves(cls, state):
        return cls.server.actions(state).actions

    @classmethod
    def perform_move(cls, state, move):
        payload = PerformPayload()
        payload.action.CopyFrom(move)
        payload.state.CopyFrom(state)

        return cls.server.perform(payload)
