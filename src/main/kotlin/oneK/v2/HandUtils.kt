package oneK.v2

import oneK.deck.Card
import oneK.deck.Figure

fun Hand.contains(card: Card) = this.cards.any { it == card }

fun Hand.containsAll(hand: Hand) = this.cards.containsAll(hand.cards)

fun Hand.hasTriumph(): Boolean {
    val kings = this.cards.filter { it.figure == Figure.KING }.map { it.color }
    val queens = this.cards.filter { it.figure == Figure.QUEEN }.map { it.color }

    return kings.any { queens.contains(it) }
}

fun String.toCardSet(): Set<Card>? {
    if (this.isBlank()) return null
    val cards = this.split(",".toRegex())
    val cardsList = cards
            .map { it.trim() }
            .map { Card.fromString(it[0], it[1]) }
    return if (cardsList.contains(null)) null else cardsList.requireNoNulls().toHashSet()
}

fun getClassicDeck(): Set<Card> {
    val cards = "9S,9C,9D,9H,JS,JC,JD,JH,QS,QC,QD,QH,KS,KC,KD,KH,TS,TC,TD,TH,AS,AC,AD,AH"
    return cards.toCardSet()!!
}