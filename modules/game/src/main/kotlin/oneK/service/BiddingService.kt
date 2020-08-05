package oneK.service

import oneK.state.Action
import oneK.state.Bidder
import oneK.state.State

interface BiddingService {
    fun State.Bidding.performBid(bid: Int): State.Bidding
    fun State.Bidding.performFold(): State.Bidding
}

internal object DefaultBiddingServiceImpl : BiddingService {

    override fun State.Bidding.performBid(bid: Int): State.Bidding = endTurnWith(
        Action.Bidding.Bid(bid))

    override fun State.Bidding.performFold(): State.Bidding = endTurnWith(
        Action.Bidding.Fold)

    private fun State.Bidding.endTurnWith(action: Action.Bidding): State.Bidding =
        this.copy(
            order = this.order.replaceCurrentAndNextUntil(bidderWithAction(action)) { it.lastAction.isNotFold() }
        )

    private fun State.Bidding.bidderWithAction(action: Action.Bidding): Bidder =
        this.order.current().copy(lastAction = action)

    private fun Action.Bidding?.isNotFold() = (this is Action.Bidding.Fold).not()
}
