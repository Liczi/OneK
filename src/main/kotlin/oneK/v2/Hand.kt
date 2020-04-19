package oneK.v2

import oneK.deck.Card
import oneK.player.Player

data class Hand(val cards: Set<Card>, val player: Player)
