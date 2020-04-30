package service

import oneK.v2.isBiddingCompleted
import oneK.v2.service.DefaultBiddingServiceImpl.performBid
import oneK.v2.service.DefaultBiddingServiceImpl.performFold
import oneK.v2.service.TwoPlayerBiddingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DefaultBiddingServiceImplTest : TwoPlayerBiddingTest() {

    //    TODO add nested class responsible for 3 players test - add reset function (in parent class)
//    TODO add more test cases for bidding stage
    @Test
    fun `should `() {
        val state = initialBiddingState
            .performBid(100)
            .performBid(110)
            .performFold()

        assertThat(state.bidders.current().hand.player).isEqualTo(players[0])
        assertTrue(state.isBiddingCompleted())
    }
}