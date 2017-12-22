package oneK.strategy

import oneK.deck.Hand
import oneK.game.MAXIMUM_BID

class DefaultGameStrategy : GameStrategy {
    override var getUpperBidThreshold = { MAXIMUM_BID }
    override var getInitialBid = { 100 }
    override var getMaxBidStep = { 10 }
    override var canBid: (Hand, Int) -> Boolean = { hand, bid ->
        bid <= getMaxBidStep() && (bid <= 120 || hand.hasTriumph())
    }
    override var getLimitedScoringThreshold = { 900 }
}