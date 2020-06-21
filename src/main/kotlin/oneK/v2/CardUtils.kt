package oneK.v2

import oneK.deck.Card
import oneK.deck.Figure

//fun Hand.contains(card: Card) = this.cards.any { it == card }
//
//fun Hand.containsAll(hand: Hand) = this.cards.containsAll(hand.cards)


//TODO for sanity use regex or delegate Card.fromString to accept the whole splitted segment
fun String.toCardSet(): Set<Card> {
    if (this.isBlank()) return emptySet()
    val cards = this.split(",".toRegex())
    val cardsList = cards
        .map { it.trim() }
        .map { Card.fromString(it[0], it[1]) }
    return cardsList.filterNotNull().toSet()
}

fun getClassicDeck(): Set<Card> {
    val cards = "9S,9C,9D,9H,JS,JC,JD,JH,QS,QC,QD,QH,KS,KC,KD,KH,TS,TC,TD,TH,AS,AC,AD,AH"
    return cards.toCardSet()
}

fun Set<Card>.hasTriumph(): Boolean {
    val kings = this.filter { it.figure == Figure.KING }.map { it.color }
    val queens = this.filter { it.figure == Figure.QUEEN }.map { it.color }

    return kings.any { queens.contains(it) }
}

fun Collection<Card>.splitCardsToEqualSets(setsCount: Int): List<Set<Card>> {
    require(this.size % setsCount == 0)
    val desiredSize = this.size / setsCount
    return this.withIndex()
        .groupBy { it.index / desiredSize }
        .map(::toCardSet)
}

private fun <T> toCardSet(it: Map.Entry<Int, List<IndexedValue<T>>>) =
    it.value.map { it.value }.toSet()