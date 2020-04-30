package service

import oneK.player.Player
import oneK.v2.getClassicDeck
import oneK.v2.service.TwoPlayerBiddingTest
import oneK.v2.service.DefaultSummaryServiceImpl.performStart
import oneK.v2.state.State
import oneK.v2.variant.DefaultVariant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultSummaryServiceImplTest : TwoPlayerBiddingTest() {

    //    TODO add nested class responsible for 3 players test
//    TODO sort out initial players order - in tests we may assume we start with the given order not to confuse
    @Test
    fun `should properly perform start`() {
        val summaryState = State.Summary(players)
        val deck = getClassicDeck().take(8)

        val biddingState = summaryState.performStart(deck, DefaultVariant())

        assertThat(biddingState).isEqualTo(initialBiddingState)
    }

}