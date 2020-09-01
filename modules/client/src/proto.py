import sys

sys.path.append("./generated")
import grpc

from generated.server_pb2_grpc import GameServiceStub
from generated.server_pb2 import StartPayload, PerformPayload
# from generated.action_pb2 import BiddingActionType, ReviewActionType, StrifeActionType, BID, FOLD, PICK, DISTRIBUTE, BID, CONFIRM, RESTART

class Server(object):
    channel = grpc.insecure_channel('localhost:50051')
    server = GameServiceStub(channel)

    @classmethod
    def initial_state(cls, player_names):
        payload = StartPayload()
        payload.names.extend(player_names)
        return cls.server.start(payload)

    @classmethod
    def restart(cls, state):
        return cls.server.restart(state)

    @classmethod
    def get_moves(cls, state):
        return cls.server.actions(state).actions

    @classmethod
    def perform_move(cls, state, move):
        payload = PerformPayload()
        payload.action.CopyFrom(move)
        payload.state.CopyFrom(state)

        return cls.server.perform(payload)


#     option allow_alias = true;

# def action_lightweight_string(action):
#     action_payload_type = action.WhichOneof('action')
#     if action_payload_type == 'bidding':
#
#     elif action_payload_type == 'review':
#         pass
#     elif action_payload_type == 'strife':
#         pass
#     else:
#         pass
# myproto_pb2.BiddingActionType.Name(
