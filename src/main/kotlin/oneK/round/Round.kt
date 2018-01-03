package oneK.round

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure
import oneK.deck.Hand
import oneK.game.MAXIMUM_BID
import oneK.player.Player
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventListener
import oneK.round.events.RoundEventPublisher
import oneK.round.strategy.RoundStrategy
import java.io.IOException
import java.io.Serializable
import kotlin.math.round

//TODO
//Think about listeners concept (bomb, end of round, end of stage, restart...)
//Maybe event system ?

class Round(private val players: List<Player>,
            private val strategy: RoundStrategy,
            private var bid: Int,
            private var hands: LinkedHashMap<Player, Hand>) : Serializable {

    private val table: LinkedHashMap<Player, Card>
    private var score: MutableMap<Player, Int>

    private var gameIsLocked = true
    var roundHasEnded = false
    private var currentTrump: Color? = null
    var currentPlayer = players[0]
        private set(value) {
            field = value
        }
    val biddingPlayer = currentPlayer

    @Transient
    private var eventPublisher: RoundEventPublisher = RoundEventPublisher()


    init {
        this.table = linkedMapOf()
        this.score = mutableMapOf(*(players.map { Pair(it, 0) }.toTypedArray()))
    }


    /**
     * Bidding player is confronted against his bid and score table is rounded to decimal
     */
    private fun endRound() {
        require(this.score.keys.contains(biddingPlayer))
        var bidderScore = this.score[biddingPlayer]!!
        bidderScore = if (bidderScore >= bid) bid else -bid

        this.score[biddingPlayer] = bidderScore

//        this.score.replace(biddingPlayer, bidderScore)
        this.score.round()
        this.roundHasEnded = true
        this.eventPublisher.publish(RoundEvent.ROUND_ENDED)
    }

    /**
     * All cards from table are scored to player which wins and discarded
     */
    private fun endStage() {
        val ranking = mutableListOf<Int>()
        val players = mutableListOf<Player>()
        var count = 1
        var firstColor: Color? = null
        for ((player, card) in this.table) {
            firstColor = if (firstColor == null) card.color else firstColor
            val firstBonus = if (card.color == firstColor) 100 else 0
            val playerRanking = card.figure.value + firstBonus + if (card.color == this.currentTrump) 200 else 1
            count--
            ranking.add(playerRanking)
            players.add(player)
        }
        val winningPlayer = players[ranking.indexOf(ranking.max())]

        val tableCardsValueSum = this.table.values.sumBy { it.figure.value }
        addPoints(winningPlayer, tableCardsValueSum)
        this.table.clear()
        this.currentPlayer = winningPlayer
        this.eventPublisher.publish(RoundEvent.STAGE_ENDED)
    }

    private fun addPoints(player: Player, points: Int) {
        require(this.score.keys.contains(player))
        val previousScore = this.score[player]!!
        this.score[player] = previousScore + points

//        this.score.replace(player, previousScore + points)
    }

    private fun nextPlayer() {
        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = if (currentIndex == players.size - 1) 0 else currentIndex + 1
        this.currentPlayer = players.elementAt(nextIndex)
        this.eventPublisher.publish(RoundEvent.PLAYER_CHANGED)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: java.io.ObjectInputStream) {
        `in`.defaultReadObject()
        this.eventPublisher = RoundEventPublisher()
    }


    public fun getPlayerNames() = this.players.map { it.name }.toTypedArray()

    public fun getScoreValues() = this.players.map { this.score[it]!! }.toIntArray()

    public fun restart(restartingHand: Hand) {
        //we can restart game on our turn so the game not necessarily should be locked or unlocked
        //only additional req is that no cards were scored
        require(canRestart(restartingHand))
        this.table.clear()
        this.hands.clear()
        this.score.clear()

        this.gameIsLocked = true
        this.roundHasEnded = false
        this.currentTrump = null
        this.currentPlayer = players[0]
        //shuffleAndAssign()
        this.eventPublisher.publish(RoundEvent.ROUND_RESTARTED)
    }

    public fun canRestart(hand: Hand): Boolean {
        return !this.strategy.isValid(hand) && score.values.all { it == 0 }
    }

    /**
     * Current player takes talon to the hand
     */
    public fun pickTalon(talonIndex: Int) {
        require(talonIndex in 0..(this.strategy.getTalonsQuantity() - 1) && this.gameIsLocked && !roundHasEnded)
        this.hands[currentPlayer]!!.cards.addAll(this.strategy.getTalons()[talonIndex])
        this.eventPublisher.publish(RoundEvent.TALON_PICKED)
    }

    public fun canChangeBid(newBid: Int) = newBid in this.bid..MAXIMUM_BID &&
            newBid % 10 == 0 &&
            this.gameIsLocked &&
            !roundHasEnded

    public fun changeBid(newBid: Int) {
        require(canChangeBid(newBid))
        this.bid = newBid
        this.eventPublisher.publish(RoundEvent.BID_CHANGED)
    }

    public fun activateBomb() {
        require(this.gameIsLocked && strategy.getBombAllowedBidThreshold() >= bid)

        val opponents = players.filter { it != currentPlayer }
        opponents.forEach { addPoints(it, strategy.getBombPoints()) }
        this.eventPublisher.publish(RoundEvent.BOMB_ACTIVATED)
        this.roundHasEnded = true
        this.eventPublisher.publish(RoundEvent.ROUND_ENDED)
    }

    /**
     * Current player distributes card, that he owns to other player
     */
    public fun distributeCards(toGive: Map<Player, Card>) {
        require(!toGive.containsKey(currentPlayer) &&
                toGive.size == this.hands.size - 1 &&
                toGive.values.all { card -> currentPlayer.has(card) } &&
                this.gameIsLocked)

        toGive.forEach { entry -> hands[entry.key]!!.cards.add(entry.value) }
        this.hands[currentPlayer]!!.cards.removeAll(toGive.values)
        this.eventPublisher.publish(RoundEvent.TALON_DISTRIBUTED)

        this.gameIsLocked = false
        this.eventPublisher.publish(RoundEvent.ROUND_STARTED)
    }

    /**
     * Current player plays specified card on the table
     */
    public fun play(card: Card) {
        require(currentPlayer.has(card) && !this.table.containsKey(currentPlayer) && !gameIsLocked)

        this.table.put(currentPlayer, card)
        this.eventPublisher.publish(RoundEvent.CARD_PLAYED)
        this.hands[currentPlayer]!!.cards.remove(card)
        nextPlayer()

        if (this.table.size >= this.hands.size)
            endStage()
        if (this.hands.empty())
            endRound()
    }

    /**
     * Announces trump and plays specified card
     */
    public fun triumph(card: Card) {
        require(canPlayTriumph(card, currentPlayer))

        this.currentTrump = card.color
        this.eventPublisher.publish(RoundEvent.TRIUMPH_CHANGED)
        addPoints(currentPlayer, card.color.value)
        play(card)
    }

    public fun canPlayTriumph(card: Card, player: Player): Boolean {
        if (!((card.figure == Figure.KING || card.figure == Figure.QUEEN) &&
                !gameIsLocked &&
                table.isEmpty())) return false
        val otherTriumphCard =
                if (card.figure == Figure.QUEEN) Card(Figure.KING, card.color)
                else Card(Figure.QUEEN, card.color)

        if (!(hands[player]!!.contains(otherTriumphCard))) return false

        return true
    }

    public fun registerListener(listener: RoundEventListener) {
        this.eventPublisher.addListener(listener)
    }

    public fun getHandOf(player: Player) = hands[player]

    public fun getCurrentScore() = this.score

    //EXTENSION
    private fun Player.has(card: Card) = hands[currentPlayer]!!.contains(card)

    fun MutableMap<Player, Hand>.empty() = this.values.all { hand -> hand.cards.isEmpty() }

    private fun MutableMap<Player, Int>.round() {

        val itr = this.keys.iterator()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = this[key]!!.toDouble()
            this.set(key, round(value).toInt())
        }

//        this.keys.forEach { player ->
//            val currentScore = this[player]!!.toDouble()
//            this.replace(player, round(currentScore).toInt())
//        }
    }
}