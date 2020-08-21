package oneK.server

import io.grpc.stub.StreamObserver
import oneK.GameServiceGrpc
import oneK.State
import oneK.ValidatedGame
import javax.inject.Singleton

@Singleton
class GameEndpoint(private val game: ValidatedGame) : GameServiceGrpc.GameServiceImplBase() {

    override fun perform(request: ActionOuterClass.Action?, responseObserver: StreamObserver<State>?) {
        super.perform(request, responseObserver)
    }
}