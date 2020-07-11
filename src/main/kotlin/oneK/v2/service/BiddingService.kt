package oneK.v2.service

import oneK.v2.action.BiddingAction
import oneK.v2.state.Bidder
import oneK.v2.state.State

interface BiddingService {
    fun State.Bidding.performBid(bid: Int): State.Bidding
    fun State.Bidding.performFold(): State.Bidding
}

internal object DefaultBiddingServiceImpl : BiddingService {

    override fun State.Bidding.performBid(bid: Int): State.Bidding = endTurnWith(BiddingAction.Bid(bid))

    override fun State.Bidding.performFold(): State.Bidding = endTurnWith(BiddingAction.Fold)

    private fun State.Bidding.endTurnWith(action: BiddingAction): State.Bidding =
        this.copy(biddersOrder = this.biddersOrder.replaceCurrentAndNext(performAction(action)))

    private fun State.Bidding.performAction(action: BiddingAction): Bidder =
        this.biddersOrder.current().copy(lastAction = action)
}
