package oneK.v2.service

import oneK.player.Player

interface TestPlayersHolder {
    fun getPlayers(): List<Player>
    class TwoPlayer : TestPlayersHolder {
        private val players = listOf(Player("Zenek"), Player("Krzysiek"))
        override fun getPlayers(): List<Player> = this.players
    }

    class ThreePlayer : TestPlayersHolder {
        private val players = listOf(Player("Zenek"), Player("Krzysiek"), Player("Maciek"))
        override fun getPlayers(): List<Player> = this.players
    }
}