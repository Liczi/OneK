package oneK.v2

import oneK.v2.action.BiddingAction
import oneK.v2.state.Bidder
import oneK.v2.state.State

fun State.Bidding.isBiddingCompleted() =
    this.biddersOrder
        .map(Bidder::lastAction)
        .filterIsInstance<BiddingAction.Fold>()
        .size <= 1

fun State.Bidding.currentBid(initialBid: Int): Int =
    this.biddersOrder
        .asSequence()
        .map(Bidder::lastAction)
        .filterIsInstance<BiddingAction.Bid>()
        .map(BiddingAction.Bid::amount)
        .max() ?: initialBid