package oneK.validation

import oneK.state.State
import oneK.state.currentBid
import oneK.state.isValidBid
import oneK.state.isValidStep
import oneK.variant.Variant

interface BiddingValidator {
    fun canBid(state: State.Bidding, bid: Int): State.Bidding?
}

internal class BiddingStateValidatorImpl(private val variant: Variant) : BiddingValidator, StateValidator() {
    override fun canBid(state: State.Bidding, bid: Int): State.Bidding? {
        return state.ensureValid {
            val currentBid = state.currentBid()
            isValidBid(bid, currentBid, state.order.current().cards, variant)
                    && bid.isValidStep(currentBid, variant)
        }
    }
}