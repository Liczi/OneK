package oneK.service

import oneK.asCardSet
import oneK.deck.Card
import oneK.player.Player
import oneK.service.DefaultReviewServiceImpl.performChangeBid
import oneK.service.DefaultReviewServiceImpl.performConfirm
import oneK.service.DefaultReviewServiceImpl.performDistributeCards
import oneK.service.DefaultReviewServiceImpl.performPickTalon
import oneK.state.Choice
import oneK.state.State
import oneK.testsuits.TestStateHolder
import oneK.testsuits.ThreePlayer
import oneK.testsuits.TwoPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultReviewServiceImplTest {

    @Nested
    inner class TwoPlayerTest : TestStateHolder.Review(TwoPlayer()) {

        @Test
        fun `should properly perform pick talon`() {
            (0..1).forEach {
                assertTalonPickedProperly(it, initialState)
            }
        }

        @Test
        fun `should properly perform distribute cards`() {
            val toGive = players
                .filter { it != players[0] }
                .zip("9S".asCardSet()).toMap()
            val newState = initialState
                .performPickTalon(0)
                .performDistributeCards(toGive)

            assertDistributedCardsProperly(newState, toGive)
        }

        @Test
        fun `should assign initial bid if bid changed`() {
            val newState = initialState.performConfirm()

            assertThat(newState.bid)
                .isEqualTo(100)
        }

        @Test
        fun `should assign changed bid if bid changed`() {
            val newState = initialState
                .performChangeBid(120)
                .performConfirm()

            assertThat(newState.bid)
                .isEqualTo(120)
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Review(ThreePlayer()) {

        @Test
        fun `should properly perform pick talon`() {
            assertTalonPickedProperly(0, initialState)
        }

        @Test
        fun `should properly perform distribute cards`() {
            val toGive = players
                .filter { it != players[0] }
                .zip("9S,9C".asCardSet()).toMap()
            val newState = initialState
                .performPickTalon(0)
                .performDistributeCards(toGive)

            assertDistributedCardsProperly(newState, toGive)
        }
    }

    private fun assertDistributedCardsProperly(
        newState: State.Review,
        toGive: Map<Player, Card>
    ) {
        toGive.forEach { (player, card) ->
            assertThat(newState.order.firstOrNull { it.player == player }?.cards)
                .contains(card)
        }
        assertThat(newState.talon)
            .isInstanceOf(Choice.Taken::class.java)
        assertThat(newState.order)
            .allMatch { it.cards.size == newState.order.current().cards.size }
        assertThat(newState.toGive)
            .isNotNull
    }

    private fun assertTalonPickedProperly(talonInd: Int, initialState: State.Review) {
        val newState = initialState.performPickTalon(talonInd)
        val talonCards = initialState.talon.take(talonInd).value
        assertThat((newState.talon as Choice.Taken).value)
            .containsExactlyElementsOf(talonCards)
        assertThat(newState.order.current().cards)
            .contains(*talonCards.toTypedArray())
    }
}