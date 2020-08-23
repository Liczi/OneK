package oneK.server.converter

import oneK.deck.Card
import oneK.player.Player
import oneK.proto.common.ToGive
import oneK.proto.common.ToGivePayload

internal fun Card.toProtoMessage(): CardProto =
    CardProto.newBuilder()
        .setFigure(this.figure.name)
        .setColor(this.color.name)
        .build()

internal fun Player.toProtoMessage(): PlayerProto =
    PlayerProto.newBuilder()
        .setName(this.name)
        .setUuid(this.uuid.toString())
        .build()

internal fun Map<Player, Card>.toProtoMessage(): ToGivePayload =
    ToGivePayload.newBuilder()
        .addAllPayload(this.map { it.toProtoMessage() })
        .build()

private fun Map.Entry<Player, Card>.toProtoMessage(): ToGive =
    ToGive.newBuilder()
        .setCard(this.value.toProtoMessage())
        .setPlayer(this.key.toProtoMessage())
        .build()