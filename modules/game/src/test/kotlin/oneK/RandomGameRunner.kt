package oneK

import oneK.generator.ActionGenerator
import oneK.player.Player
import oneK.state.Action
import oneK.state.RepeatableOrder
import oneK.state.State
import oneK.variant.getVariantFor
import kotlin.system.measureTimeMillis

fun main() {
    val variant = getVariantFor(2)
    val game = GameFactory.default(variant)
    val generator = ActionGenerator(game.validator, variant)

    val players = arrayListOf("Zbyszek", "Zdzichu").map { Player(it) }

    var turn = 0
    var state: State = State.Summary(RepeatableOrder.of(players))
    var actions: List<Action> = generator.generate(state)
    val timeMs = measureTimeMillis {
        while (actions.isNotEmpty()) {
            val action = actions.random()
            state = game.perform(action, state)
            turn++
            actions = generator.generate(state)
        }
    }
    print("Game ended after $turn turns, executed in ${timeMs}ms. Efficiency: ${turn / (timeMs)} [turns/millisecond]")
}
