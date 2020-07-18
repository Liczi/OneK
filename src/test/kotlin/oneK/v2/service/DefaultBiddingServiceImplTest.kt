package service

import oneK.v2.state.isBiddingCompleted
import oneK.v2.service.DefaultBiddingServiceImpl.performBid
import oneK.v2.service.DefaultBiddingServiceImpl.performFold
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testsuits.TestStateHolder
import testsuits.ThreePlayer
import testsuits.TwoPlayer

internal class DefaultBiddingServiceImplTest {
    //TODO add tests for above 120 bids
    @Nested
    inner class TwoPlayerTest : TestStateHolder.Bidding(TwoPlayer()) {

        @Test
        fun `should complete bidding with first player folding`() {
            val state = initialState
                .performBid(100)
                .performBid(110)
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[1])
            assertTrue(state.isBiddingCompleted())
        }

        @Test
        fun `should complete bidding with second player folding`() {
            val state = initialState
                .performBid(100)
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[0])
            assertTrue(state.isBiddingCompleted())
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Bidding(ThreePlayer()) {

        @Test
        fun `should complete bidding with first and second player folding`() {
            val state = initialState
                .performBid(100)
                .performBid(110)
                .performBid(120)
                .performFold()
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[2])
            assertTrue(state.isBiddingCompleted())
        }

        @Test
        fun `should complete bidding with second and third player folding`() {
            val state = initialState
                .performBid(100)
                .performFold()
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[0])
            assertTrue(state.isBiddingCompleted())
        }

        @Test
        fun `should skip a player who folded between bids`() {
            val state = initialState
                .performBid(100)
                .performFold()
                .performBid(110)
                .performBid(120)
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[0])
            assertTrue(state.isBiddingCompleted())
        }

        @Test
        fun `should skip two players who folded between bids`() {
            val state = initialState
                .performBid(100)
                .performFold()
                .performBid(110)
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[2])
            assertTrue(state.isBiddingCompleted())
        }
    }
}