package oneK.service

import oneK.deck.Card
import oneK.splitCardsToEqualSets
import oneK.state.Bidder
import oneK.state.State
import oneK.variant.Variant

interface SummaryService {
    fun State.Summary.performStart(deck: List<Card>, variant: Variant): State.Bidding
}

internal object DefaultSummaryServiceImpl : SummaryService {

    override fun State.Summary.performStart(deck: List<Card>, variant: Variant): State.Bidding {
        val talonSize = variant.getTalonCardsQuantity()
        val talon = variant.getTalonCards(deck.take(talonSize))
        val playersHands = deck.drop(talonSize).splitCardsToEqualSets(this.order.size)
        val bidders = this.order
            .zip(playersHands)
            .map { (player, cards) -> Bidder(cards, player) }

        return State.Bidding(
            order = bidders,
            talon = talon,
            ranking = this.ranking
        )
    }
}