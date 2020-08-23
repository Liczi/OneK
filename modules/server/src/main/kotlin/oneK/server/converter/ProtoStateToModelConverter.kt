package oneK.server.converter

import oneK.deck.Card
import oneK.deck.Color
import oneK.player.Player
import oneK.proto.common.CardHolder
import oneK.proto.state.BiddingState
import oneK.proto.state.ReviewState
import oneK.proto.state.StrifeState
import oneK.proto.state.SummaryState
import oneK.splitCardsToEqualSets
import oneK.state.Bidder
import oneK.state.Choice
import oneK.state.RepeatableOrder
import oneK.state.Reviewer
import oneK.state.State
import oneK.state.Strifer

private typealias StateProtoCase = oneK.proto.state.State.StateCase

internal fun StateProto.toModel(): State =
    when (this.stateCase) {
        StateProtoCase.BIDDING -> this.bidding.toModel()
        StateProtoCase.REVIEW -> this.review.toModel()
        StateProtoCase.STRIFE -> this.strife.toModel()
        StateProtoCase.SUMMARY -> this.summary.toModel()
        else -> error("State is not set!")
    }

private fun BiddingState.toModel(): State.Bidding =
    this.orderList.map(BidderProto::toModel).let { bidders ->
        State.Bidding(
            order = RepeatableOrder.of(bidders, this.current - 1),
            talon = this.talonList.toCardModelSets(this.talonSplit),
            ranking = this.rankingMap.toModel(bidders.map { it.player })
        )
    }

private fun BidderProto.toModel(): Bidder =
    Bidder(
        cards = this.holder.cardsList.map(CardProto::toModel).toSet(),
        player = this.holder.player.toModel(),
        lastAction = this.lastAction.takeIf { this.hasLastAction() }?.toModel()
    )

private fun ReviewState.toModel(): State.Review =
    this.orderList.map(CardHolder::toReviewerModel).let { reviewers ->
        State.Review(
            order = RepeatableOrder.of(reviewers, this.current - 1),
            talon = this.talonList.toCardModelSets(this.talonSplit).toChoice(this),
            ranking = this.rankingMap.toModel(reviewers.map { it.player }),
            toGive = this.toGive.takeIf { this.hasToGive() }?.toModel(),
            initialBid = this.initialBid,
            changedBid = this.changedBid.takeIf { it > 0 }
        )
    }

private fun CardHolder.toReviewerModel(): Reviewer =
    Reviewer(
        cards = this.cardsList.map(CardProto::toModel).toSet(),
        player = this.player.toModel()
    )

private fun List<Set<Card>>.toChoice(state: ReviewState): Choice<Set<Card>> =
    if (state.takenCount > 0)
        Choice.Taken(state.takenList.map(CardProto::toModel).toSet(), this)
    else
        Choice.NotTaken(this)

private fun StrifeState.toModel(): State.Strife =
    this.orderList.map(StriferProto::toModel).let { strifers ->
        State.Strife(
            order = RepeatableOrder.of(strifers, this.current - 1),
            ranking = this.rankingMap.toModel(strifers.map { it.player }),
            bid = this.bid,
            currentTriumph = this.currentTriumph.takeIf { it.isNotBlank() }?.let { Color.valueOf(it) }
        )
    }

private fun StriferProto.toModel(): Strifer =
    Strifer(
        cards = this.holder.cardsList.map(CardProto::toModel).toSet(),
        player = this.holder.player.toModel(),
        points = this.points,
        isConstrained = this.isConstrained,
        lastAction = this.lastAction.takeIf { this.hasLastAction() }?.toModel()
    )

private fun SummaryState.toModel(): State.Summary =
    this.orderList.map(PlayerProto::toModel).let { players ->
        State.Summary(
            order = RepeatableOrder.of(players, this.current - 1),
            ranking = this.rankingMap.toModel(players)
        )
    }

private fun List<CardProto>.toCardModelSets(splits: Int): List<Set<Card>> =
    this.map(CardProto::toModel).splitCardsToEqualSets(splits)

private fun Map<String, Int>.toModel(players: List<Player>): Map<Player, Int> =
    this.mapKeys { (uuid, _) -> players.first { it.uuid.toString() == uuid } }
