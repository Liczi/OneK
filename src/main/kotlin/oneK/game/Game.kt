package oneK.game

import oneK.deck.Card
import oneK.deck.Hand
import oneK.game.events.GameEvent
import oneK.game.events.GameEventPublisher
import oneK.player.Player
import oneK.round.Round
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventListener
import oneK.strategy.GameStrategy
import oneK.strategy.RoundStrategy

public val MAXIMUM_BID = 400
public val GAME_GOAL = 1000

class Game(private val players: List<Player>,
           private val gameStrategy: GameStrategy,
           private val roundStrategy: RoundStrategy) {

    //initialize after bidding
    private var currentRound: Round? = null
    private var currentBid = gameStrategy.getInitialBid()
    private var currentPlayer = players[1]
    var winner: Player? = null

    var biddingEnded = false
    val ranking: MutableMap<Player, Int>
    internal var hands: LinkedHashMap<Player, Hand>
    internal val bidders: MutableMap<Player, Boolean>

    internal val eventPublisher = GameEventPublisher()

    init {
        ranking = mutableMapOf(*(players.map { Pair(it, 0) }.toTypedArray())) //todo make sure reading from state is not overriding
        bidders = mutableMapOf(*players.map { Pair(it, true) }.toTypedArray())
        hands = linkedMapOf()

        shuffleAndAssign()
    }

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
        val roundScore = this.currentRound!!.score
        val threshold = this.gameStrategy.getLimitedScoringThreshold()
        val biddingPlayer = this.currentRound!!.biddingPlayer

        for ((player, ranking) in ranking) {
            if (!(this.ranking[player]!! >= threshold &&
                    player != biddingPlayer)) {
                this.ranking.replace(player, ranking + roundScore[player]!!)
            }
        }

        checkForWinner()
    }

    private fun endBidding() {
        val biddingEntries = bidders.filter { entry -> entry.value }
        require(biddingEntries.size == 1)
        val biddingWinner = biddingEntries.keys.first()

        val newOrder = mutableListOf(biddingWinner)
        var index = players.indexOf(biddingWinner)
        var count = 1
        while (count < players.size) {
            index++
            if (index >= players.size) index = 0
            newOrder.add(players[index])
            count++
        }

        this.biddingEnded = true
        this.eventPublisher.publish(GameEvent.BIDDING_ENDED)

        this.startRound(newOrder)
    }

    //todo test for deadloop
    private fun nextPlayer() {
        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = if (currentIndex == players.size - 1) 0 else currentIndex + 1

        this.currentPlayer = players.elementAt(nextIndex)
        if (!bidders[currentPlayer]!!) return nextPlayer()
        this.eventPublisher.publish(GameEvent.PLAYER_CHANGED)
    }

    internal fun checkForWinner() {
        //todo consider multiple winners
        if (this.ranking.values.all { it < GAME_GOAL }) return

        this.winner = this.ranking.entries.first { it.value >= GAME_GOAL }.key
        this.eventPublisher.publish(GameEvent.GAME_ENDED)
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
        val talonCardsQuant = roundStrategy.getTalonSize() * roundStrategy.getTalonsQuantity()
        val talonCards = pickCards(talonCardsQuant, cards)
        roundStrategy.setTalonCards(talonCards)
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

    public fun startRound(players: List<Player>) {
        require(currentRound == null && biddingEnded)

        this.currentRound = Round(players, roundStrategy, currentBid, hands)
        this.currentRound!!.registerListener(roundEventListener)
        this.eventPublisher.publish(GameEvent.ROUND_INITIALIZED)
    }


    public fun canBid(hand: Hand, bid: Int): Boolean {
        return this.gameStrategy.canBid(hand, bid) &&
                !biddingEnded &&
                bid % 10 == 0 &&
                bid - currentBid <= gameStrategy.getMaxBidStep() &&
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
    }
}