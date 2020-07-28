package oneK

import oneK.deck.Card
import oneK.deck.Figure

private val ALL_NINES = "9S,9C,9D,9H".toCardSet()

fun String.toCardSet(): Set<Card> {
    if (this.isBlank()) return emptySet()
    val cards = this.split(",".toRegex())
    val cardsList = cards
        .map(String::trim)
        .map { Card.fromString(it[0], it[1]) }
    return cardsList.toSet()
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

fun Set<Card>.containsAllNines(): Boolean = this.containsAll(ALL_NINES)

internal fun Collection<Card>.splitCardsToEqualSets(setsCount: Int): List<Set<Card>> {
    require(this.size % setsCount == 0)
    val desiredSize = this.size / setsCount
    return this.withIndex()
        .groupBy { it.index / desiredSize }
        .map(::toCardSet)
}

private fun <T> toCardSet(it: Map.Entry<Int, List<IndexedValue<T>>>) =
    it.value.map { it.value }.toSet()