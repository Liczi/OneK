package oneK.server.converter

import oneK.state.State
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.junit.jupiter.api.Test

internal class ConverterTest : TestStateHolder.Summary(TwoPlayer()) {

    @Test
    fun `should convert state properly along play`() {

//        start with this.initialState
        val actions = listOf<(State) -> State>(
            { game.start(it as State.Summary) },
            { game.bid(it as State.Bidding, 110) },
            { game.fold(it as State.Bidding).state() },
            { game.pickTalon(it as State.Review, 0) },
            { state ->
                game.distributeCards(
                    state as State.Review,
                    mapOf(state.order.first { it != state.order.current() }.player to state.order.current().cards.first())
                )
            },
            { game.changeBid(it as State.Review, 120) }
        ).scan(this.initialState as State) { acc, play -> play(acc) }

        actions.forEach { convertAndAssertEqual(it) }
    }
}