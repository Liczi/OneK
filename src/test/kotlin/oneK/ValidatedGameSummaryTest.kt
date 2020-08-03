package oneK

import oneK.state.BiddingAction
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ValidatedGameSummaryTest : TestStateHolder.Summary(TwoPlayer()) {

    @Test
    fun `should transition to bidding`() {
        val newState = game.start(initialState)

        val currentBidder = newState.order.current()
        assertThat(currentBidder.player)
            .isEqualTo(players[1])
        assertThat((newState.order[0].lastAction as BiddingAction.Bid).amount)
            .isEqualTo(100)
        assertNull(currentBidder.lastAction)
    }

    @Test
    fun `should not allow start if game is finished`() {
        assertThrows<IllegalStateException> {
            game.start(
                initialState.copy(
                    ranking = initialState.ranking + mapOf(players[1] to 1000)
                )
            )
        }
    }
}