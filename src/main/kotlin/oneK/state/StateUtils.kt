package oneK.state

import oneK.deck.Card
import oneK.player.Player
import oneK.variant.Variant

internal fun State.Bidding.allCardsPlayed(): Boolean =
    this.order
        .map(Bidder::lastAction)
        .filterNot { it is BiddingAction.Fold }
        .size <= 1

internal fun State.Bidding.currentBid(): Int =
    this.order
        .asSequence()
        .map(Bidder::lastAction)
        .filterIsInstance<BiddingAction.Bid>()
        .map(BiddingAction.Bid::amount)
        .max() ?: 0

internal fun isValidBid(
    bid: Int,
    currentBid: Int,
    cards: Set<Card>,
    variant: Variant
): Boolean {
    return bid > currentBid
            && variant.canBid(cards, bid)
            && bid % 10 == 0
            && bid - currentBid <= variant.getMaxBidStep() // TODO min bid oneK.state ???
            && bid <= variant.getUpperBidThreshold()
}

internal fun State.Strife.allCardsPlayed(): Boolean = this.order.all { it.cards.isEmpty() }

internal fun State.Strife.isBoardFull(): Boolean = this.order.mapNotNull(
    Strifer::lastAction).isEmpty()

internal fun State.Strife.currentCardsUnordered(): List<Card> = this.order.mapNotNull { it.lastAction?.card }

internal fun State.Strife.firstCard(): Card? = this.order.firstOrNull { it.lastAction != null }?.lastAction?.card

internal fun State.Strife.accountForStriferConstraint(): RepeatableOrder<Pair<Player, Int>> =
    this.order
        .map { Pair(it.player, if (this.bid > it.points) -this.bid else this.bid) }

internal fun State.Strife.addPointsAndClearBoard(): State.Strife {
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

internal fun State.Strife.getWinner(): Strifer {
    val firstCard = this.firstCard()
    return this.order
        .filter { it.lastAction?.card?.color == firstCard?.color }
        .maxBy { it.lastAction?.card?.figure?.value ?: 0 }
        ?: error("Unable to determine winner")
}