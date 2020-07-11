package oneK.v2.validation

import oneK.deck.Card
import oneK.v2.variant.Variant

fun isValidBid(
    bid: Int,
    currentBid: Int,
    cards: Set<Card>,
    variant: Variant
): Boolean {
    return bid > currentBid
            && variant.canBid(cards, bid)
            && bid % 10 == 0
            && bid - currentBid <= variant.getMaxBidStep() // TODO min bid state ???
            && bid <= variant.getUpperBidThreshold()
}