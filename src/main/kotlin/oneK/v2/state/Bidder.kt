package oneK.v2.state

//TODO change package
import oneK.player.Player
import oneK.v2.Hand

//TODO change Player class
data class Bidder(val hand: Hand, var lastBid: Int? = null, var folded: Boolean = false)
