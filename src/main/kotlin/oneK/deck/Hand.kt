package oneK.deck

/**
 * Created by Jakub Licznerski on 03.11.2017.
 */
class Hand(val cards: List<Card>) {

    companion object {
        @JvmStatic
        fun fromString(hand: String): Hand? {
            val cards = hand.split(",".toRegex())
            val cardsList = cards.map { Card.fromString(it[0], it[1]) }
            return if (cardsList.contains(null)) null else Hand(cardsList.requireNoNulls())
        }
    }

    override fun toString(): String {
        return cards.map { it.toString() }.reduceRight({ it, acc -> "$acc,  $it" })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hand) return false
        if (!cards.containsAll(other.cards) || !other.cards.containsAll(cards)) return false

        return true
    }

    override fun hashCode(): Int {
        return cards.hashCode()
    }

}