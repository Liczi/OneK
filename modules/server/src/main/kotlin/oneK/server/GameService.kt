package oneK.server

import oneK.ValidatedGame
import oneK.generator.ActionGenerator
import oneK.player.Player
import oneK.state.Action
import oneK.state.RepeatableOrder
import oneK.state.State
import java.util.UUID
import javax.inject.Singleton

@Singleton
class GameService(private val game: ValidatedGame, private val generator: ActionGenerator) {

    fun start(names: List<String>): State.Summary =
        State.Summary(RepeatableOrder.of(names.map { Player(it) }))

    fun restart(nameToUuid: List<Pair<String, String>>): State.Summary =
        State.Summary(RepeatableOrder.of(nameToUuid.map { (name, uuid) ->
            Player(
                name = name,
                uuid = UUID.fromString(uuid)
            )
        }))

    fun generate(state: State): List<Action> = generator.generate(state)

    fun perform(action: Action, state: State): State = game.perform(action, state)
}