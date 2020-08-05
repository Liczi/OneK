package oneK

import oneK.state.PlayingEffect
import oneK.state.RepeatableOrder
import oneK.state.State
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ValidatedGameStrifeTest {

//    TODO add case when player is disallowed to play card off-color

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Strife(TwoPlayer()) {

        @Test
        fun `should transition to summary with valid state`() {
            val state = initialState.copy(ranking = mapOf(players[0] to 200, players[1] to 0))
                .let { (game.play(it, "JS".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "JD".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "JC".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "JH".asCard()) as PlayingEffect.SummaryTransition).state }

            assertThat(state)
                .isEqualToComparingFieldByField(
                    State.Summary(
                        order = RepeatableOrder.of(listOf(players[1], players[0])),
                        ranking = mapOf(players[0] to 100, players[1] to 0)
                    )
                )
        }
    }

    @Nested
    inner class TwoPlayerTriumphTest : TestStateHolder.Strife(TwoPlayer(), listOf("QH, KH, 9H", "AH, AC, 9C")) {

        @Test
        fun `should switch players correctly`() {
            val state = initialState.copy(ranking = mapOf(players[0] to 110, players[1] to 0))
                .let { (game.play(it, "KH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "AH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "AC".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "QH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "9C".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "9H".asCard()) as PlayingEffect.SummaryTransition).state }

            assertThat(state)
                .isEqualToComparingFieldByField(
                    State.Summary(
                        order = RepeatableOrder.of(listOf(players[1], players[0])),
                        ranking = mapOf(players[0] to 10, players[1] to 30)
                    )
                )
        }

        @Test
        fun `should use triumph card correctly`() {
            val state = initialState
                .let { game.triumph(it, "QH".asCard()) }
                .let { (game.play(it, "AH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "AC".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "KH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "9H".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "9C".asCard()) as PlayingEffect.SummaryTransition).state }

            assertThat(state)
                .isEqualToComparingFieldByField(
                    State.Summary(
                        order = RepeatableOrder.of(listOf(players[1], players[0])),
                        ranking = mapOf(players[0] to 100, players[1] to 10)
                    )
                )
        }

        @Test
        fun `should not win with off color card`() {
            val state = initialState.copy(ranking = mapOf(players[0] to 90, players[1] to 0))
                .let { (game.play(it, "KH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "AH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "9C".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "QH".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "AC".asCard()) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, "9H".asCard()) as PlayingEffect.SummaryTransition).state }

            assertThat(state)
                .isEqualToComparingFieldByField(
                    State.Summary(
                        order = RepeatableOrder.of(listOf(players[1], players[0])),
                        ranking = mapOf(players[0] to 0, players[1] to 30)
                    )
                )
        }
    }
}