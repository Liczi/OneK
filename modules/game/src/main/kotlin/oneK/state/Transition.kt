package oneK.state

import oneK.player.Player
import oneK.rotate
import java.lang.Integer.max

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
    val roundResult = this.rankingAccountForConstraint()
    val newRanking = roundResult
        .map { (player, points) -> Pair(player, addPointsWithMinZero(points, player)) }
        .toMap()
    return PlayingEffect.SummaryTransition(
        State.Summary(
            order = RepeatableOrder.of(this.order.rotate().map { it.player }),
            roundRanking = roundResult.toMap(),
            ranking = newRanking
        )
    )
}

private fun State.Strife.addPointsWithMinZero(points: Int, player: Player) =
    max(points.roundUpFromFive() + this.ranking.getOrDefault(player, 0), 0)

private fun Int.roundUpFromFive(): Int = (if (this % 10 >= 5) (this + 5) else this) / 10 * 10