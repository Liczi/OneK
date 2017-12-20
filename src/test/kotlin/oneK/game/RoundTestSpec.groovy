package oneK.game

import oneK.deck.Card
import oneK.deck.Figure
import oneK.deck.Hand
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

    def "talon should be distributed properly"() {
        setup:
        def strategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]
        def round = new Round(players, strategy, 120, players.first())
        def talons = round.strategy.getTalons.invoke().flatten()

        when:
        round.pickTalon(0)
        def givenToFirst = round.hands[players[0]].cards[0]
        def givenToSecond = round.hands[players[0]].cards[1]
        def cardsToGive = [(players[1]): givenToFirst, (players[2]): givenToSecond] as Map<Player, Card>
        round.distributeCard(cardsToGive)

        then:
        round.hands[players[1]].cards.contains(givenToFirst)
        round.hands[players[2]].cards.contains(givenToSecond)
        !round.hands[players[0]].cards.containsAll([givenToFirst, givenToSecond])
        def handSize = round.hands[players[0]].cards.size()
        round.hands.values().forEach { hand -> hand.cards.size() == handSize }
    }

//    def "stage should be played properly"() {
//        setup:
//        def strategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
//        def players = [new Player("P1"), new Player("P2"), new Player("P3")]
//        def round = new Round(players, strategy, 120, players.first())
//        def talons = round.strategy.getTalons.invoke().flatten()
//        round.pickTalon(0)
//        def givenToFirst = round.hands[players[0]].cards[0]
//        def givenToSecond = round.hands[players[0]].cards[1]
//        def cardsToGive = [(players[1]): givenToFirst, (players[2]): givenToSecond] as Map<Player, Card>
//        round.distributeCard(cardsToGive)
//
//        def C1 = round.hands[players[0]].cards[0]
//        def C2 = round.hands[players[1]].cards[0]
//        def C3 = round.hands[players[2]].cards[0]
//
//        when:
//        round.play(C1)
//        round.play(C2)
//        round.play(C3)
//
//        then:
//        round.score[players[0]] == C1.figure.value
//        round.score[players[1]] == C2.figure.value
//        round.score[players[2]] == C3.figure.value
//
//    }

    //todo add more test cases
    //todo test also for 2 players
    def "test gameplay for 3 players"() {
        setup:
        def strategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]
        def round = new Round(players, strategy, 120)
        round.gameIsLocked = false
        round.hands = [
                (players[0]): Hand.fromString("AS, KS"),
                (players[1]): Hand.fromString("QS, JS"),
                (players[2]): Hand.fromString("AC, AH"),
        ]

        when:
        round.play(Card.fromString('A' as char, 'S' as char))
        round.play(Card.fromString('Q' as char, 'S' as char))
        round.play(Card.fromString('A' as char, 'C' as char))

        then:
        round.score[players[0]] == Figure.ACE.value * 2 + Figure.QUEEN.value
    }

    //todo test bomb
}
