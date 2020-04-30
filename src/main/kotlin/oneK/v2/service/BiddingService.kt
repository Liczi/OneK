package oneK.v2.service

import oneK.v2.state.State

interface BiddingService {
    fun State.Bidding.performBid(bid: Int): State.Bidding
    fun State.Bidding.performFold(): State.Bidding
}

internal object DefaultBiddingServiceImpl : BiddingService {

    override fun State.Bidding.performBid(bid: Int): State.Bidding {
        this.bidders.current().lastBid = bid
        this.currentBid = bid
        this.bidders.next()
        return this
    }

    override fun State.Bidding.performFold(): State.Bidding {
        this.bidders.current().folded = true
        this.bidders.next()
        return this
    }
}