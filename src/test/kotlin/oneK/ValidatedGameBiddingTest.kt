package oneK

import oneK.state.FoldingEffect
import oneK.testsuits.TestStateHolder
import oneK.testsuits.ThreePlayer
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal open class ValidatedGameBiddingTest {

    @Nested
    inner class DefaultCardsTest : TestStateHolder.Bidding(TwoPlayer()) {

        @Test
        fun `should transition to review with valid state`() {
            val newState = initialState
                .let { game.bid(it, 110) }
                .let { game.bid(it, 120) }
                .let { game.fold(it) }

            val reviewState = (newState as FoldingEffect.ReviewTransition).state
            assertThat(reviewState.initialBid)
                .isEqualTo(120)
            assertThat(reviewState.order.current().player)
                .isEqualTo(players[0])
            assertThat(reviewState.talon.take(0).value)
                .containsExactlyInAnyOrderElementsOf(initialState.talon[0])
            assertThat(reviewState.talon.take(1).value)
                .containsExactlyInAnyOrderElementsOf(initialState.talon[1])
            assertThat(reviewState.order.flatMap { it.cards })
                .containsExactlyInAnyOrderElementsOf(initialState.order.flatMap { it.cards })
        }
    }

    @Nested
    inner class UnlimitedBiddingCardsTest :
        TestStateHolder.Bidding(ThreePlayer(), listOf("KC, QC, AC", "9C, 9D, 9H", "KD, QD, AD")) {

        @Test
        fun `should allow bid above 120 and transition to review`() {
            val newState = initialState
                .let { game.bid(it, 110) }
                .let { game.bid(it, 120) }
                .let { game.bid(it, 130) }
                .let { (game.fold(it) as FoldingEffect.NoTransition).state }
                .let { game.bid(it, 140) }
                .let { game.bid(it, 150) }
                .let { game.bid(it, 160) }
                .let { game.fold(it) }

            val reviewState = (newState as FoldingEffect.ReviewTransition).state
            assertThat(reviewState.initialBid)
                .isEqualTo(160)
            assertThat(reviewState.order.current().player)
                .isEqualTo(players[2])
        }

        @Test
        fun `should transition to review`() {
            val newState = initialState
                .let { (game.fold(it) as FoldingEffect.NoTransition).state }
                .let { game.fold(it) }

            val reviewState = (newState as FoldingEffect.ReviewTransition).state
            assertThat(reviewState.initialBid)
                .isEqualTo(100)
            assertThat(reviewState.order.current().player)
                .isEqualTo(players[0])
        }
    }
}