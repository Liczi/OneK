package oneK.server.converter

import com.google.protobuf.MessageOrBuilder

internal typealias ActionProto = oneK.proto.action.Action
internal typealias ActionProtoBuilder = oneK.proto.action.Action.Builder
internal typealias StateProto = oneK.proto.state.State
internal typealias StriferProto = oneK.proto.state.Strifer
internal typealias BidderProto = oneK.proto.state.Bidder
internal typealias CardProto = oneK.proto.common.Card
internal typealias PlayerProto = oneK.proto.common.Player

internal fun <T : MessageOrBuilder, R> T.setIfNotNull(nullable: R?, setFunction: (T, R) -> T): T =
    when (nullable) {
        null -> this
        else -> setFunction(this, nullable)
    }

internal fun <T : MessageOrBuilder> T.setIf(condition: Boolean, setFunction: (T) -> T): T =
    if (condition) setFunction(this)
    else this
