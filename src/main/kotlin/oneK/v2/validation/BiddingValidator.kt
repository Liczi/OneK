package oneK.v2.validation

import oneK.v2.state.GameState
import oneK.v2.state.State
import oneK.v2.variant.Variant

interface BiddingValidator {
    fun canBid(bid: Int, gameState: GameState, variant: Variant): Boolean
}

object BiddingValidatorImpl : BiddingValidator {
    override fun canBid(bid: Int, gameState: GameState, variant: Variant): Boolean {
        return (gameState.currentState as? State.Bidding)?.let { state ->
            bid > state.currentBid &&
                    variant.canBid(state.bidders.current().hand.cards, bid) &&
                    bid % 10 == 0 && // TODO min bid state ???
                    bid - state.currentBid <= variant.getMaxBidStep() &&
                    bid <= variant.getUpperBidThreshold()
        } ?: false
    }
}