package oneK.deck

data class Card(val figure: Figure, val color: Color) {

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