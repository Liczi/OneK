package oneK.game

import oneK.deck.Card
import oneK.player.Player
import oneK.strategy.GameStrategy
import oneK.strategy.RoundStrategy
import spock.lang.Specification
import spock.lang.Unroll

class GameTestSpec extends Specification {

    @Unroll
    def "should assign #talonCardsCount to talon and #playerCardsQuant to each player"(List<Player> players, int talonCardsCount, int playerCardsQuant) {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(players.size()).build()
        def gameStrategy = new GameStrategy.Builder().build()

        def game = new Game(players, gameStrategy, roundStrategy)
        game.biddingEnded = true
        game.startRound(players)
        def round = game.currentRound
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

    def "talon should be distributed properly"() {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def gameStrategy = new GameStrategy.Builder().build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]

        def game = new Game(players, gameStrategy, roundStrategy)
        game.biddingEnded = true
        game.startRound(players)
        def round = game.currentRound
        round.strategy.getTalons.invoke().flatten()

        when:
        round.pickTalon(0)
        def givenToFirst = round.hands[players[0]].cards[0]
        def givenToSecond = round.hands[players[0]].cards[1]
        def cardsToGive = [(players[1]): givenToFirst, (players[2]): givenToSecond] as Map<Player, Card>
        round.distributeCards(cardsToGive)

        then:
        round.hands[players[1]].cards.contains(givenToFirst)
        round.hands[players[2]].cards.contains(givenToSecond)
        !round.hands[players[0]].cards.containsAll([givenToFirst, givenToSecond])
        def handSize = round.hands[players[0]].cards.size()
        round.hands.values().forEach { hand -> hand.cards.size() == handSize }
    }

}
