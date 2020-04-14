package oneK.game

import oneK.deck.Hand
import oneK.game.events.GameEvent
import oneK.game.events.GameEventPublisher
import oneK.game.strategy.GameVariant
import oneK.player.Player

class BiddingService(var players: List<Player>, private val gameVariant: GameVariant, private val eventPublisher: GameEventPublisher) {
    var currentPlayer = players[1]
    var currentBid = gameVariant.getInitialBid()
    var bidders: MutableMap<Player, Boolean> = newBidders()
    var biddingEnded = false
    fun newBidders() = mutableMapOf(*players.map { Pair(it, true) }.toTypedArray())

    fun canBid(hand: Hand, bid: Int): Boolean {
        return bid > currentBid &&
                this.gameVariant.canBid(hand, bid) &&
                !biddingEnded &&
                bid % 10 == 0 &&
                bid - currentBid <= this.gameVariant.getMaxBidStep() &&
                bid <= gameVariant.getUpperBidThreshold()
    }


    fun fold() {
        bidders[currentPlayer] = false
        if (bidders.values.filter { it }.size <= 1) endBidding()
        else nextPlayer()
    }

    fun bid(hand: Hand, bid: Int) {
        require(canBid(hand, bid))
        this.currentBid = bid
        nextPlayer()
    }

    fun nextPlayer() {
        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = if (currentIndex == players.size - 1) 0 else currentIndex + 1

        this.currentPlayer = players.elementAt(nextIndex)
        if (!bidders[currentPlayer]!!) return nextPlayer()
        else this.eventPublisher.publish(GameEvent.PLAYER_CHANGED)
    }

    fun endBidding() {
        val biddingEntries = bidders.filter { entry -> entry.value }
        require(biddingEntries.size == 1)
        val biddingWinner = biddingEntries.keys.first()
        currentPlayer = biddingWinner

        this.biddingEnded = true
        this.eventPublisher.publish(GameEvent.BIDDING_ENDED)
    }
}