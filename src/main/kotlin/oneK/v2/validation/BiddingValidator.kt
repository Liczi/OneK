package oneK.v2.validation

import oneK.v2.state.State
import oneK.v2.variant.Variant

interface BiddingValidator {
    fun canBid(bid: Int, state: State.Bidding): State.Bidding?
}

class BiddingStateValidatorImpl(private val variant: Variant) : BiddingValidator, StateValidator() {
    override fun canBid(bid: Int, state: State.Bidding): State.Bidding? {
        return state.ensureValid {
            bid > state.currentBid &&
                    variant.canBid(state.bidders.current().hand.cards, bid) &&
                    bid % 10 == 0 && // TODO min bid state ???
                    bid - state.currentBid <= variant.getMaxBidStep() &&
                    bid <= variant.getUpperBidThreshold()
        }
    }
}