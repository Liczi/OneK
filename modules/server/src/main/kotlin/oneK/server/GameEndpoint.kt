package oneK.server

import io.grpc.stub.StreamObserver
import io.micronaut.context.annotation.Value
import oneK.proto.Actions
import oneK.proto.GameServiceGrpc
import oneK.proto.PerformPayload
import oneK.proto.StartPayload
import oneK.proto.state.State
import oneK.server.converter.toModel
import oneK.server.converter.toProtoMessage
import javax.inject.Singleton

@Singleton
class GameEndpoint(
    private val gameService: GameService,
    @Value("\${game.variant.players}") private val playersCount: String
) : GameServiceGrpc.GameServiceImplBase() {

    override fun perform(request: PerformPayload, responseObserver: StreamObserver<State>) {
        val action = request.action.toModel()
        val state = request.state.toModel()
        val newState = gameService.perform(action, state)
        responseObserver.onNext(newState.toProtoMessage())
        responseObserver.onCompleted()
    }

    override fun actions(request: State, responseObserver: StreamObserver<Actions>) {
        val actions = gameService.generate(request.toModel())
        val response = Actions.newBuilder()
            .addAllActions(actions.map { it.toProtoMessage() })
            .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun start(request: StartPayload, responseObserver: StreamObserver<State>) {
        val names = request.namesList.toList()
        if (playersCount.toInt() != names.size)
            responseObserver.onError(IllegalArgumentException("Couldn't create new game for $names. This server handles $playersCount-player games."))
        val state = gameService.start(names)
        responseObserver.onNext(state.toProtoMessage())
        responseObserver.onCompleted()
    }

    override fun restart(request: State, responseObserver: StreamObserver<State>) {
        val nameToUuid = request.summary.orderList.map { Pair(it.name, it.uuid) }
        val state = gameService.restart(nameToUuid)
        responseObserver.onNext(state.toProtoMessage())
        responseObserver.onCompleted()
    }
}
