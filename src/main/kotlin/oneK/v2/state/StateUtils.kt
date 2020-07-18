package oneK.v2.state

import oneK.deck.Card
import oneK.v2.variant.Variant

fun State.Bidding.allCardsPlayed(): Boolean =
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

fun State.Strife.allCardsPlayed(): Boolean = this.order.all { it.cards.isEmpty() }

fun State.Strife.isBoardFull(): Boolean = this.order.mapNotNull(Strifer::lastAction).isEmpty()

fun State.Strife.currentCardsUnordered(): List<Card> = this.order.mapNotNull { it.lastAction?.card }

fun State.Strife.firstCard(): Card? = this.order.firstOrNull { it.lastAction != null }?.lastAction?.card

fun State.Strife.accountForStriferConstraint(): Map<Strifer, Int> = TODO()
//    this.order.map {  }.mapValues { (strifer, points) ->
//        if (strifer.isConstrained)
//            if (this.bid > points) -this.bid else this.bid
//        else
//            points
//    }

fun State.Strife.addPointsAndClearBoard(): State.Strife {
    val winner = this.getWinner()
    val points = this.order.map { it.lastAction?.card?.figure?.value ?: 0 }.sum()
    return this.copy(
        order = this.order.map {
            it.copy(
                lastAction = null,
                points = it.points + if (it == winner) points else 0
            )
        }.withCurrent(winner)
    )
}

fun State.Strife.getWinner(): Strifer {
    val firstCard = this.firstCard()
    return this.order
        .filter { it.lastAction?.card?.color == firstCard?.color }
        .maxBy { it.lastAction?.card?.figure?.value ?: 0 }
        ?: error("Unable to determine winner")
}