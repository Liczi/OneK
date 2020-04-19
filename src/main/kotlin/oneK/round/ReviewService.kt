package oneK.round

import oneK.deck.Card
import oneK.deck.Hand
import oneK.game.MAXIMUM_BID
import oneK.player.Player
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventListener
import oneK.round.events.RoundEventPublisher
import oneK.round.strategy.RoundVariant

class ReviewService(
        private val players: List<Player>,
        private var hands: LinkedHashMap<Player, Hand>,
        private var bid: Int,
        private val variant: RoundVariant,
        private val eventPublisher: RoundEventPublisher
) {

    var currentPlayer = players[0]
    //    TODO not needed when introduced valid states and transitions
    var gameIsLocked = true

    fun canChangeBid(newBid: Int) = newBid in this.bid..MAXIMUM_BID &&
            newBid % 10 == 0 &&
            this.gameIsLocked &&
            !roundHasEnded

    fun changeBid(newBid: Int) {
        require(canChangeBid(newBid))
        this.bid = newBid
        this.eventPublisher.publish(RoundEvent.BID_CHANGED)
    }

    fun canRestart(hand: Hand): Boolean {
        return !this.variant.qualifiesForRestart(hand) //TODO check for proper state instead of: && this.summaryService.score.values.all { it == 0 }
    }

    // TODO restart can be done only, exactly, after bidding
    fun restart(restartingHand: Hand) {
        //we can restart game on our turn so the game not necessarily should be locked or unlocked
        //only additional req is that no cards were scored
        require(canRestart(restartingHand))
        this.strifeService.table.clear()
        this.hands.clear()
        this.summaryService.score.clear()

        this.gameIsLocked = true
        this.roundHasEnded = false
        strifeService.currentTrump = null
        this.currentPlayer = players[0]
        //shuffleAndAssign()
        this.eventPublisher.publish(RoundEvent.ROUND_RESTARTED)
    }


    /**
     * Current player takes talon to the hand
     */
    fun pickTalon(talonIndex: Int) {
        require(talonIndex in 0 until this.variant.getTalonsQuantity() && this.gameIsLocked && !roundHasEnded)
        this.hands[currentPlayer]!!.cards.addAll(this.variant.getTalons()[talonIndex])
        this.eventPublisher.publish(RoundEvent.TALON_PICKED)
    }

    /**
     * Current player distributes card, that he owns to other player
     */
    fun distributeCards(toGive: Map<Player, Card>) {
        require(!toGive.containsKey(currentPlayer) &&
                toGive.size == this.hands.size - 1 &&
                toGive.values.all { card -> currentPlayer.has(card) } &&
                this.gameIsLocked)

        toGive.forEach { entry -> hands[entry.key]!!.cards.add(entry.value) }
        this.hands[currentPlayer]!!.cards.removeAll(toGive.values)
        this.eventPublisher.publish(RoundEvent.TALON_DISTRIBUTED)

        this.gameIsLocked = false
        strifeService.gameIsLocked = false
        this.eventPublisher.publish(RoundEvent.ROUND_STARTED)
    }

    //    todo add limit
    fun activateBomb() {
        require(this.gameIsLocked && variant.getBombAllowedBidThreshold() >= bid)

        val opponents = players.filter { it != currentPlayer }
        opponents.forEach { summaryService.addPoints(it, variant.getBombPoints()) }
        this.eventPublisher.publish(RoundEvent.BOMB_ACTIVATED)
        this.roundHasEnded = true
        this.eventPublisher.publish(RoundEvent.ROUND_ENDED)
    }

    fun registerListener(listener: RoundEventListener) {
        this.eventPublisher.addListener(listener)
    }

}