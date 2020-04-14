package oneK.game.strategy

import oneK.deck.Hand
import java.io.Serializable

// TODO builder without `set` prefix
interface GameVariant : Serializable {

    var getUpperBidThreshold: () -> Int
    var getInitialBid: () -> Int
    var getMaxBidStep: () -> Int
    var canBid: (Hand, Int) -> Boolean
    var getLimitedScoringThreshold: () -> Int

    class Builder {
        private val variant: GameVariant

        init {
            this.variant = DefaultGameVariant()
        }

        public fun setUpperBidThreshold(threshold: Int): Builder {
            this.variant.getUpperBidThreshold = { threshold }
            return this
        }

        public fun setInitialBid(bid: Int): Builder {
            this.variant.getInitialBid = { bid }
            return this
        }

        public fun setMaxBidStep(step: Int): Builder {
            this.variant.getMaxBidStep = { step }
            return this
        }

        /**
         * You never know what is in stash - must be checked
         */
        public fun setCanBid(canBid: (Hand, Int) -> Boolean): Builder {
            this.variant.canBid = canBid
            return this
        }

        //todo ??
//        public fun setSubmitScores(submitScores: )

        public fun setLimitedScoringThreshold(threshold: Int): Builder {
            this.variant.getUpperBidThreshold = { threshold }
            return this
        }

        public fun build(): GameVariant {
            return this.variant
        }
    }
}