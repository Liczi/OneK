package oneK.validation

import oneK.state.State
import oneK.state.currentBid
import oneK.state.isValidBid
import oneK.state.isValidStep
import oneK.variant.Variant

interface BiddingValidator {
    fun canBid(state: State.Bidding, bid: Int): Boolean
}

internal class BiddingStateValidatorImpl(private val variant: Variant) : BiddingValidator {
    override fun canBid(state: State.Bidding, bid: Int): Boolean {
        val currentBid = state.currentBid()
        return isValidBid(bid, currentBid, state.order.current().cards, variant)
                && bid.isValidStep(currentBid, variant)
    }
}