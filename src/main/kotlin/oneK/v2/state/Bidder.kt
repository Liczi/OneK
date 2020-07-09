package oneK.v2.state

import oneK.deck.Card
import oneK.player.Player
import oneK.v2.action.BiddingAction

data class Bidder(val cards: Set<Card>, val player: Player, val lastAction: BiddingAction? = null)
