package oneK.variant

import oneK.deck.Card
import oneK.hasTriumph

private const val MAXIMUM_BID = 300

private abstract class DefaultVariant : Variant {
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
}

private class DefaultTwoPlayerVariant : DefaultVariant() {
    override var getTalonsQuantity = { 2 }
    override var getTalonCardsQuantity = { 4 }
}

private class DefaultThreePlayerVariant : DefaultVariant() {
    override var getTalonsQuantity = { 1 }
    override var getTalonCardsQuantity = { 3 }
}

fun getVariantFor(playersCount: Int): Variant =
    when (playersCount) {
        2 -> DefaultTwoPlayerVariant()
        3 -> DefaultThreePlayerVariant()
        else -> error("No variant for $playersCount player(s)")
    }
