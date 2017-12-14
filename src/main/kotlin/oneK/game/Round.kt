package oneK.game

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Hand
import oneK.player.Player
import oneK.strategy.RoundStrategy

//tODO test firmly
class Round(players: List<Player>,
            private val strategy: RoundStrategy,
            private var bid: Int,
            var currentPlayer: Player) {

    private val table: MutableMap<Player, Card>
    private val hands: MutableMap<Player, Hand>
    val score: MutableMap<Player, Int>

    private var gameIsLocked = true
    private var currentTrump: Color? = null
    private var lastLeftWinner = currentPlayer


    init {
        this.table = mutableMapOf()
        this.score = mutableMapOf(*(players.map { Pair(it, 0) }.toTypedArray()))
        this.hands = mutableMapOf()
        val cards = Hand.getClassicDeck().shuffled().toMutableList()
        assignTalonCards(cards)
        assignPlayerCards(cards, players)
    }

    internal fun assignPlayerCards(cards: MutableList<Card>, players: List<Player>) {
        require(cards.size % players.size == 0)
        val playerCardsQuant = cards.size / players.size
        players.forEach { player ->
            val playerCards = cards.subList(0, playerCardsQuant)
            hands.put(player, Hand(playerCards.toTypedArray()))
            cards.removeAll(playerCards)
        }
        require(cards.size == 0)
    }

    internal fun assignTalonCards(cards: MutableList<Card>) {
        val talonCardsQuant = strategy.getTalonSize() * strategy.getTalonsQuantity()
        val talonCards = pickCards(talonCardsQuant, cards)
        strategy.setTalonCards(talonCards)
    }

    internal fun pickCards(quant: Int, cards: MutableList<Card>): HashSet<Card> {
        var i = 0
        val result = hashSetOf<Card>()

        while (i <= quant) {
            val index = Math.round(Math.random() * (cards.size - 1)).toInt()
            result.add(cards[index])
            cards.removeAt(index)
            i++
        }

        return result
    }

/* TODO procedure
    the current player begins, the game is locked (he cant place any
    he picks talon
    can change bet here or call bomb according to the strategy
    redistibutes talon, the game unlocks here

    he can place a card on the board, and the current player is changed
    meanwhile the table is checked if it contains number of cards equal to number of players
    if so, the stage is over, the score is updated and cards deleted
    when currentPlayer has no more cards to play, tha game ends


 */


//
//    public fun pickTalon(player: Player, )
    // distributeTalon
// getState
    //
}