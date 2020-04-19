package oneK.v2.state

import oneK.player.Player

sealed class State {
    data class Bidding(val bidders: RepeatableOrder<Bidder>, var currentBid: Int) : State()
    data class Review(val currentBid: Int) : State()
    data class Strife(val currentPlayer: Player) : State()
    data class Summary(val ranking: Map<Player, Int>) : State()
}