package oneK.v2.service

import oneK.v2.state.Bidder
import oneK.v2.state.BiddingAction
import oneK.v2.state.State

interface BiddingService {
    fun State.Bidding.performBid(bid: Int): State.Bidding
    fun State.Bidding.performFold(): State.Bidding
}

internal object DefaultBiddingServiceImpl : BiddingService {

    override fun State.Bidding.performBid(bid: Int): State.Bidding = endTurnWith(BiddingAction.Bid(bid))

    override fun State.Bidding.performFold(): State.Bidding = endTurnWith(BiddingAction.Fold)

    private fun State.Bidding.endTurnWith(action: BiddingAction): State.Bidding =
        this.copy(
            order = this.order.replaceCurrentAndNextUntil(bidderWithAction(action)) { it.lastAction.isNotFold() }
        )

    private fun State.Bidding.bidderWithAction(action: BiddingAction): Bidder =
        this.order.current().copy(lastAction = action)

    private fun BiddingAction?.isNotFold() = (this is BiddingAction.Fold).not()
}
