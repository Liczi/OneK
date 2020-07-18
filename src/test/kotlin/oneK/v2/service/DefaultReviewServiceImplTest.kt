package service

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.service.DefaultReviewServiceImpl.performChangeBid
import oneK.v2.service.DefaultReviewServiceImpl.performConfirm
import oneK.v2.service.DefaultReviewServiceImpl.performDistributeCards
import oneK.v2.service.DefaultReviewServiceImpl.performPickTalon
import oneK.v2.service.DefaultReviewServiceImpl.performRestart
import oneK.v2.state.Choice
import oneK.v2.state.State
import oneK.v2.toCardSet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import testsuits.TestStateHolder
import testsuits.ThreePlayer
import testsuits.TwoPlayer

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
        fun `should properly perform restart`() {
            val newState = initialState.performRestart()

            assertRestartedProperly(newState, initialState, 0..1)
        }

        @Test
        fun `should properly perform distribute cards`() {
            val toGive = players
                .filter { it != players[0] }
                .zip("9S".toCardSet()).toMap()
            val newState = initialState
                .performPickTalon(0)
                .performDistributeCards(toGive)

            assertDistributedCardsProperly(newState, toGive)
        }

        @Test
        fun `should assign initial bid if bid changed`() {
            val newState = initialState.performConfirm()

            assertThat(newState.bid).isEqualTo(100)
        }

        @Test
        fun `should assign changed bid if bid changed`() {
            val newState = initialState
                .performChangeBid(120)
                .performConfirm()

            assertThat(newState.bid).isEqualTo(120)
        }
    }

    @Nested
    inner class ThreePlayerTest : TestStateHolder.Review(ThreePlayer()) {

        @Test
        fun `should properly perform pick talon`() {
            assertTalonPickedProperly(0, initialState)
        }

        @Test
        fun `should properly perform restart`() {
            val newState = initialState.performRestart()

            assertRestartedProperly(newState, initialState, 0..0)
        }

        @Test
        fun `should properly perform distribute cards`() {
            val toGive = players
                .filter { it != players[0] }
                .zip("9S,9C".toCardSet()).toMap()
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
            assertThat(newState.order.firstOrNull { it.player == player }?.cards).contains(card)
        }
        assertThat(newState.talon).isInstanceOf(Choice.Taken::class.java)
        assertThat(newState.order).allMatch { it.cards.size == newState.order.current().cards.size }
        assertThat(newState.toGive).isNotNull
    }

    private fun assertRestartedProperly(newState: State.Bidding, initialState: State.Review, talonRange: IntRange) {
        (talonRange).forEach {
            assertThat(newState.talon[it])
                .containsExactlyInAnyOrderElementsOf(initialState.talon.take(it).value)
        }
        assertThat(newState.order.map { it.cards }).isEqualTo(initialState.order.map { it.cards })
        assertThat(newState.order.map { it.cards }.flatten())
            .containsExactlyInAnyOrderElementsOf(initialState.order.map { it.cards }.flatten())
        assertThat(newState.order.map { it.player }).isEqualTo(initialState.order.map { it.player })
    }

    private fun assertTalonPickedProperly(talonInd: Int, initialState: State.Review) {
        val newState = initialState.performPickTalon(talonInd)
        val talonCards = initialState.talon.take(talonInd).value
        assertThat((newState.talon as Choice.Taken).value)
            .containsExactlyElementsOf(talonCards)
        assertThat(newState.order.current().cards).contains(*talonCards.toTypedArray())
    }
}