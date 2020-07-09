package testsuits

import oneK.player.Player

private val _players = listOf(
    Player("Zenek (0)"),
    Player("Krzysiek (1)"),
    Player("Maciek (2)")
)

interface TestPlayersHolder {
    val players: List<Player>
}

open class TwoPlayer : TestPlayersHolder {
    override val players = _players.dropLast(1)
}

open class ThreePlayer : TestPlayersHolder {
    override val players = _players.toList()
}