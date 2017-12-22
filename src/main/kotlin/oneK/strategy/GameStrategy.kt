package oneK.strategy

import oneK.deck.Hand


interface GameStrategy {

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

        public fun setUpperBidThreshold(threshold: Int) {
            this.strategy.getUpperBidThreshold = { threshold }
        }

        public fun setInitialBid(bid: Int) {
            this.strategy.getInitialBid = { bid }
        }

        public fun setMaxBidStep(step: Int) {
            this.strategy.getMaxBidStep = { step }
        }

        /**
         * You never know what is in stash - must be checked
         */
        public fun setCanBid(canBid: (Hand, Int) -> Boolean) {
            this.strategy.canBid = canBid
        }

        //todo ??
//        public fun setSubmitScores(submitScores: )

        public fun setLimitedScoringThreshold(threshold: Int) {
            this.strategy.getUpperBidThreshold = { threshold }
        }

        public fun build(): GameStrategy {
            return this.strategy
        }
    }
}