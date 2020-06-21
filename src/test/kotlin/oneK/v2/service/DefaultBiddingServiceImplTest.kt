package service

import oneK.v2.isBiddingCompleted
import oneK.v2.service.DefaultBiddingServiceImpl.performBid
import oneK.v2.service.DefaultBiddingServiceImpl.performFold
import oneK.v2.service.TestPlayersHolder
import oneK.v2.service.TestStateHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DefaultBiddingServiceImplTest : TestStateHolder.Bidding(TestPlayersHolder.TwoPlayer()) {

    //    TODO add nested class responsible for 3 players test - add reset function (in parent class)
    //    TODO add more test cases for bidding stage - test ValidatedGame
    @Test
    fun `should complete bidding with first player folding`() {
        val state = initialBiddingState
            .performBid(100)
            .performBid(110)
            .performFold()

        assertThat(state.biddersOrder.current().player).isEqualTo(getPlayers()[1])
        assertTrue(state.isBiddingCompleted())
    }

    @Test
    fun `should complete bidding with second player folding`() {
        val state = initialBiddingState
            .performBid(100)
            .performFold()

        assertThat(state.biddersOrder.current().player).isEqualTo(getPlayers()[0])
        assertTrue(state.isBiddingCompleted())
    }
}