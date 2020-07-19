package oneK.variant

import oneK.deck.Card
import oneK.hasTriumph
import oneK.splitCardsToEqualSets

private const val MAXIMUM_BID = 300

class DefaultTwoPlayerVariant: Variant {
    override var getGameGoal = { 1000 }
    override var getUpperBidThreshold = { MAXIMUM_BID }
    override var getInitialBid = { 100 }
    override var getMaxBidStep = { 10 }
    override var canBid: (Set<Card>, Int) -> Boolean = { cards, bid ->
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
}

fun DefaultThreePlayerVariant(): Variant = Variant.Builder()
    .talonsQuantity(1)
    .talonCardsQuantity(3)
    .build()