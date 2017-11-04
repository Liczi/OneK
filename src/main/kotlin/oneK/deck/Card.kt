package oneK.deck

import oneK.deck.Figure.*
import oneK.deck.Color.*

/**
 * Created by Jakub Licznerski on 03.11.2017.
 */
data class Card(val figure: Figure, val color: Color) {

    companion object {
        @JvmStatic
        fun fromString(figureString: Char, colorString: Char): Card? {
            val figure = when (figureString) {
                '9' -> NINE
                'T' -> TEN
                'J' -> JACK
                'Q' -> QUEEN
                'K' -> KING
                'A' -> ACE
                else -> return null
            }

            val color = when (colorString) {
                'H' -> HEARTS
                'D' -> DIAMONDS
                'C' -> CLUBS
                'S' -> SPADES
                else -> return null
            }
            return Card(figure, color)
        }
    }

    override fun toString(): String = "$figure of $color"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false
        if (figure != other.figure && color != other.color) return false
        return true
    }

    override fun hashCode(): Int = figure.value + color.value
}