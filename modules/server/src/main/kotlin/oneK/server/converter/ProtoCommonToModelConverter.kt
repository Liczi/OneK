package oneK.server.converter

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure
import oneK.player.Player
import oneK.proto.common.ToGivePayload
import java.util.UUID

internal fun ToGivePayload.toModel(): Map<Player, Card> =
    this.payloadList.associate { Pair(it.player.toModel(), it.card.toModel()) }

internal fun CardProto.toModel(): Card = Card(Figure.valueOf(this.figure), Color.valueOf(this.color))

internal fun PlayerProto.toModel(): Player = Player(this.name, UUID.fromString(this.uuid))