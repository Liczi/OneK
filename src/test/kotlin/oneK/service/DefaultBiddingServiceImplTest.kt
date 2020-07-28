package oneK.service

import oneK.state.isAllCardsPlayed
import oneK.service.DefaultBiddingServiceImpl.performBid
import oneK.service.DefaultBiddingServiceImpl.performFold
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import oneK.testsuits.TestStateHolder
import oneK.testsuits.ThreePlayer
import oneK.testsuits.TwoPlayer

internal class DefaultBiddingServiceImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Bidding(TwoPlayer()) {

        @Test
        fun `should complete bidding with first player folding`() {
            val state = initialState
                .performBid(110)
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[1])
            assertTrue(state.isAllCardsPlayed())
        }

        @Test
        fun `should complete bidding with second player folding`() {
            val state = initialState
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[0])
            assertTrue(state.isAllCardsPlayed())
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Bidding(ThreePlayer()) {

        @Test
        fun `should complete bidding with first and second player folding`() {
            val state = initialState
                .performBid(110)
                .performBid(120)
                .performFold()
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[2])
            assertTrue(state.isAllCardsPlayed())
        }

        @Test
        fun `should complete bidding with second and third player folding`() {
            val state = initialState
                .performFold()
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[0])
            assertTrue(state.isAllCardsPlayed())
        }

        @Test
        fun `should skip a player who folded between bids`() {
            val state = initialState
                .performFold()
                .performBid(110)
                .performBid(120)
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[0])
            assertTrue(state.isAllCardsPlayed())
        }

        @Test
        fun `should skip two players who folded between bids`() {
            val state = initialState
                .performFold()
                .performBid(110)
                .performFold()

            assertThat(state.order.current().player).isEqualTo(players[2])
            assertTrue(state.isAllCardsPlayed())
        }
    }
}