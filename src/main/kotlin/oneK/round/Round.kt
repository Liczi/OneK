package oneK.round

import oneK.deck.Card
import oneK.deck.Hand
import oneK.game.MAXIMUM_BID
import oneK.player.Player
import oneK.round.events.RoundEvent
import oneK.round.events.RoundEventListener
import oneK.round.events.RoundEventPublisher
import oneK.round.strategy.RoundVariant

class Round(private val players: List<Player>,
            val biddingPlayer: Player,
            private val strifeService: StrifeService,
            private val summaryService: SummaryService,
            private val reviewService: ReviewService,
            private val variant: RoundVariant,
            private var bid: Int,
            private var hands: LinkedHashMap<Player, Hand>,
            private val eventPublisher: RoundEventPublisher) { // TODO change to separate data class Player, Hand, Score with proper accessor (service?)



    fun getPlayerNames() = this.players.map { it.name }.toTypedArray()

    fun play(card: Card) {
        this.strifeService.play(card)
    }

    fun triumph(card: Card) {
        this.strifeService.triumph(card)
    }

    fun changeBid(newBid: Int) {
        this.reviewService.changeBid(newBid)
    }

    fun activateBomb() {
        this.reviewService.activateBomb()
    }

    fun getTalons() = this.variant.getTalons()
}