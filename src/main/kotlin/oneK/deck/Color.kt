package oneK.deck

import java.io.Serializable

/**
 * Created by Jakub Licznerski on 03.11.2017.
 */
enum class Color(val value: Int) : Serializable {
    HEARTS(100),
    SPADES(40),
    DIAMONDS(80),
    CLUBS(60);

    override fun toString(): String = name.toLowerCase().capitalize()
}