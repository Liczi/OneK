package oneK.deck

import oneK.deck.Color.*
import oneK.deck.Figure.*

data class Card(val figure: Figure, val color: Color) {

    //    TODO move to Color and Figure classes, use string not single characters in this factory method
    companion object {
        @JvmStatic
        fun fromString(figureString: Char, colorString: Char): Card {
            val figure = when (figureString) {
                '9' -> NINE
                'T' -> TEN
                'J' -> JACK
                'Q' -> QUEEN
                'K' -> KING
                'A' -> ACE
                else -> error("Invalid figure literal")
            }

            val color = when (colorString) {
                'H' -> HEARTS
                'D' -> DIAMONDS
                'C' -> CLUBS
                'S' -> SPADES
                else -> error("Invalid color literal")
            }
            return Card(figure, color)
        }
    }

    override fun toString(): String = "$figure of $color"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false
        if (figure != other.figure || color != other.color) return false
        return true
    }

    //Implementation based on Josh Bloch's Effective Java
    override fun hashCode() =
        37 * figure.value.xor(figure.value.ushr(32)) + color.value.xor(color.value.ushr(32))
}