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
import oneK.round.strategy.Variant
import java.io.IOException
import java.io.Serializable
import java.util.* // TODO ...

// TODO wrong place for these constants
val MAXIMUM_BID = 400
val GAME_GOAL = 1000

//TODO prepare full documentation (what is implemented, future work, how mechanics can be changed through Variants
class Game(players: List<Player>,
           private val gameStrategy: GameStrategy,
           private val variant: Variant) : Serializable {

    var players = players
        private set(value) {
            field = value
        }
    //initialize after bidding
    var currentRound: Round? = null
        private set(value) {
            field = value
        }
    var currentBid: Int = -1
        private set(value) {
            field = value
        }
    var currentPlayer = players[1]
        private set(value) {
            field = value
        }

    var winner: Player? = null
        private set(value) {
            field = value
        }
    private var ranking: MutableMap<Player, Int>

    var biddingEnded = false
        private set(value) {
            field = value
        }
    //private var roundNumber = 1

    private var hands: LinkedHashMap<Player, Hand>
    private var bidders: MutableMap<Player, Boolean>

    @Transient
    private var eventPublisher: GameEventPublisher = GameEventPublisher()

    //todo handle events
    @Transient
    private var roundEventListener: RoundEventListener

    init {
        this.currentBid = this.gameStrategy.getInitialBid()

        roundEventListener = object : RoundEventListener {
            override fun onEvent(event: RoundEvent) {
                when (event) {
                    RoundEvent.ROUND_ENDED -> {
                        handleEndRound()
                    }
                    else -> return
                }
            }
        }

        ranking = mutableMapOf(*(players.map { Pair(it, 0) }.toTypedArray())) //todo make sure reading from state is not overriding
        bidders = newBidders()
        hands = linkedMapOf()

        shuffleAndAssign()
        this.eventPublisher.publish(GameEvent.BIDDING_STARTED)

        currentRound?.registerListener(roundEventListener)
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

    private fun shuffleAndAssign() {
        val cards = Hand.getClassicDeck().shuffled().toMutableList()
        assignTalonCards(cards)
        assignPlayerCards(cards, players)
    }

    private fun handleEndRound() {
        val roundScore = this.currentRound!!.getCurrentScore()
        val threshold = this.gameStrategy.getLimitedScoringThreshold()
        val biddingPlayer = this.currentRound!!.biddingPlayer

        val itr = this.ranking.iterator()
        while (itr.hasNext()) {
            val curr = itr.next()
            val player = curr.key
            val ranking = curr.value

            if (!(ranking >= threshold &&
                    player != biddingPlayer)) {
                this.ranking[player] = ranking + roundScore[player]!!
            }
        }
        eventPublisher.publish(GameEvent.RANKING_CHANGED)

//        for ((player, ranking) in ranking) {
//            if (!(this.ranking[player]!! >= threshold &&
//                    player != biddingPlayer)) {
//                this.ranking.replace(player, ranking + roundScore[player]!!)
//            }
//        }

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

        this.currentRound = Round(players, variant, currentBid, hands)
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
        else this.eventPublisher.publish(GameEvent.PLAYER_CHANGED)
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
        val toRemove = mutableListOf<Card>()
        for ((index, player) in players.withIndex()) {
            val playerCards = cards.subList(0 + playerCardsQuant * index, playerCardsQuant * (index + 1))
            hands.put(player, Hand(playerCards.toTypedArray()))
            toRemove.addAll(playerCards)
        }
        cards.removeAll(toRemove)
        require(cards.size == 0)
    }

    private fun assignTalonCards(cards: MutableList<Card>) {
        val talonCardsQuant = variant.getTalonSize() * variant.getTalonsQuantity()
        val talonCards = pickCards(talonCardsQuant, cards)
        variant.setTalonCards(talonCards)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: java.io.ObjectInputStream) {
        `in`.defaultReadObject()
        eventPublisher = GameEventPublisher()
        roundEventListener = object : RoundEventListener {
            override fun onEvent(event: RoundEvent) {
                when (event) {
                    RoundEvent.ROUND_ENDED -> {
                        handleEndRound()
                    }
                    else -> {
                        return
                    }
                }
            }
        }
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


    fun getPlayerNames() = this.players.map { it.name }.toTypedArray()

    fun getRankingValues() = this.players.map { this.ranking[it]!! }.toIntArray()

    fun nextGameStage(firstPlayer: Player) {
        require(this.winner == null && this.currentRound?.roundHasEnded ?: false)
        //this.roundNumber++
        this.clearRoundData()
        this.players = listPlayersFrom(firstPlayer)
        this.currentPlayer = players[1]
        shuffleAndAssign()
        this.eventPublisher.publish(GameEvent.BIDDING_STARTED)
    }

    fun canBid(hand: Hand, bid: Int): Boolean {
        return bid > currentBid &&
                this.gameStrategy.canBid(hand, bid) &&
                !biddingEnded &&
                bid % 10 == 0 &&
                bid - currentBid <= this.gameStrategy.getMaxBidStep() &&
                bid <= gameStrategy.getUpperBidThreshold()
    }

    fun bid(bid: Int) {
        require(canBid(hands[currentPlayer]!!, bid))
        this.currentBid = bid
        nextPlayer()
    }

    fun fold() {
        bidders.put(currentPlayer, false)
        if (bidders.values.filter { it }.size <= 1) endBidding()
        else nextPlayer()
    }

    fun getPlayerRanking(player: Player) = this.ranking[player]!!

    fun getPlayerHand(player: Player) = this.hands[player]!!

    fun registerListener(listener: GameEventListener) {
        this.eventPublisher.addListener(listener)
    }

    fun registerListener(listener: RoundEventListener) = this.currentRound?.registerListener(listener)

}