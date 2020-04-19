package oneK.v2.variant

import oneK.deck.Card
import oneK.deck.Hand
import java.lang.IllegalArgumentException

interface Variant {
//TODO minBidStep ???
    var getUpperBidThreshold: () -> Int
    var getInitialBid: () -> Int
    var getMaxBidStep: () -> Int
    var canBid: (Set<Card>, Int) -> Boolean
    var getLimitedScoringThreshold: () -> Int
    var getBombPoints: () -> Int
    var getBombAllowedBidThreshold: () -> Int
    var setTalonCards: (HashSet<Card>) -> Unit
    var getTalonSize: () -> Int
    var getTalonsQuantity: () -> Int
    var getTalons: () -> Array<HashSet<Card>>
    var qualifiesForRestart: (Hand) -> Boolean

    class Builder {
        private val variant: Variant

        init {
            this.variant = DefaultVariant()
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

        fun playersQuant(players: Int): Builder {
            when (players) {
                2 -> {
                    this.variant.getTalonSize = { 2 }
                    this.variant.getTalonsQuantity = { 2 }
                }
                3 -> {
                    this.variant.getTalonSize = { 3 }
                    this.variant.getTalonsQuantity = { 1 }
                }
                4 -> {
                    this.variant.getTalonSize = { 4 }
                    this.variant.getTalonsQuantity = { 1 }
                }
                else -> throw IllegalArgumentException("Invalid players number. Supported is 2, 3 or 4 players.")
            }

            return this
        }

        fun bombAllowedBidThreshold(threshold: Int): Builder {
            this.variant.getBombAllowedBidThreshold = { threshold }
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