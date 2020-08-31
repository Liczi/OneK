package oneK.server.converter

import oneK.deck.Card
import oneK.player.Player
import oneK.proto.common.CardHolder
import oneK.proto.state.BiddingState
import oneK.proto.state.ReviewState
import oneK.proto.state.StrifeState
import oneK.proto.state.SummaryState
import oneK.state.Bidder
import oneK.state.Choice
import oneK.state.State
import oneK.state.Strifer

internal fun State.toProtoMessage(): StateProto {
    val builder = StateProto.newBuilder()
    when (this) {
        is State.Bidding -> builder.bidding = this.toProtoMessage()
        is State.Review -> builder.review = this.toProtoMessage()
        is State.Strife -> builder.strife = this.toProtoMessage()
        is State.Summary -> builder.summary = this.toProtoMessage()
    }
    return builder.build()
}

private fun State.Bidding.toProtoMessage(): BiddingState =
    BiddingState.newBuilder()
        .addAllOrder(this.order.map(Bidder::toProtoMessage))
        .setCurrent(this.order.indexOf(this.order.current()) + 1)
        .putAllRanking(this.ranking.toProtoMessage())
        .addAllTalon(this.talon.toProtoMessage())
        .setTalonSplit(this.talon.size)
        .build()

private fun Bidder.toProtoMessage(): BidderProto =
    BidderProto.newBuilder()
        .setIfNotNull(this.lastAction) { builder, action -> builder.setLastAction(action.toProtoMessage()) }
        .setHolder(toCardHolder(this.player, this.cards))
        .build()

private fun State.Review.toProtoMessage(): ReviewState =
    ReviewState.newBuilder()
        .addAllOrder(this.order.map { toCardHolder(it.player, it.cards) })
        .setCurrent(this.order.indexOf(this.order.current()) + 1)
        .putAllRanking(this.ranking.toProtoMessage())
        .setInitialBid(this.initialBid)
        .addAllTalon(this.talon.toProtoMessage())
        .setTalonSplit(this.talon.size)
        .setIf(this.talon is Choice.Taken) { it.addAllTaken((this.talon as Choice.Taken).value.map(Card::toProtoMessage)) }
        .setIfNotNull(this.toGive) { builder, toGive -> builder.setToGive(toGive.toProtoMessage()) }
        .setIfNotNull(this.changedBid) { builder, changedBid -> builder.setChangedBid(changedBid) }
        .build()

private fun State.Strife.toProtoMessage(): StrifeState =
    StrifeState.newBuilder()
        .addAllOrder(this.order.map(Strifer::toProtoMessage))
        .setCurrent(this.order.indexOf(this.order.current()) + 1)
        .putAllRanking(this.ranking.toProtoMessage())
        .setBid(this.bid)
        .setIfNotNull(this.currentTriumph) { builder, color -> builder.setCurrentTriumph(color.name) }
        .build()

private fun Strifer.toProtoMessage(): StriferProto =
    StriferProto.newBuilder()
        .setHolder(toCardHolder(this.player, this.cards))
        .setIfNotNull(this.lastAction) { builder, state -> builder.setLastAction(state.toProtoMessage()) }
        .setPoints(this.points)
        .setIsConstrained(this.isConstrained)
        .build()

private fun State.Summary.toProtoMessage(): SummaryState =
    SummaryState.newBuilder()
        .addAllOrder(this.order.map(Player::toProtoMessage))
        .setCurrent(this.order.indexOf(this.order.current()) + 1)
        .putAllRoundRanking(this.roundRanking.toProtoMessage())
        .putAllRanking(this.ranking.toProtoMessage())
        .build()

private fun Map<Player, Int>.toProtoMessage(): Map<String, Int> = this.mapKeys { it.key.uuid.toString() }

private fun List<Set<Card>>.toProtoMessage(): List<CardProto> =
    this.flatMap { it.map(Card::toProtoMessage) }

private fun toCardHolder(player: Player, cards: Set<Card>): CardHolder =
    CardHolder.newBuilder()
        .setPlayer(player.toProtoMessage())
        .addAllCards(cards.map(Card::toProtoMessage))
        .build()