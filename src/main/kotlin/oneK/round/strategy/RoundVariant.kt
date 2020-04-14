package oneK.round.strategy

import oneK.deck.Card
import oneK.deck.Hand
import java.io.Serializable
import java.lang.IllegalArgumentException

interface Variant : Serializable {
    var getBombPoints: () -> Int
    var getBombAllowedBidThreshold: () -> Int

    var setTalonCards: (HashSet<Card>) -> Unit
    var getTalonSize: () -> Int
    var getTalonsQuantity: () -> Int
    var getTalons: () -> Array<HashSet<Card>>

    // false if game could be restarted
    var isValid: (Hand) -> Boolean

    class Builder {

        private val strategy: Variant

        init {
            this.strategy = DefaultVariant()
        }

        fun build(): Variant = this.strategy

        fun setBombPoints(points: Int): Builder {
            this.strategy.getBombPoints = { points }
            return this
        }

        fun setPlayersQuant(players: Int): Builder {
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

            return this
        }

        fun setBombAllowedBidThreshold(threshold: Int): Builder {
            this.strategy.getBombAllowedBidThreshold = { threshold }
            return this
        }

        fun setIsValid(isValid: (Hand) -> Boolean): Builder {
            this.strategy.isValid = isValid
            return this
        }

    }
}