package oneK.deck

enum class Color(val value: Int) {
    HEARTS(100),
    SPADES(40),
    DIAMONDS(80),
    CLUBS(60);

    override fun toString(): String = name.toLowerCase().capitalize()
}