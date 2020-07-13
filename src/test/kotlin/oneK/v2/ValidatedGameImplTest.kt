import oneK.v2.GameFactory
import oneK.v2.state.BiddingAction
import oneK.v2.state.FoldingEffect
import testsuits.TwoPlayer
import oneK.v2.state.RepeatableOrder
import oneK.v2.state.State
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ValidatedGameImplTest {
    private val game = GameFactory.default()

    @Nested
    inner class TwoPlayerTest : TwoPlayer() {
        @Test
        fun `should transition to bidding`() {
            val newState = game.start(State.Summary(RepeatableOrder.of(players)))

            val currentBidder = newState.order.current()
            assertThat(currentBidder.player).isEqualTo(players[1])
            assertThat((newState.order[0].lastAction as BiddingAction.Bid).amount).isEqualTo(100)
            assertNull(currentBidder.lastAction)
        }

        @Test
        fun `should transition to review`() {
            val biddingState = game.start(State.Summary(RepeatableOrder.of(players)))
            val newState = biddingState
                .let { game.bid(it, 110) }
                .let { game.bid(it, 120) }
                .let { game.fold(it) }

            val reviewState = (newState as FoldingEffect.ReviewTransition).state
            assertThat(reviewState.initialBid).isEqualTo(120)
            assertThat(reviewState.order.current().player).isEqualTo(players[0])
            assertThat(reviewState.talon.take(0).value).containsExactlyInAnyOrderElementsOf(biddingState.talon[0])
            assertThat(reviewState.talon.take(1).value).containsExactlyInAnyOrderElementsOf(biddingState.talon[1])
            assertThat(reviewState.order.flatMap { it.cards })
                .containsExactlyInAnyOrderElementsOf(biddingState.order.flatMap { it.cards })
        }
    }

//    TODO add 3 Player test
}