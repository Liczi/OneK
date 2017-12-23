package oneK.game

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure
import oneK.deck.Hand
import oneK.player.Player
import oneK.game.strategy.GameStrategy
import oneK.round.events.RoundEvent
import oneK.round.strategy.RoundStrategy
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

    def "bidding with 3 players under 120"() {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def gameStrategy = new GameStrategy.Builder().build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]

        def game = new Game(players, gameStrategy, roundStrategy)

        when:
        game.bid(110)
        game.bid(120)
        game.fold()
        game.fold()

        then:
        game.biddingEnded
        game.currentPlayer == players[2]
    }

    def "bidding with 3 players over 120"() {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def gameStrategy = new GameStrategy.Builder().build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]

        def game = new Game(players, gameStrategy, roundStrategy)
        game.hands.replace(players[0], Hand.fromString("KS, QS"))

        when:
        game.bid(110)
        game.bid(120)
        game.bid(130)
        game.fold()
        game.fold()

        then:
        game.biddingEnded
        game.currentPlayer == players[0]
    }

    def "simple game with 2 cards 2 players"() {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(2).build()
        def gameStrategy = new GameStrategy.Builder().build()
        def players = [new Player("P1"), new Player("P2")]

        def game = new Game(players, gameStrategy, roundStrategy)
        game.hands.replace(players[0], Hand.fromString("KS, QS"))
        game.hands.replace(players[1], Hand.fromString("AS, QD"))


        when:
        game.fold()
        def round = game.getCurrentRound()
        round.gameIsLocked = false

        round.triumph(new Card(Figure.KING, Color.SPADES))
        round.play(new Card(Figure.ACE, Color.SPADES))
        round.play(new Card(Figure.QUEEN, Color.DIAMONDS))
        round.play(new Card(Figure.QUEEN, Color.SPADES))

        then:
        game.biddingEnded
        round.roundHasEnded
        game.ranking == [(players[0]): -100, (players[1]): 15]
    }

    def "simple game and starting new round"() {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(2).build()
        def gameStrategy = new GameStrategy.Builder().build()
        def players = [new Player("P1"), new Player("P2")]

        def game = new Game(players, gameStrategy, roundStrategy)
        def h1 = Hand.fromString("KS, QS")
        def h2 = Hand.fromString("AS, QD")

        game.hands.replace(players[0], new Hand(Arrays.copyOf(h1.cards.toArray([] as Card[]), h1.cards.size())))
        game.hands.replace(players[1], new Hand(Arrays.copyOf(h2.cards.toArray([] as Card[]), h2.cards.size())))


        when:
        game.fold()
        def round = game.getCurrentRound()
        round.gameIsLocked = false

        round.triumph(new Card(Figure.KING, Color.SPADES))
        round.play(new Card(Figure.ACE, Color.SPADES))
        round.play(new Card(Figure.QUEEN, Color.DIAMONDS))
        round.play(new Card(Figure.QUEEN, Color.SPADES))
        game.nextRound(players[1])
        game.fold()
        round = game.getCurrentRound()

        then:
        game.biddingEnded
        game.ranking == [(players[0]): -100, (players[1]): 15]
        !round.hands[players[0]].containsAll(h1)
        !round.hands[players[1]].containsAll(h2)
        round.gameIsLocked
        round.currentPlayer == players[1]

    }

    def "bidding not allowed over 120 without triumph"() {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def gameStrategy = new GameStrategy.Builder().build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]

        def game = new Game(players, gameStrategy, roundStrategy)
        game.hands.replace(players[0], Hand.fromString("9S"))

        when:
        game.bid(110)
        game.bid(120)
        game.bid(130)
        game.fold()
        game.fold()

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "should handle round end and assign winner"() {
        setup:
        def roundStrategy = new RoundStrategy.Builder().setPlayersQuant(3).build()
        def gameStrategy = new GameStrategy.Builder().setLimitedScoringThreshold(900).build()
        def players = [new Player("P1"), new Player("P2"), new Player("P3")]

        def game = new Game(players, gameStrategy, roundStrategy)
        game.biddingEnded = true
        game.ranking = [(players[0]): r1, (players[1]): r2, (players[2]): r3]
        game.startRound(players)
        game.currentRound.score = [(players[0]): score1, (players[1]): score2, (players[2]): score3] as LinkedHashMap

        when:
        game.currentRound.eventPublisher.publish(RoundEvent.ROUND_ENDED)

        then:
        game.ranking == [(players[0]): ranking1, (players[1]): ranking2, (players[2]): ranking3]
        game.winner == winner

        where:
        r1  | r2  | r3  | score1 | score2 | score3 || ranking1 | ranking2 | ranking3 || winner
        0   | 0   | 0   | 100    | 100    | 130    || 100      | 100      | 130      || null
        900 | 0   | 0   | 100    | 0      | 0      || 1000     | 0        | 0        || new Player("P1")
        0   | 900 | 0   | 100    | 100    | 100    || 100      | 900      | 100      || null
        500 | 600 | 700 | -100   | 120    | 0      || 400      | 720      | 700      || null

    }

}
