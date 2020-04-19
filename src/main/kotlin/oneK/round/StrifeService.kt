package oneK.round

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure
import oneK.deck.Hand
import oneK.player.Player
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventPublisher

//TODO handle correct order of arguments
class StrifeService(
        private val players: List<Player>,
        var currentPlayer: Player,
        private var hands: LinkedHashMap<Player, Hand>,
        private val summaryService: SummaryService,
        private var eventPublisher: RoundEventPublisher
        ) {
    val table: LinkedHashMap<Player, Card> = linkedMapOf()
    var currentTrump: Color? = null

    var gameIsLocked = true

    fun canPlay(card: Card, player: Player): Boolean {
        return player.has(card) &&
                !this.table.containsKey(player) &&
                !gameIsLocked
    }

    /**
     * Current player plays specified card on the table
     */
    fun play(card: Card) {
        require(canPlay(card, currentPlayer))

        this.table.put(currentPlayer, card)
        this.hands[currentPlayer]!!.cards.remove(card)
        this.eventPublisher.publish(RoundEvent.CARD_PLAYED)
        nextPlayer()

        if (this.table.size >= this.hands.size)
            endStage()
        if (this.hands.allEmpty())
            summaryService.endRound()
    }

    /**
     * Announces trump and plays specified card
     */
    fun triumph(card: Card) {
        require(canPlayTriumph(card, currentPlayer))

        this.currentTrump = card.color
        summaryService.addPoints(currentPlayer, card.color.value) //TODO internal score
        play(card)
        this.eventPublisher.publish(RoundEvent.TRIUMPH_CHANGED)
    }

    fun canPlayTriumph(card: Card, player: Player): Boolean {
        if (!((card.figure == Figure.KING || card.figure == Figure.QUEEN) &&
                        !gameIsLocked &&
                        table.isEmpty())) return false
        val otherTriumphCard =
                if (card.figure == Figure.QUEEN) Card(Figure.KING, card.color)
                else Card(Figure.QUEEN, card.color)

        if (!(hands[player]!!.contains(otherTriumphCard))) return false

        return true
    }

    private fun nextPlayer() {
        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = if (currentIndex == players.size - 1) 0 else currentIndex + 1
        this.currentPlayer = players.elementAt(nextIndex)
        this.eventPublisher.publish(RoundEvent.PLAYER_CHANGED)
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
            firstColor = firstColor ?: card.color
            val firstBonus = if (card.color == firstColor) 100 else 0
            val playerRanking = card.figure.value + firstBonus + if (card.color == this.currentTrump) 200 else 1
            count--
            ranking.add(playerRanking)
            players.add(player)
        }
        val winningPlayer = players[ranking.indexOf(ranking.max())]

        val tableCardsValueSum = this.table.values.sumBy { it.figure.value }
        summaryService.addPoints(winningPlayer, tableCardsValueSum)
        this.table.clear()
        this.currentPlayer = winningPlayer
        this.eventPublisher.publish(RoundEvent.PLAYER_CHANGED)
        this.eventPublisher.publish(RoundEvent.STAGE_ENDED)
    }

    fun getHandOf(player: Player) = hands[player]




    fun getTableContents() = this.table.map { it.value }.toList()

    fun Player.has(card: Card) = hands[currentPlayer]!!.contains(card)

    fun MutableMap<Player, Hand>.allEmpty() = this.values.all { hand -> hand.cards.isEmpty() }
}

