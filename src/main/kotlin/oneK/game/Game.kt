package oneK.game

import oneK.deck.Card
import oneK.deck.Hand
import oneK.game.events.GameEvent
import oneK.game.events.GameEventListener
import oneK.game.events.GameEventPublisher
import oneK.game.strategy.GameVariant
import oneK.player.Player
import oneK.round.Round
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventListener
import oneK.round.strategy.RoundVariant
import java.io.IOException
import java.io.Serializable
import java.util.* // TODO ...

// TODO wrong place for these constants
val MAXIMUM_BID = 400
val GAME_GOAL = 1000

//TODO prepare full documentation (what is implemented, future work, how mechanics can be changed through Variants
class Game(players: List<Player>,
           eventPublisher: GameEventPublisher,
           private val biddingService: BiddingService,
           private val gameVariant: GameVariant,
           private val roundVariant: RoundVariant) : Serializable {

    //TODO add empty constructor with satisfied dependencies
    var winner: Player? = null


    //initialize after bidding
    var currentRound: Round? = null
        private set(value) {
            field = value
        }

    private var ranking: MutableMap<Player, Int>

    //private var roundNumber = 1

    private var hands: LinkedHashMap<Player, Hand>

    //    TODO extract to parent abstract class
//    registerListener...
    @Transient
    private var eventPublisher: GameEventPublisher = eventPublisher

    //todo handle events
    @Transient
    private var roundEventListener: RoundEventListener

    init {
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
        hands = linkedMapOf()

        shuffleAndAssign()
        this.eventPublisher.publish(GameEvent.BIDDING_STARTED)

        currentRound?.registerListener(roundEventListener)
    }

    //    TODO will be deleted
//    just create a new state object
    private fun clearRoundData() {
        currentRound = null
        biddingService.currentBid = gameVariant.getInitialBid()
        biddingService.currentPlayer = biddingService.players[1]

        biddingService.bidders = biddingService.newBidders()
        hands = linkedMapOf()
        biddingService.biddingEnded = false
    }


    private fun shuffleAndAssign() {
        val cards = Hand.getClassicDeck().shuffled().toMutableList()
        assignTalonCards(cards)
        assignPlayerCards(cards, biddingService.players)
    }

    private fun handleEndRound() {
        val roundScore = this.currentRound!!.getCurrentScore()
        val threshold = this.gameVariant.getLimitedScoringThreshold()
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

    fun fold() {
        biddingService.fold()
//        TODO extract this responsibility (EVENT)?
        if (biddingService.biddingEnded) this.startRound(listPlayersFrom(biddingService.currentPlayer, biddingService.players)
        )

    }

    fun bid(bid: Int) {
        biddingService.bid(hands[biddingService.currentPlayer]!!, bid)
    }

    private fun startRound(players: List<Player>) {
        require(currentRound == null && biddingService.biddingEnded)

        this.currentRound = Round(players, roundVariant, biddingService.currentBid, hands)
        this.currentRound!!.registerListener(roundEventListener)
        this.eventPublisher.publish(GameEvent.ROUND_INITIALIZED)
    }

    private fun checkForWinner() {
        //todo consider multiple winners
        if (this.ranking.values.all { it < GAME_GOAL }) return

        winner = this.ranking.entries.first { it.value >= GAME_GOAL }.key
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
        val talonCardsQuant = roundVariant.getTalonSize() * roundVariant.getTalonsQuantity()
        val talonCards = pickCards(talonCardsQuant, cards)
        roundVariant.setTalonCards(talonCards)
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

    fun getPlayerNames() = biddingService.players.map { it.name }.toTypedArray()

    fun getRankingValues() = biddingService.players.map { this.ranking[it]!! }.toIntArray()

    //    TODO delete state
//    TODO just create new state object
    fun nextGameStage(firstPlayer: Player) {
        require(winner == null && this.currentRound?.roundHasEnded ?: false)
        //this.roundNumber++
        this.clearRoundData()
        biddingService.players = listPlayersFrom(firstPlayer, biddingService.players)
        biddingService.currentPlayer = biddingService.players[1]
        shuffleAndAssign()
        this.eventPublisher.publish(GameEvent.BIDDING_STARTED)
    }

    fun getPlayerRanking(player: Player) = this.ranking[player]!!

    fun getPlayerHand(player: Player) = this.hands[player]!!

    fun registerListener(listener: GameEventListener) {
        this.eventPublisher.addListener(listener)
    }

    fun registerListener(listener: RoundEventListener) = this.currentRound?.registerListener(listener)

}