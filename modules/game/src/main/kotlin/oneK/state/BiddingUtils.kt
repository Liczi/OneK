package oneK.state

import oneK.deck.Card
import oneK.variant.Variant

internal fun State.Bidding.isAllCardsPlayed(): Boolean =
    this.order
        .map(Bidder::lastAction)
        .filterNot { it is Action.Bidding.Fold }
        .size <= 1

internal fun State.Bidding.currentBid(): Int =
    this.order
        .asSequence()
        .map(Bidder::lastAction)
        .filterIsInstance<Action.Bidding.Bid>()
        .map(Action.Bidding.Bid::amount)
        .max() ?: 0

internal fun isValidBid(
    bid: Int,
    currentBid: Int,
    cards: Set<Card>,
    variant: Variant
): Boolean {
    return bid > currentBid
            && variant.canBid(cards, bid)
            && bid.isMultiplicityOfTen()
            && bid <= variant.getUpperBidThreshold()
}

private fun Int.isMultiplicityOfTen() = this % 10 == 0
internal fun Int.isValidStep(currentBid: Int, variant: Variant) = this - currentBid <= variant.getMaxBidStep()