package oneK.deck

import java.io.Serializable

class Hand : Serializable {

    val cards: HashSet<Card>

    constructor(cards: HashSet<Card>) {
        this.cards = cards
    }

    constructor(cards: Array<Card>) {
        this.cards = cards.toHashSet()
    }

    public fun contains(card: Card) = this.cards.any { it == card }

    public fun containsAll(hand: Hand) = this.cards.containsAll(hand.cards)

    public fun hasTriumph(): Boolean {
        val kings = this.cards.filter { it.figure == Figure.KING }
        val queens = this.cards.filter { it.figure == Figure.QUEEN }

        return kings.any { queens.map { it.color }.contains(it.color) }
    }

    //    TODO delete unused
    companion object {
        @JvmStatic
        fun fromString(hand: String): Hand {
            if (hand.trim() == "") return Hand(hashSetOf())
            val cards = hand.split(",".toRegex())
            val cardsList = cards
                .map { it.trim() }
                .map { Card.fromString(it[0], it[1]) }
            return Hand(cardsList.requireNoNulls().toHashSet())
        }

        @JvmStatic
        fun getClassicDeck(): HashSet<Card> {
            val cards = "9S,9C,9D,9H,JS,JC,JD,JH,QS,QC,QD,QH,KS,KC,KD,KH,TS,TC,TD,TH,AS,AC,AD,AH"
            return fromString(cards).cards
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