package oneK.state

import oneK.rotate

internal fun State.Bidding.transitionToReviewState(): FoldingEffect.ReviewTransition {
    return FoldingEffect.ReviewTransition(
        State.Review(
            order = this.order.map { Reviewer(it.cards, it.player) },
            initialBid = this.currentBid(),
            talon = Choice.of(this.talon),
            ranking = this.ranking
        )
    )
}

internal fun State.Strife.transitionToSummaryState(): PlayingEffect.SummaryTransition {
    val newRanking = this.accountForStriferConstraint()
        .map { (player, points) -> Pair(player, points + this.ranking.getOrDefault(player, 0)) }
        .toMap()
    return PlayingEffect.SummaryTransition(
        State.Summary(
            order = RepeatableOrder.of(this.order.rotate().map { it.player }),
            ranking = newRanking
        )
    )
}