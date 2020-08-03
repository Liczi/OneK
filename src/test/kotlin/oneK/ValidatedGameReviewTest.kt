package oneK

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure
import oneK.state.FoldingEffect.NoTransition
import oneK.state.FoldingEffect.ReviewTransition
import oneK.state.State
import oneK.state.Strifer
import oneK.testsuits.TestStateHolder
import oneK.testsuits.ThreePlayer
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class ValidatedGameReviewTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Review(TwoPlayer()) {

        @Test
        fun `should pick talon and confirm bid`() {
            val state = game.pickTalon(initialState, 1)
                .let { game.distributeCards(it, mapOf(players[1] to (Figure.NINE of Color.DIAMONDS))) }
                .let { game.changeBid(it, 120) }
                .let { game.confirm(it) }

            val addedCardsStack = Stack<Card>()
                .also { it.push(Figure.NINE of Color.DIAMONDS) }
                .also { it.push(Figure.NINE of Color.HEARTS) }
            assertThat(state)
                .isEqualToComparingFieldByFieldRecursively(
                    State.Strife(
                        order = initialState.order
                            .map { Strifer(it.cards + addedCardsStack.pop(), it.player) }
                            .replaceCurrent { it.copy(isConstrained = true) },
                        bid = 120,
                        ranking = initialState.order.associate { it.player to 0 }
                    )
                )
        }

        @Test
        fun `should not allow restart`() {
            assertThrows<IllegalStateException> {
                game.restart(initialState)
            }
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Review(ThreePlayer(), listOf("9H", "JC", "JD")) {

        @Test
        fun `should not allow bidding over 120 without triumph`() {
            val state = game.pickTalon(initialState, 0)
                .let {
                    game.distributeCards(
                        it,
                        mapOf(
                            players[1] to (Figure.NINE of Color.DIAMONDS),
                            players[2] to (Figure.NINE of Color.HEARTS)
                        )
                    )
                }
            assertThrows<IllegalStateException> { state.let { game.changeBid(it, 130) } }
        }

        @Test
        fun `should allow restart`() {
            val state = game.pickTalon(initialState, 0)
                .let { game.restart(it) }
                .let { game.fold(it) as NoTransition }
                .let { game.fold(it.state) as ReviewTransition }


            val transitionState = state.state
            assertThat(transitionState)
                .isEqualToIgnoringGivenFields(initialState, "order", "talon")
            assertThat(transitionState.order.map { it.player })
                .isEqualTo(initialState.order.map { it.player })
            assertThat(transitionState.order.map { it.cards })
                .isNotEqualTo(initialState.order.map { it.cards })
            assertThat(transitionState.order.current().player)
                .isEqualTo(initialState.order.current().player)
        }
    }
}
