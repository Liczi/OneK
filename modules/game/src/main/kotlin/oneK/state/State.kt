package oneK.state

import oneK.deck.Card
import oneK.deck.Color
import oneK.player.Player

sealed class State {

    data class Bidding(
        val order: RepeatableOrder<Bidder>,
        val talon: List<Set<Card>>,
        val ranking: Map<Player, Int>
    ) : State()

    data class Review(
        val order: RepeatableOrder<Reviewer>,
        val initialBid: Int,
        val talon: Choice<Set<Card>>,
        val ranking: Map<Player, Int>,
        val toGive: Map<Player, Card>? = null,
        val changedBid: Int? = null
    ) : State()

    data class Strife(
        val order: RepeatableOrder<Strifer>,
        val bid: Int,
        val ranking: Map<Player, Int>,
        val currentTriumph: Color? = null
    ) : State()

    data class Summary(
        val order: RepeatableOrder<Player>,
        val roundRanking: Map<Player, Int> = order.associateWith { 0 },
        val ranking: Map<Player, Int> = order.associateWith { 0 }
    ) : State()
}

data class Bidder(val cards: Set<Card>, val player: Player, val lastAction: Action.Bidding? = null)

data class Reviewer(val cards: Set<Card>, val player: Player)

data class Strifer(
    val cards: Set<Card>,
    val player: Player,
    val lastAction: Action.Strife? = null,
    val points: Int = 0,
    val isConstrained: Boolean = false
)
