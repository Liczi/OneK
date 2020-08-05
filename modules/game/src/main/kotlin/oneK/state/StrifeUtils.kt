package oneK.state

import oneK.deck.Card
import oneK.player.Player

internal fun State.Strife.isAllCardsPlayed(): Boolean = this.order.all { it.cards.isEmpty() }

internal fun State.Strife.isBoardFull(): Boolean = this.order.all { it.lastAction != null }

internal fun State.Strife.currentCardsUnordered(): List<Card> = this.order.mapNotNull { it.lastAction?.card }

internal fun State.Strife.firstCard(): Card? = this.order.firstOrNull { it.lastAction != null }?.lastAction?.card

internal fun State.Strife.rankingAccountForConstraint(): RepeatableOrder<Pair<Player, Int>> =
    this.order
        .map { Pair(it.player, if (it.isConstrained) it.points.accountForConstraint(this.bid) else it.points) }

private fun Int.accountForConstraint(bid: Int): Int = if (bid > this) -bid else bid

internal fun State.Strife.addPointsAndClearBoard(): State.Strife {
    val winner = this.getWinner().player
    val points = this.order.map { it.lastAction?.card?.figure?.value ?: 0 }.sum()
    return this.copy(
        order = this.order.map {
            it.copy(
                lastAction = null,
                points = it.points + if (it.player == winner) points else 0
            )
        }.withCurrent { it.player == winner }
    )
}

internal fun State.Strife.getWinner(): Strifer {
    val firstCard = this.firstCard()
    return this.order
        .filter { it.lastAction?.card?.color == this.currentTriumph }
        .maxBy { lastCardValue(it) }
        ?: this.order
            .filter { it.lastAction?.card?.color == firstCard?.color }
            .maxBy { lastCardValue(it) }
        ?: error("Unable to determine winner")
}

private fun lastCardValue(it: Strifer) = it.lastAction?.card?.figure?.value ?: 0