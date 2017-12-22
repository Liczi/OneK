package oneK.round

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Hand
import oneK.player.Player
import oneK.strategy.RoundStrategy
import spock.lang.Specification
import spock.lang.Unroll

class RoundTestSpec extends Specification {
    @Unroll
    def "when #card1, #card2, #card3 played, player#winnerIndex wins with value #cardsValue where trump is #currentTrump"() {
        setup:
        def strategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]
        def hands = [
                (players[0]): Hand.fromString(hand1),
                (players[1]): Hand.fromString(hand2),
                (players[2]): Hand.fromString(hand3),
        ]
        def round = new Round(players, strategy, bid, hands)
        round.gameIsLocked = false
        round.currentTrump = currentTrump


        when:
        round.play(Card.fromString(card1[0] as char, card1[1] as char))
        round.play(Card.fromString(card2[0] as char, card2[1] as char))
        round.play(Card.fromString(card3[0] as char, card3[1] as char))

        then:
        round.score[players[winnerIndex]] == score

        where:
        hand1    | hand2    | hand3    | card1 | card2 | card3 | currentTrump   | bid || winnerIndex | score
        "AS, KS" | "QS, JS" | "AC, AH" | "AS"  | "QS"  | "AC"  | null           | 0   || 0           | 25
        "9S, KS" | "QS, JS" | "AC, AS" | "9S"  | "QS"  | "AS"  | null           | 0   || 2           | 14
        "9S, KS" | "AD, JS" | "AC, AS" | "9S"  | "AD"  | "AC"  | null           | 0   || 0           | 22
        "9S, KS" | "AD, JS" | "AC, AS" | "9S"  | "AD"  | "AC"  | Color.SPADES   | 0   || 0           | 22
        "9S, KD" | "AD, 9S" | "AC, AS" | "KD"  | "9S"  | "AC"  | Color.SPADES   | 0   || 1           | 15
        "AS"     | "AD"     | "AC"     | "AS"  | "AD"  | "AC"  | null           | 30  || 0           | 30
        "KS"     | "9D"     | "KC"     | "KS"  | "9D"  | "KC"  | Color.DIAMONDS | 0   || 1           | 8
        "9D"     | "KS"     | "KC"     | "9D"  | "KS"  | "KC"  | Color.DIAMONDS | 10  || 0           | -10
    }

    @Unroll
    def "triumph should add points and change currentTriumph"() {
        setup:
        def strategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]
        def hands = [
                (players[0]): Hand.fromString(hand1),
                (players[1]): Hand.fromString(hand2),
                (players[2]): Hand.fromString(hand3),
        ]
        def round = new Round(players, strategy, bid, hands)
        round.gameIsLocked = false


        when:
        round.triumph(Card.fromString(cards1[0][0] as char, cards1[0][1] as char))
        round.play(Card.fromString(cards2[0][0] as char, cards2[0][1] as char))
        round.play(Card.fromString(cards3[0][0] as char, cards3[0][1] as char))

        round.play(Card.fromString(cards1[1][0] as char, cards1[1][1] as char))
        round.play(Card.fromString(cards2[1][0] as char, cards2[1][1] as char))
        round.play(Card.fromString(cards3[1][0] as char, cards3[1][1] as char))

        then:
        round.score[players[0]] == score1
        round.score[players[1]] == score2
        round.score[players[2]] == score3
        round.currentTrump == Card.fromString(cards1[0][0] as char, cards1[0][1] as char).color

        where:
        hand1        | hand2        | hand3        | cards1       | cards2       | cards3       | bid || score1 | score2 | score3
        "KS, QS, 9S" | "QD, JS, 9D" | "AC, AH, 9H" | ["KS", "QS"] | ["QD", "JS"] | ["AC", "9H"] | 0   || 63     | 0      | 0
        "KS, QS, 9S" | "QD, AS, 9D" | "AC, AH, 9H" | ["KS", "9D"] | ["AS", "9H"] | ["AC", "QS"] | 0   || 43     | 26     | 0
        "KS, QS"     | " AS, 9D"    | "AC, 9H"     | ["KS", "9D"] | ["AS", "9H"] | ["AC", "QS"] | 50  || -50    | 26     | 0
    }

    def "activate bomb should add default value 60 to opponents' score"() {
        setup:
        def strategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]
        def round = new Round(players, strategy, 120, Mock(Map) as LinkedHashMap)

        when:
        round.activateBomb()

        then:
        round.score[players[0]] == 0
        round.score[players[1]] == 60
        round.score[players[2]] == 60
    }

    def "double change of trump"() {
        def strategy = new RoundStrategy.Builder().setPlayersQuant(2).build()
        def players = [new Player("P1"), new Player("P2")]
        def hands = [
                (players[0]): Hand.fromString("KS, QS, 9D"),
                (players[1]): Hand.fromString("KD, QD, AS")
        ]
        def round = new Round(players, strategy, 40, hands)
        round.gameIsLocked = false


        when:
        round.triumph(Card.fromString('Q' as char, 'S' as char))
        round.play(Card.fromString('A' as char, 'S' as char))

        round.triumph(Card.fromString('Q' as char, 'D' as char))
        round.play(Card.fromString('K' as char, 'S' as char))

        round.play(Card.fromString('K' as char, 'D' as char))
        round.play(Card.fromString('9' as char, 'D' as char))

        then:
        round.score[players[0]] == 40
        round.score[players[1]] == 105

    }

    def "round should restart properly"() {
        setup:
        def strategy = new RoundStrategy.Builder().setPlayersQuant(3).setIsValid({ hand -> false }).build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]
        def h1 = Hand.fromString("AS, KS, QS")
        def h2 = Hand.fromString("AD, KD, QD")
        def h3 = Hand.fromString("AC, KC, QC")
        def talon = Hand.fromString("9S, 9C, 9D")
        def hands = [
                (players[0]): h1,
                (players[1]): h2,
                (players[2]): h3,
        ]

        def round = new Round(players, strategy, 120, hands)

        //round.strategy.setTalonCards.invoke(*[talon.cards])

        when:
        //any hand
        round.restart(h1)

        then:
        round.hands[players[0]] != h1
        round.hands[players[0]] != h2
        round.hands[players[0]] != h3
        //!talon.cards.containsAll(round.strategy.getTalons.invoke().flatten()) //todo functionality in Game class
    }
}
