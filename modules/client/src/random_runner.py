import sys

sys.path.append("./generated")
import grpc
from generated.server_pb2_grpc import GameServiceStub
from generated.server_pb2 import StartPayload

channel = grpc.insecure_channel('localhost:50051')
server = GameServiceStub(channel)

payload = StartPayload()
payload.names.extend(["Zbyszek", "Mietek"])
print(payload)

response = server.start(payload)
print(response)

