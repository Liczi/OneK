package oneK.game

import oneK.deck.Card
import oneK.deck.Hand
import oneK.game.events.GameEvent
import oneK.game.events.GameEventListener
import oneK.game.events.GameEventPublisher
import oneK.game.strategy.GameStrategy
import oneK.player.Player
import oneK.round.Round
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventListener
import oneK.round.strategy.RoundStrategy

public val MAXIMUM_BID = 400
public val GAME_GOAL = 1000

class Game(private var players: List<Player>,
           private val gameStrategy: GameStrategy,
           private val roundStrategy: RoundStrategy) {

    //initialize after bidding
    private var currentRound: Round? = null
    private var currentBid = gameStrategy.getInitialBid()
    private var currentPlayer = players[1]

    private var winner: Player? = null
    private var ranking: MutableMap<Player, Int>

    private var biddingEnded = false
    //private var roundNumber = 1

    private var hands: LinkedHashMap<Player, Hand>
    private var bidders: MutableMap<Player, Boolean>

    private val eventPublisher = GameEventPublisher()

    init {
        ranking = mutableMapOf(*(players.map { Pair(it, 0) }.toTypedArray())) //todo make sure reading from state is not overriding
        bidders = newBidders()
        hands = linkedMapOf()

        shuffleAndAssign()
        this.eventPublisher.publish(GameEvent.ROUND_INITIALIZED)
    }

    private fun clearRoundData() {
        currentRound = null
        currentBid = gameStrategy.getInitialBid()
        currentPlayer = players[1]

        bidders = newBidders()
        hands = linkedMapOf()
        biddingEnded = false
    }

    private fun newBidders() = mutableMapOf(*players.map { Pair(it, true) }.toTypedArray())

    //todo handle events
    private val roundEventListener = object : RoundEventListener {
        override fun onEvent(event: RoundEvent) {
            when (event) {
                RoundEvent.ROUND_ENDED -> handleEndRound()
                else -> return
            }
        }
    }

    private fun shuffleAndAssign() {
        val cards = Hand.getClassicDeck().shuffled().toMutableList()
        assignTalonCards(cards)
        assignPlayerCards(cards, players)
    }

    private fun handleEndRound() {
        val roundScore = this.currentRound!!.getCurrentScore()
        val threshold = this.gameStrategy.getLimitedScoringThreshold()
        val biddingPlayer = this.currentRound!!.biddingPlayer

        for ((player, ranking) in ranking) {
            if (!(this.ranking[player]!! >= threshold &&
                    player != biddingPlayer)) {
                this.ranking.replace(player, ranking + roundScore[player]!!)
            }
        }

        //this.biddingEnded = true //block until newRound called
//        shuffleAndAssign()
        checkForWinner()
    }

    private fun endBidding() {
        val biddingEntries = bidders.filter { entry -> entry.value }
        require(biddingEntries.size == 1)
        val biddingWinner = biddingEntries.keys.first()
        this.currentPlayer = biddingWinner

        val newOrder = listPlayersFrom(biddingWinner)

        this.biddingEnded = true
        this.eventPublisher.publish(GameEvent.BIDDING_ENDED)

        this.startRound(newOrder)
    }

    private fun startRound(players: List<Player>) {
        require(currentRound == null && biddingEnded)

        this.currentRound = Round(players, roundStrategy, currentBid, hands)
        this.currentRound!!.registerListener(roundEventListener)
        this.eventPublisher.publish(GameEvent.ROUND_INITIALIZED)
    }

    private fun listPlayersFrom(first: Player): MutableList<Player> {
        val newOrder = mutableListOf(first)
        var index = players.indexOf(first)
        var count = 1
        while (count < players.size) {
            index++
            if (index >= players.size) index = 0
            newOrder.add(players[index])
            count++
        }
        return newOrder
    }

    private fun nextPlayer() {
        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = if (currentIndex == players.size - 1) 0 else currentIndex + 1

        this.currentPlayer = players.elementAt(nextIndex)
        if (!bidders[currentPlayer]!!) return nextPlayer()
        this.eventPublisher.publish(GameEvent.PLAYER_CHANGED)
    }

    private fun checkForWinner() {
        //todo consider multiple winners
        if (this.ranking.values.all { it < GAME_GOAL }) return

        this.winner = this.ranking.entries.first { it.value >= GAME_GOAL }.key
        this.eventPublisher.publish(GameEvent.GAME_ENDED)
    }

    private fun assignPlayerCards(cards: MutableList<Card>, players: List<Player>) {
        require(cards.size % players.size == 0)
        val playerCardsQuant = cards.size / players.size
        players.forEach { player ->
            val playerCards = cards.subList(0, playerCardsQuant)
            hands.put(player, Hand(playerCards.toTypedArray()))
            cards.removeAll(playerCards)
        }
        require(cards.size == 0)
    }

    private fun assignTalonCards(cards: MutableList<Card>) {
        val talonCardsQuant = roundStrategy.getTalonSize() * roundStrategy.getTalonsQuantity()
        val talonCards = pickCards(talonCardsQuant, cards)
        roundStrategy.setTalonCards(talonCards)
    }

    private fun pickCards(quant: Int, cards: MutableList<Card>): HashSet<Card> {
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

    public fun getCurrentRound() = this.currentRound


    public fun nextRound(firstPlayer: Player) {
        require(this.winner == null && this.currentRound?.roundHasEnded ?: false)
        //this.roundNumber++
        this.clearRoundData()
        this.players = listPlayersFrom(firstPlayer)
        this.currentPlayer = players[1]
        shuffleAndAssign()
        this.eventPublisher.publish(GameEvent.BIDDING_STARTED)
        //startRound(listPlayersFrom(firstPlayer))
    }

    public fun canBid(hand: Hand, bid: Int): Boolean {
        return bid > currentBid &&
                this.gameStrategy.canBid(hand, bid) &&
                !biddingEnded &&
                bid % 10 == 0 &&
                currentBid - bid <= this.gameStrategy.getMaxBidStep() &&
                bid <= gameStrategy.getUpperBidThreshold()
    }

    public fun bid(bid: Int) {
        require(canBid(hands[currentPlayer]!!, bid))
        this.currentBid = bid
        nextPlayer()
    }

    public fun fold() {
        bidders.put(currentPlayer, false)
        if (bidders.values.filter { it }.size <= 1) endBidding()
        else nextPlayer()
    }

    public fun registerListener(listener: GameEventListener) {
        this.eventPublisher.addListener(listener)
    }

    public fun registerListener(listener: RoundEventListener) = this.currentRound?.registerListener(listener)
}