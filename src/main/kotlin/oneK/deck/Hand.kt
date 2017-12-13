package oneK.deck

/**
 * Created by Jakub Licznerski on 03.11.2017.
 */
class Hand {

    val cards: HashSet<Card>

    constructor(cards: HashSet<Card>) {
        this.cards = cards
    }

    constructor(cards: Array<Card>) {
        this.cards = cards.toHashSet()
    }

    companion object {
        @JvmStatic
        fun fromString(hand: String): Hand? {
            if (hand.trim() == "") return Hand(hashSetOf())
            val cards = hand.split(",".toRegex())
            val cardsList = cards
                    .map { it.trim() }
                    .map { Card.fromString(it[0], it[1]) }
            return if (cardsList.contains(null)) null else Hand(cardsList.requireNoNulls().toHashSet())
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

    //Implementation based on Josh Bloch's Effective Java
    override fun hashCode() = cards.map { it.hashCode() }.reduceRight { i, acc -> 37 * acc + i }
}