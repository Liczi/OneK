package oneK.v2.service

import oneK.v2.state.State

object BiddingServiceImpl : BiddingService {

    override fun State.Bidding.bid(bid: Int): State.Bidding {
        this.bidders.current().lastBid = bid
        this.currentBid = bid
        this.bidders.next()
        return this
    }

    override fun State.Bidding.fold(): State.Bidding {
        this.bidders.current().folded = true
        this.bidders.next()
        return this
    }
}