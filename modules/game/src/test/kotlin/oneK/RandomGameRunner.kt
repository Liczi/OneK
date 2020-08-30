package oneK

import oneK.generator.ActionGenerator
import oneK.player.Player
import oneK.state.Action
import oneK.state.RepeatableOrder
import oneK.state.State
import oneK.variant.getVariantFor
import kotlin.math.roundToInt
import kotlin.system.measureNanoTime

private const val ONE_MILLION = 1000000

fun main() {
//    randomGame()
//    multipleRandomGames(1000)
}

fun randomGame(
    playersCount: Int = 2,
    stateInterceptor: (State) -> Unit = {},
    silent: Boolean = false
): Pair<Int, Long> {
    val variant = getVariantFor(playersCount)
    val game = GameFactory.default(variant)
    val generator = ActionGenerator(game.validator, variant)

    val players = (1..playersCount).map { Player("Player$it") }

    var turn = 0
    var state: State = State.Summary(RepeatableOrder.of(players))
    var actions: List<Action> = generator.generate(state)
    val timeNano = measureNanoTime {
        while (actions.isNotEmpty()) {
            stateInterceptor(state)
            val action = actions.random()
            state = game.perform(action, state)
            turn++
            actions = generator.generate(state)
        }
    }
    val timeMs = timeNano / ONE_MILLION
    if (!silent) print("Game ended after $turn turns, executed in ${timeMs}ms. Efficiency: ${turn / timeMs} [turns/millisecond]")
    return Pair(turn, timeNano)
}

fun multipleRandomGames(gamesToExecute: Int, playersCount: Int = 2) {
    val allTurns = mutableListOf<Int>()
    val allTimes = mutableListOf<Long>()

    for (i in 1..gamesToExecute) {
        println("Executing game $i/$gamesToExecute")
        val (turns, timeNano) = randomGame(playersCount, silent = true)
        allTurns.add(turns)
        allTimes.add(timeNano)
    }
    val efficiencies = allTurns.zip(allTimes).map { it.first.toDouble() * ONE_MILLION / it.second }
    print(
        "Average turns played: ${allTurns.average()}, average execution time: ${
            allTimes.map { it / ONE_MILLION }.average()
        }ms, average efficiency: ${efficiencies.average().roundToInt()}[turns/millisecond]"
    )
}
