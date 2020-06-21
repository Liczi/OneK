package oneK.v2.service

import oneK.deck.Card
import oneK.v2.state.Bidder
import oneK.v2.state.RepeatableOrder
import oneK.v2.state.State
import oneK.v2.toCardSet

interface TestStateHolder {
    abstract class Bidding(private val playersHolder: TestPlayersHolder) : TestPlayersHolder by playersHolder {

        private val playerCards = this.getPlayers().zip(listOf("JS,JC", "JD,JH", "QS,QC"))
        private val talon = if (this.getPlayers().size == 2) listOf("9S,9C", "9D,9H") else listOf("9S,9C,9D")
        protected val initialBiddingState = State.Bidding(
            RepeatableOrder.of(
                playerCards.map { (player, cardsString) -> Bidder(cardsString.toCardSet(), player) }
            ),
            talon.map(String::toCardSet)
        )

        protected fun getDeck(): List<Card> =
            (playerCards.map { it.second } + talon)
                .map(String::toCardSet)
                .flatten()
                .sortedBy { it.figure.value * 100 + it.color.value }

    }
}