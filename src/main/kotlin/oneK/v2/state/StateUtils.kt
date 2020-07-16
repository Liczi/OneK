package oneK.v2.state

import oneK.deck.Card
import oneK.v2.variant.Variant

fun State.Bidding.isBiddingCompleted() =
    this.order
        .map(Bidder::lastAction)
        .filterNot { it is BiddingAction.Fold }
        .size <= 1

fun State.Bidding.currentBid(): Int =
    this.order
        .asSequence()
        .map(Bidder::lastAction)
        .filterIsInstance<BiddingAction.Bid>()
        .map(BiddingAction.Bid::amount)
        .max() ?: 0

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

fun State.Strife.currentCardsUnordered(): List<Card> =
    this.order.mapNotNull { it.lastAction?.card }

fun State.Strife.firstCard(): Card? = this.order.firstOrNull { it.lastAction != null }?.lastAction?.card
