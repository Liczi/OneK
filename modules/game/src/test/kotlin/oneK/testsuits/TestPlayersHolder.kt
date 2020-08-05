package oneK.testsuits

import oneK.GameFactory
import oneK.ValidatedGame
import oneK.player.Player
import oneK.variant.Variant
import oneK.variant.getVariantFor

private val _players = listOf(
    Player("Zenek (0)"),
    Player("Krzysiek (1)"),
    Player("Maciek (2)")
)

interface TestPlayersHolder {
    val players: List<Player>
    val variant: Variant
    val game: ValidatedGame
}

class TwoPlayer : TestPlayersHolder {
    override val players = _players.dropLast(1)
    override val variant = getVariantFor(2)
    override val game = GameFactory.default(variant)
}

class ThreePlayer : TestPlayersHolder {
    override val players = _players.toList()
    override val variant = getVariantFor(3)
    override val game = GameFactory.default(variant)
}