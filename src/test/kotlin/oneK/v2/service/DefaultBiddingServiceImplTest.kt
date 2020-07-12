package service

import oneK.v2.isBiddingCompleted
import oneK.v2.service.DefaultBiddingServiceImpl.performBid
import oneK.v2.service.DefaultBiddingServiceImpl.performFold
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import testsuits.TestStateHolder
import testsuits.TwoPlayer

internal class DefaultBiddingServiceImplTest : TestStateHolder.Bidding(TwoPlayer()) {

    //    TODO add nested class responsible for 3 players test - add reset function (in parent class)
    //    TODO add more test cases for bidding stage - test ValidatedGame
    @Test
    fun `should complete bidding with first player folding`() {
        val state = initialBiddingState
            .performBid(100)
            .performBid(110)
            .performFold()

        assertThat(state.order.current().player).isEqualTo(players[1])
        assertTrue(state.isBiddingCompleted())
    }

    @Test
    fun `should complete bidding with second player folding`() {
        val state = initialBiddingState
            .performBid(100)
            .performFold()

        assertThat(state.order.current().player).isEqualTo(players[0])
        assertTrue(state.isBiddingCompleted())
    }
}