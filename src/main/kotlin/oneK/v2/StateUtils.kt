package oneK.v2

import oneK.v2.state.Bidder
import oneK.v2.state.State

fun State.Bidding.isBiddingCompleted() = this.bidders.filter(Bidder::folded).size <= 1