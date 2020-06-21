package oneK.v2.variant

import oneK.deck.Card
import oneK.game.MAXIMUM_BID
import oneK.v2.Hand
import oneK.v2.hasTriumph
import oneK.v2.splitCardsToEqualSets


class DefaultVariant : Variant {
    override var getGameGoal = { 1000 }
    override var getUpperBidThreshold = { MAXIMUM_BID }
    override var getInitialBid = { 100 }
    override var getMaxBidStep = { 10 }
    override var canBid: (Hand, Int) -> Boolean = { cards, bid ->
        bid <= 120 || cards.hasTriumph()
    }
    override var getLimitedScoringThreshold = { 900 }
    override var getBombPoints: () -> Int = { 60 }
    override var getBombAllowedBidThreshold: () -> Int = { 120 }

    override var getTalonsQuantity = { 2 }
    override var getTalonCards: (Collection<Card>) -> List<Set<Card>> = { cards ->
        cards.splitCardsToEqualSets(this.getTalonsQuantity())
    }
    override var getTalonCardsQuantity = { 4 }

    override var qualifiesForRestart = { _: Hand -> false }
}