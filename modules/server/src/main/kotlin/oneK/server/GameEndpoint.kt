package oneK.server

import io.grpc.stub.StreamObserver
import io.micronaut.context.annotation.Value
import oneK.proto.GameServiceGrpc
import oneK.proto.PerformPayload
import oneK.proto.StartPayload
import oneK.proto.action.Action
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

    override fun actions(request: State, responseObserver: StreamObserver<Action>) {
        val actions = gameService.generate(request.toModel())
        actions.forEach { responseObserver.onNext(it.toProtoMessage()) }
        responseObserver.onCompleted()
    }

    override fun start(request: StartPayload, responseObserver: StreamObserver<State>) {
        val names = request.namesList.toList()
        if (playersCount.toInt() != names.size)
            responseObserver.onError(IllegalArgumentException("Couldn't create new game for $names. This server handles $playersCount-player games."))
        val state = gameService.startGame(names)
        responseObserver.onNext(state.toProtoMessage())
        responseObserver.onCompleted()
    }
}
