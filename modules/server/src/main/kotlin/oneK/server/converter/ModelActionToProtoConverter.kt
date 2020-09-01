package oneK.server.converter

import oneK.proto.action.BiddingAction
import oneK.proto.action.ReviewAction
import oneK.proto.action.ReviewActionPayload
import oneK.proto.action.StrifeAction
import oneK.proto.action.SummaryAction
import oneK.state.Action


internal fun Action.toProtoMessage(): ActionProto =
    when (this) {
        is Action.Summary -> actionWith { this.setSummary(this@toProtoMessage.toProtoMessage()) }
        is Action.Bidding -> actionWith { this.setBidding(this@toProtoMessage.toProtoMessage()) }
        is Action.Review -> actionWith { this.setReview(this@toProtoMessage.toProtoMessage()) }
        is Action.Strife -> actionWith { this.setStrife(this@toProtoMessage.toProtoMessage()) }
    }

private fun actionWith(setFunction: ActionProtoBuilder.() -> ActionProtoBuilder): ActionProto =
    ActionProto.newBuilder()
        .setFunction()
        .build()

internal fun Action.Bidding.toProtoMessage(): BiddingAction {
    val builder = BiddingAction.newBuilder()
    when (this) {
        is Action.Bidding.Bid -> builder
            .setAmount(this.amount)
            .type = BiddingAction.BiddingActionType.BID
        Action.Bidding.Fold -> builder
            .type = BiddingAction.BiddingActionType.FOLD
    }
    return builder.build()
}

internal fun Action.Strife.toProtoMessage(): StrifeAction {
    val builder = StrifeAction.newBuilder()
    when (this) {
        is Action.Strife.Play -> builder
            .setCard(this.card.toProtoMessage())
            .type = StrifeAction.StrifeActionType.PLAY
        is Action.Strife.Triumph -> builder
            .setCard(this.card.toProtoMessage())
            .type = StrifeAction.StrifeActionType.TRIUMPH
    }
    return builder.build()
}

private fun Action.Review.toProtoMessage(): ReviewAction {
    val builder = ReviewAction.newBuilder()
    when (this) {
        is Action.Review.Pick -> builder
            .setPayload(reviewPayloadWith { this.setTalonInd(this@toProtoMessage.talonInd) })
            .type = ReviewAction.ReviewActionType.PICK
        is Action.Review.Change -> builder
            .setPayload(reviewPayloadWith { this.setNewBid(this@toProtoMessage.newBid) })
            .type = ReviewAction.ReviewActionType.BID
        is Action.Review.Distribute -> builder
            .setPayload(reviewPayloadWith { this.setDistribute(this@toProtoMessage.toGive.toProtoMessage()) })
            .type = ReviewAction.ReviewActionType.DISTRIBUTE
        Action.Review.Restart -> builder.type = ReviewAction.ReviewActionType.RESTART
        Action.Review.Confirm -> builder.type = ReviewAction.ReviewActionType.CONFIRM
    }
    return builder.build()
}

private fun reviewPayloadWith(setFunction: ReviewActionPayload.Builder.() -> ReviewActionPayload.Builder): ReviewActionPayload =
    ReviewActionPayload.newBuilder()
        .setFunction()
        .build()

private fun Action.Summary.toProtoMessage(): SummaryAction {
    val builder = SummaryAction.newBuilder()
    when (this) {
        Action.Summary.Start -> builder.type = SummaryAction.SummaryActionType.START
    }
    return builder.build()
}