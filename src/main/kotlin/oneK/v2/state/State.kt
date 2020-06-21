package oneK.v2.state

import oneK.deck.Card
import oneK.player.Player

sealed class State {
    data class Bidding(val biddersOrder: RepeatableOrder<Bidder>, val talon: List<Set<Card>>) : State()
    data class Review(val currentBid: Int) : State()
    data class Strife(val currentPlayer: Player) : State()
    data class Summary(val order: List<Player>, val ranking: Map<Player, Int> = order.associateWith { 0 }) : State()
}