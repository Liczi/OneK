package oneK.server.converter

import oneK.proto.action.BiddingAction
import oneK.proto.action.ReviewAction
import oneK.proto.action.StrifeAction
import oneK.proto.action.SummaryAction
import oneK.state.Action

private typealias ActionProtoCase = oneK.proto.action.Action.ActionCase

internal fun ActionProto.toModel(): Action =
    when (this.actionCase) {
        ActionProtoCase.BIDDING -> this.bidding.toModel()
        ActionProtoCase.REVIEW -> this.review.toModel()
        ActionProtoCase.STRIFE -> this.strife.toModel()
        ActionProtoCase.SUMMARY -> this.summary.toModel()
        else -> error("Action is not set!")
    }

internal fun BiddingAction.toModel(): Action.Bidding =
    when (this.type) {
        BiddingAction.BiddingActionType.BID -> Action.Bidding.Bid(this.amount)
        BiddingAction.BiddingActionType.FOLD -> Action.Bidding.Fold
        else -> error("Unrecognized action type!")
    }

private fun ReviewAction.toModel(): Action.Review =
    when (this.type) {
        ReviewAction.ReviewActionType.PICK -> Action.Review.Pick(this.payload.talonInd)
        ReviewAction.ReviewActionType.DISTRIBUTE -> Action.Review.Distribute(this.payload.distribute.toModel())
        ReviewAction.ReviewActionType.CHANGE -> Action.Review.Change(this.payload.newBid)
        ReviewAction.ReviewActionType.CONFIRM -> Action.Review.Confirm
        ReviewAction.ReviewActionType.RESTART -> Action.Review.Restart
        else -> error("Unrecognized action type!")
    }


internal fun StrifeAction.toModel(): Action.Strife =
    when (this.type) {
        StrifeAction.StrifeActionType.PLAY -> Action.Strife.Play(this.card.toModel())
        StrifeAction.StrifeActionType.TRIUMPH -> Action.Strife.Triumph(this.card.toModel())
        else -> error("Unrecognized action type!")
    }

private fun SummaryAction.toModel(): Action.Summary =
    when (this.type) {
        SummaryAction.SummaryActionType.START -> Action.Summary.Start
        else -> error("Unrecognized action type!")
    }