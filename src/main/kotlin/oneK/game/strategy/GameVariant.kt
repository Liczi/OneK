package oneK.game.strategy

import oneK.deck.Hand
import java.io.Serializable

// TODO builder without `set` prefix
interface GameStrategy : Serializable {

    var getUpperBidThreshold: () -> Int
    var getInitialBid: () -> Int
    var getMaxBidStep: () -> Int
    var canBid: (Hand, Int) -> Boolean
    var getLimitedScoringThreshold: () -> Int

    class Builder {
        private val strategy: GameStrategy

        init {
            this.strategy = DefaultGameStrategy()
        }

        public fun setUpperBidThreshold(threshold: Int): Builder {
            this.strategy.getUpperBidThreshold = { threshold }
            return this
        }

        public fun setInitialBid(bid: Int): Builder {
            this.strategy.getInitialBid = { bid }
            return this
        }

        public fun setMaxBidStep(step: Int): Builder {
            this.strategy.getMaxBidStep = { step }
            return this
        }

        /**
         * You never know what is in stash - must be checked
         */
        public fun setCanBid(canBid: (Hand, Int) -> Boolean): Builder {
            this.strategy.canBid = canBid
            return this
        }

        //todo ??
//        public fun setSubmitScores(submitScores: )

        public fun setLimitedScoringThreshold(threshold: Int): Builder {
            this.strategy.getUpperBidThreshold = { threshold }
            return this
        }

        public fun build(): GameStrategy {
            return this.strategy
        }
    }
}