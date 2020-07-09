package oneK.v2.state

import oneK.deck.Card
import oneK.player.Player

//TODO CardHolder ?
data class Reviewer(val cards: Set<Card>, val player: Player)
