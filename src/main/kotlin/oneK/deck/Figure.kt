package oneK.deck

enum class Figure(val value: Int) {
    NINE(0),
    TEN(10),
    JACK(2),
    QUEEN(3),
    KING(4),
    ACE(11);

    override fun toString(): String = name.toLowerCase().capitalize()
}