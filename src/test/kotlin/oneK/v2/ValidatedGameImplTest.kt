import oneK.v2.GameFactory
import oneK.v2.action.BiddingAction
import oneK.v2.state.FoldingEffect
import testsuits.TwoPlayer
import oneK.v2.state.RepeatableOrder
import oneK.v2.state.State
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ValidatedGameImplTest : TwoPlayer() {
    private val game = GameFactory.default()

    @Test
    fun `should transition to bidding`() {
        val newState = game.start(State.Summary(RepeatableOrder.of(players)))

        val currentBidder = newState.biddersOrder.current()
        assertThat(currentBidder.player).isEqualTo(players[1])
        assertThat((newState.biddersOrder[0].lastAction as BiddingAction.Bid).amount).isEqualTo(100)
        assertNull(currentBidder.lastAction)
    }

//    TODO make work
    @Test
    fun `should transition to review`() {
        val newState = game.start(State.Summary(RepeatableOrder.of(players)))
            .let { game.bid(it, 110) }
            .let { game.bid(it, 120) }
            .let { game.fold(it) }

        val reviewState = (newState as FoldingEffect.ReviewTransition).state
        assertThat(reviewState.initialBid).isEqualTo(120)
    }
}