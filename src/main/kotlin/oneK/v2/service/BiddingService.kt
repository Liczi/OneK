package oneK.v2.service

import oneK.v2.state.State

interface BiddingService {
    fun State.Bidding.bid(bid: Int): State.Bidding
    fun State.Bidding.fold(): State.Bidding
}