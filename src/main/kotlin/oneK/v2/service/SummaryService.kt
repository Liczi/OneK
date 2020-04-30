package oneK.v2.service

import oneK.deck.Card
import oneK.v2.Hand
import oneK.v2.rotate
import oneK.v2.splitCardsToEqualSets
import oneK.v2.state.Bidder
import oneK.v2.state.RepeatableOrder
import oneK.v2.state.State
import oneK.v2.variant.TALON_SIZE
import oneK.v2.variant.Variant

interface SummaryService {
    fun State.Summary.performStart(deck: List<Card>, variant: Variant): State.Bidding
}

internal object DefaultSummaryServiceImpl : SummaryService {
    override fun State.Summary.performStart(deck: List<Card>, variant: Variant): State.Bidding {
        val talon = variant.getTalonCards(deck.take(TALON_SIZE))
        val playersHands = deck.drop(TALON_SIZE).splitCardsToEqualSets(this.order.size)
        val bidders = playersHands
            .zip(this.order.rotate())
            .map { (cards, player) -> Bidder(Hand(cards, player)) }

        return State.Bidding(RepeatableOrder(bidders), talon)
    }
}