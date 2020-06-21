package oneK.v2.variant

import oneK.deck.Card
import oneK.v2.Hand

interface Variant {
    //TODO minBidStep ???
    var getGameGoal: () -> Int
    var getUpperBidThreshold: () -> Int
    var getInitialBid: () -> Int
    var getMaxBidStep: () -> Int
    var canBid: (Set<Card>, Int) -> Boolean
    var getLimitedScoringThreshold: () -> Int
    var getBombPoints: () -> Int
    var getBombAllowedBidThreshold: () -> Int
    var getTalonCards: (Collection<Card>) -> List<Set<Card>>
    var getTalonsQuantity: () -> Int
    var getTalonCardsQuantity: () -> Int
    var qualifiesForRestart: (Hand) -> Boolean

    class Builder {
        private val variant: Variant

        init {
            this.variant = DefaultVariant()
        }

        fun gameGoal(goal: Int): Builder {
            this.variant.getGameGoal = { goal }
            return this
        }

        fun upperBidThreshold(threshold: Int): Builder {
            this.variant.getUpperBidThreshold = { threshold }
            return this
        }

        fun initialBid(bid: Int): Builder {
            this.variant.getInitialBid = { bid }
            return this
        }

        fun maxBidStep(step: Int): Builder {
            this.variant.getMaxBidStep = { step }
            return this
        }

        fun canBid(canBid: (Set<Card>, Int) -> Boolean): Builder {
            this.variant.canBid = canBid
            return this
        }

        fun limitedScoringThreshold(threshold: Int): Builder {
            this.variant.getUpperBidThreshold = { threshold }
            return this
        }

        fun bombPoints(points: Int): Builder {
            this.variant.getBombPoints = { points }
            return this
        }

        fun bombAllowedBidThreshold(threshold: Int): Builder {
            this.variant.getBombAllowedBidThreshold = { threshold }
            return this
        }

        fun talonsQuantity(quantity: Int): Builder {
            this.variant.getTalonsQuantity = { quantity }
            return this
        }

        fun talonCardsQuantity(quantity: Int): Builder {
            this.variant.getTalonCardsQuantity = { quantity }
            return this
        }

        fun qualifiesForRestart(qualifiesForRestart: (Hand) -> Boolean): Builder {
            this.variant.qualifiesForRestart = qualifiesForRestart
            return this
        }

        fun build(): Variant {
            return this.variant
        }
    }
}