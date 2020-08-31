package oneK

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure

// CARD CONSTRUCTION
infix fun Figure.of(color: Color) = Card(this, color)

fun String.asCard(): Card =
    this.trim()
        .let {
            require(it.length == 2)
            Card(it[0].asCardFigure(), it[1].asCardColor())
        }

private fun Char.asCardFigure(): Figure =
    when (this) {
        '9' -> Figure.NINE
        'T' -> Figure.TEN
        'J' -> Figure.JACK
        'Q' -> Figure.QUEEN
        'K' -> Figure.KING
        'A' -> Figure.ACE
        else -> error("Invalid figure literal")
    }

private fun Char.asCardColor(): Color =
    when (this) {
        'H' -> Color.HEARTS
        'D' -> Color.DIAMONDS
        'C' -> Color.CLUBS
        'S' -> Color.SPADES
        else -> error("Invalid color literal")
    }

fun String.asCardSet(): Set<Card> {
    if (this.isBlank()) return emptySet()
    val cards = this.split(",".toRegex())
    val cardsList = cards
        .map(String::trim)
        .map(String::asCard)
    return cardsList.toSet()
}

private val CLASSIC_DECK = "9S,9C,9D,9H,JS,JC,JD,JH,QS,QC,QD,QH,KS,KC,KD,KH,TS,TC,TD,TH,AS,AC,AD,AH".asCardSet()

fun getClassicDeck(): Set<Card> = CLASSIC_DECK


// CARD SET UTILS
fun Set<Card>.hasTriumph(): Boolean {
    val kings = this.filter { it.figure == Figure.KING }.map { it.color }
    val queens = this.filter { it.figure == Figure.QUEEN }.map { it.color }

    return kings.any { queens.contains(it) }
}

private val ALL_NINES = "9S,9C,9D,9H".asCardSet()

fun Set<Card>.containsAllNines(): Boolean = this.containsAll(ALL_NINES)

fun Collection<Card>.splitCardsToEqualSets(setsCount: Int): List<Set<Card>> {
    require(this.size % setsCount == 0)
    val desiredSize = this.size / setsCount
    return this.withIndex()
        .groupBy { it.index / desiredSize }
        .map(::asCardSet)
}

private fun <T> asCardSet(it: Map.Entry<Int, List<IndexedValue<T>>>) =
    it.value.map { it.value }.toSet()