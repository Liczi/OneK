package oneK

import oneK.deck.Card
import oneK.state.PlayingEffect
import oneK.state.RepeatableOrder
import oneK.state.State
import oneK.testsuits.TestStateHolder
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ValidatedGameStrifeTest {

    private val game = GameFactory.default()

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Strife(TwoPlayer()) {

        @Test
        fun `should transition to summary with valid state`() {
            val state = initialState
                .let { (game.play(it, Card.fromString('J', 'S')) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, Card.fromString('J', 'D')) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, Card.fromString('J', 'C')) as PlayingEffect.NoTransition).state }
                .let { (game.play(it, Card.fromString('J', 'H')) as PlayingEffect.SummaryTransition).state }

            assertThat(state).isEqualToComparingFieldByField(State.Summary(
                order = RepeatableOrder.of(listOf(players[1], players[0])),
                ranking = mapOf(players[0] to -100, players[1] to 0)
            ))
//                .let { game.play(it, Card.fromString('J', 'S')) }
//                .let { game.play(it, Card.fromString('J', 'S')) }
//                .let { game.play(it, Card.fromString('J', 'S')) }

        }

//        TODO add tests for changing the order of players between strifes, counting intermediate points (+triumphs), playing cards off color - do they change order ?
    }
}