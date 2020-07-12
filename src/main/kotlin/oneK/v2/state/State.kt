package oneK.v2.state

import oneK.deck.Card
import oneK.deck.Color
import oneK.player.Player

sealed class State {
    data class Bidding(val order: RepeatableOrder<Bidder>, val talon: List<Set<Card>>) : State()

    data class Review(
        val order: RepeatableOrder<Reviewer>,
        val initialBid: Int,
        val talon: Choice<Set<Card>>,
        val toGive: Map<Player, Card>? = null,
        val changedBid: Int? = null
    ) : State()

    data class Strife(val order: RepeatableOrder<Strifer>, val bid: Int, val currentTriumph: Color? = null) : State()

    data class Summary(val order: RepeatableOrder<Player>, val ranking: Map<Player, Int> = order.associateWith { 0 }) :
        State()
}

data class Bidder(val cards: Set<Card>, val player: Player, val lastAction: BiddingAction? = null)

data class Reviewer(val cards: Set<Card>, val player: Player)

data class Strifer(
    val cards: Set<Card>,
    val player: Player,
    val lastAction: StrifeAction? = null,
    val points: Int = 0,
    val isConstrained: Boolean = false
)
