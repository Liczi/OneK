package oneK.game

import oneK.player.Player
import oneK.strategy.RoundStrategy
import spock.lang.Specification
import spock.lang.Unroll

class RoundTestSpec extends Specification {

    @Unroll
    def "should assign #talonCardsCount to talon and #playerCardsQuant to each player"(List<Player> players, int talonCardsCount, int playerCardsQuant) {
        setup:
        def strategy = new RoundStrategy.Builder().setPlayersQuant(players.size()).build()
        def round = new Round(players, strategy, 120, players.first())
        def talons = round.strategy.getTalons.invoke().flatten()

        expect:
        //TALONS
        talons.unique() == talons
        talons.size() == talonCardsCount

        //PLAYERS
        round.hands.keySet().size() == players.size()
        round.hands.values().stream().allMatch { hand -> hand.cards.size() == playerCardsQuant }
        round.hands.values().stream().allMatch { hand -> hand.cards.unique().size() == hand.cards.size() }


        where:
        players                                                                                   | talonCardsCount | playerCardsQuant
        [new Player("Jurek"), new Player("Mietek")]                                               | 4               | 10
        [new Player("Jurek"), new Player("Mietek"), new Player("Zbyszek")]                        | 3               | 7
        [new Player("Jurek"), new Player("Mietek"), new Player("Zbyszek"), new Player("Filipek")] | 4               | 5
    }

}
