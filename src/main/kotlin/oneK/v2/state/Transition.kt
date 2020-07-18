package oneK.v2.state

internal fun State.Bidding.transitionToReviewState(): FoldingEffect.ReviewTransition {
    return FoldingEffect.ReviewTransition(
        State.Review(
            order = this.order.map { Reviewer(it.cards, it.player) },
            initialBid = this.currentBid(),
            talon = Choice.of(this.talon)
        )
    )
}

internal fun State.Strife.transitionToSummaryState(): PlayingEffect.SummaryTransition {
    val strifeRanking = this.accountForStriferConstraint() //todo implement
//    appy accounted ranking to summary state - consider using points within Summarer ? class
    TODO("Not yet implemented")
}