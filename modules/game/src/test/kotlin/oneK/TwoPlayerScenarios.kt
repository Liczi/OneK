package oneK

import oneK.state.FoldingEffect
import oneK.state.PlayingEffect
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TwoPlayerScenarios : TestStateHolder.Bidding(
    TwoPlayer(),
    listOf("AS, KH, JD", "AD, TC, KD"),
    listOf("AH, QH", "AC, QD")
) {

    @Test
    fun `first player should win with first talon`() {
        val summary = game.bid(initialState, 110)
            .let { game.bid(it, 120) }
            .let { (game.fold(it) as FoldingEffect.ReviewTransition).state }
            .let { game.pickTalon(it, 0) }
            .let { game.distributeCards(it, mapOf(players[1] to "JD".asCard())) }
            .let { game.changeBid(it, 130) }
            .let { game.confirm(it) }
            .let { (game.play(it, "AS".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "JD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AH".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "KD".asCard()) as PlayingEffect.NoTransition).state }
            .let { game.triumph(it, "QH".asCard()) }
            .let { (game.play(it, "TC".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "KH".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AD".asCard()) as PlayingEffect.SummaryTransition).state }

        assertThat(summary.ranking)
            .isEqualTo(mapOf(players[0] to 130, players[1] to 0))
    }

    @Test
    fun `first player should lose with second talon`() {
        val summary = game.bid(initialState.copy(ranking = mapOf(players[0] to 120, players[1] to 0)), 110)
            .let { game.bid(it, 120) }
            .let { (game.fold(it) as FoldingEffect.ReviewTransition).state }
            .let { game.pickTalon(it, 1) }
            .let { game.distributeCards(it, mapOf(players[1] to "QD".asCard())) }
            .let { game.confirm(it) }
            .let { (game.play(it, "AS".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "TC".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "JD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AD".asCard()) as PlayingEffect.NoTransition).state }
            .let { game.triumph(it, "QD".asCard()) }
            .let { (game.play(it, "KH".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "KD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AC".asCard()) as PlayingEffect.SummaryTransition).state }

        assertThat(summary.ranking)
            .isEqualTo(mapOf(players[0] to 0, players[1] to 120))
    }

    @Test
    fun `second player should lose with first talon`() {
        val summary = game.bid(initialState.copy(ranking = mapOf(players[0] to 0, players[1] to 100)), 110)
            .let { (game.fold(it) as FoldingEffect.ReviewTransition).state }
            .let { game.pickTalon(it, 0) }
            .let { game.distributeCards(it, mapOf(players[0] to "QH".asCard())) }
            .let { game.confirm(it) }
            .let { (game.play(it, "AD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "JD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AH".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "QH".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "TC".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "KH".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "KD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AS".asCard()) as PlayingEffect.SummaryTransition).state }

        assertThat(summary.ranking)
            .isEqualTo(mapOf(players[0] to 0, players[1] to 0))
    }

    @Test
    fun `second player should win with second talon`() {
        val summary = game.bid(initialState, 110)
            .let { (game.fold(it) as FoldingEffect.ReviewTransition).state }
            .let { game.pickTalon(it, 1) }
            .let { game.distributeCards(it, mapOf(players[0] to "TC".asCard())) }
            .let { game.changeBid(it, 130) }
            .let { game.confirm(it) }
            .let { (game.play(it, "AD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "JD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AC".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "TC".asCard()) as PlayingEffect.NoTransition).state }
            .let { game.triumph(it, "QD".asCard()) }
            .let { (game.play(it, "KH".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "KD".asCard()) as PlayingEffect.NoTransition).state }
            .let { (game.play(it, "AS".asCard()) as PlayingEffect.SummaryTransition).state }

        assertThat(summary.ranking)
            .isEqualTo(mapOf(players[0] to 0, players[1] to 130))
    }
}