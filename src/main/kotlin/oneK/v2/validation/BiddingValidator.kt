package oneK.v2.validation

import oneK.v2.currentBid
import oneK.v2.state.State
import oneK.v2.variant.Variant

interface BiddingValidator {
    fun canBid(bid: Int, state: State.Bidding): State.Bidding?
}

internal class BiddingStateValidatorImpl(private val variant: Variant) : BiddingValidator, StateValidator() {
    override fun canBid(bid: Int, state: State.Bidding): State.Bidding? {
        return state.ensureValid {
            val currentBid = state.currentBid(variant.getInitialBid())
            isValidBid(bid, currentBid, state.order.current().cards, variant)
        }
    }
}