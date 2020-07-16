package testsuits

import oneK.player.Player
import oneK.v2.variant.DefaultThreePlayerVariant
import oneK.v2.variant.DefaultTwoPlayerVariant
import oneK.v2.variant.Variant

private val _players = listOf(
    Player("Zenek (0)"),
    Player("Krzysiek (1)"),
    Player("Maciek (2)")
)

interface TestPlayersHolder {
    val players: List<Player>
    val variant: Variant
}

open class TwoPlayer : TestPlayersHolder {
    override val players = _players.dropLast(1)
    override val variant = DefaultTwoPlayerVariant()
}

open class ThreePlayer : TestPlayersHolder {
    override val players = _players.toList()
    override val variant = DefaultThreePlayerVariant()
}