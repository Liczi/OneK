package oneK.v2.state

//TODO change package
import oneK.v2.Hand

data class Bidder(val hand: Hand, var lastBid: Int? = null, var folded: Boolean = false)
