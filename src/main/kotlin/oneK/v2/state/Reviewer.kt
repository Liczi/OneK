package oneK.v2.state

import oneK.deck.Card
import oneK.player.Player

//TODO rename to CardHolder ?
data class Reviewer(val cards: Set<Card>, val player: Player)
