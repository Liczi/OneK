package oneK.game

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure
import oneK.deck.Hand
import oneK.player.Player
import oneK.strategy.RoundStrategy


//tODO test firmly
class Round(players: List<Player>,
            internal val strategy: RoundStrategy,
            internal var bid: Int,
            var currentPlayer: Player) {

    internal val table: MutableMap<Player, Card>
    internal val hands: MutableMap<Player, Hand>
    val score: MutableMap<Player, Int>

    internal var gameIsLocked = true
    internal var currentTrump: Color? = null
    internal var lastLeftWinner = currentPlayer


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

        while (i < quant) {
            val index = Math.round(Math.random() * (cards.size - 1)).toInt()
            result.add(cards[index])
            cards.removeAt(index)
            i++
        }

        return result
    }

/* TODO procedure
    if the cards given to player are not valid he could decide to re-draw
    the current player begins, the game is locked (he cant place any
    he picks talon
    can change bet here or call bomb according to the strategy
    redistibutes talon, the game unlocks here

    he can place a card on the board, and the current player is changed
    meanwhile the table is checked if it contains number of cards equal to number of players
    if so, the stage is over, the score is updated and cards deleted
    when currentPlayer has no more cards to play, tha game ends
 */


    public fun isHandValid(hand: Hand): Boolean {
        return this.strategy.isValid(hand)
    }

    /**
     * Current player takes talon
     */
    public fun pickTalon(talonIndex: Int): HashSet<Card> {
        TODO("Not implemented")
    }

    public fun changeBid(newBid: Int) {
        require(newBid in this.bid..MAXIMUM_BID)
        TODO("Not implemented")
    }

    /**
     * Current player distributes card, that he owns to other player
     */
    public fun distributeCard(to: Player, card: Card) {
        require(to != currentPlayer && currentPlayer.has(card))
        //TODO when last card is distributed, lock is disabled ---- CONSIDER MAP
        TODO("Implement")
    }

    /**
     * Current player plays specified card on the table
     */
    public fun play(card: Card) {
        require(currentPlayer.has(card))

        TODO("Implement")
    }

    /**
     * Announces trump and plays specified card
     */
    public fun triumph(card: Card) {
        require(card.figure == Figure.KING || card.figure == Figure.QUEEN)
        val triumphCard = if (card.figure == Figure.QUEEN) Card(Figure.KING, card.color) else Card(Figure.QUEEN, card.color)
        require(hands[currentPlayer]!!.cards.contains(triumphCard))

        TODO("Implement")
    }

    //EXTENSION
    fun Player.has(card: Card) = hands[currentPlayer]!!.cards.contains(card)
}