package state

import oneK.v2.state.Bidder
import oneK.v2.state.RepeatableOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testsuits.TestPlayersHolder
import testsuits.TestStateHolder
import testsuits.ThreePlayer
import testsuits.TwoPlayer

internal class RepeatableOrderTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Bidding(TwoPlayer()) {
        @Test
        fun `should return next player for given condition`() {
            performTestForAvailableIndices(this.initialState.order)
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Bidding(ThreePlayer()) {
        @Test
        fun `should return next player for given condition`() {
            performTestForAvailableIndices(this.initialState.order)
        }
    }

    private fun TestPlayersHolder.performTestForAvailableIndices(initialOrder: RepeatableOrder<Bidder>) {
        this.players.indices
            .map { dummyReplaceAndNextUntil(initialOrder, it) }
            .forEachIndexed { index, newOrder ->
                assertThat(newOrder.current().player).isEqualTo(this.players[index])
            }
    }

    private fun TestPlayersHolder.dummyReplaceAndNextUntil(
        order: RepeatableOrder<Bidder>,
        nextUntilIndex: Int
    ): RepeatableOrder<Bidder> =
        order.replaceCurrentAndNextUntil(order.current()) { it.player == this.players[nextUntilIndex] }
}