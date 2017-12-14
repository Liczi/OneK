package oneK.strategy

import oneK.deck.Card
import java.lang.IllegalArgumentException

interface RoundStrategy {
    var getBombPoints: () -> Int
    var getBombAllowedBidThreshold: () -> Int

    var setTalonCards: (HashSet<Card>) -> Unit
    var getTalonSize: () -> Int
    var getTalonsQuantity: () -> Int
    var getTalons: () -> Array<HashSet<Card>>

    //todo show talon listener ?
    //var getShowTalonThreshold: () -> Int
    //todo bomb listener ?

    class Builder {

        private val strategy: RoundStrategy

        init {
            this.strategy = DefaultRoundStrategy()
        }

        fun build(): RoundStrategy = this.strategy

        fun setBombPoints(points: Int) {
            this.strategy.getBombPoints = { points }
        }

        fun setPlayersQuant(players: Int) {
            when (players) {
                2 -> {
                    this.strategy.getTalonSize = { 2 }
                    this.strategy.getTalonsQuantity = { 2 }
                }
                3 -> {
                    this.strategy.getTalonSize = { 3 }
                    this.strategy.getTalonsQuantity = { 1 }
                }
                4 -> {
                    this.strategy.getTalonSize = { 4 }
                    this.strategy.getTalonsQuantity = { 1 }
                }
                else -> throw IllegalArgumentException("Too many players")
            }
        }

        fun setBombAllowedBidThreshold(threshold: Int) {
            this.strategy.getBombAllowedBidThreshold = { threshold }
        }

    }
}