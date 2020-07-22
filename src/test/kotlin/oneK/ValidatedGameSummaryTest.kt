package oneK

import oneK.state.BiddingAction
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class ValidatedGameSummaryTest : TestStateHolder.Summary(TwoPlayer()) {

    private val game = GameFactory.default()

//    TODO add test cases
    @Test
    fun `should transition to bidding`() {
        val newState = game.start(initialState)

        val currentBidder = newState.order.current()
        assertThat(currentBidder.player).isEqualTo(players[1])
        assertThat((newState.order[0].lastAction as BiddingAction.Bid).amount)
            .isEqualTo(100)
        assertNull(currentBidder.lastAction)
    }
}