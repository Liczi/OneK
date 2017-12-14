package oneK.deck

import kotlin.UninitializedPropertyAccessException
import oneK.strategy.DefaultRoundStrategy
import oneK.strategy.RoundStrategy
import spock.lang.Specification
import spock.lang.Unroll

import static oneK.deck.Figure.*
import static oneK.deck.Color.*


class RoundStrategySpec extends Specification {
    def "builder should build default strategy"() {
        expect:
        built.getBombPoints.invoke() == constructed.getBombPoints.invoke()
        built.getBombAllowedBidThreshold.invoke() == constructed.getBombAllowedBidThreshold.invoke()

        where:
        built = new RoundStrategy.Builder().build()
        constructed = new DefaultRoundStrategy()
    }

    def "builder should override default strategy"() {
        given:
        def builder = new RoundStrategy.Builder()
        def constructed = new DefaultRoundStrategy()

        when:
        builder.setBombPoints(100)
                .setBombAllowedBidThreshold(50)
        def built = builder.build()


        then:
        built.getBombPoints.invoke() != constructed.getBombPoints.invoke()
        built.getBombAllowedBidThreshold.invoke() != constructed.getBombAllowedBidThreshold.invoke()
    }

    @Unroll
    def "cards should be distributed to talons properly for #players players"(int players, String cards, HashSet<Card>[] talons) {
        expect:
        def builder = new RoundStrategy.Builder()
        def built = builder.setPlayersQuant(players).build()
        built.setTalonCards.invoke(Hand.fromString(cards).getCards())
        built.getTalons.invoke() == talons

        where:
        players | cards            || talons
        2       | "AS, KS, QS, JS" || [[new Card(ACE, SPADES), new Card(KING, SPADES)], [new Card(QUEEN, SPADES), new Card(JACK, SPADES)]]
        3       | "AS, KS, QS"     || [[new Card(ACE, SPADES), new Card(KING, SPADES), new Card(QUEEN, SPADES)]]
        4       | "AS, KS, QS, JS" || [[new Card(ACE, SPADES), new Card(KING, SPADES), new Card(QUEEN, SPADES), new Card(JACK, SPADES)]]
    }

    def "improper usage of builder"() {
        when:
        effect.invoke()

        then:
        thrown exception

        where:
        effect                                                || exception
        new RoundStrategy.Builder().build().getTalons         || UninitializedPropertyAccessException
        new RoundStrategy.Builder().build().getTalonSize      || UninitializedPropertyAccessException
        new RoundStrategy.Builder().build().getTalonsQuantity || UninitializedPropertyAccessException
    }

    def "improper usage of strategy"() {
        when:
        effect.invoke(args)

        then:
        thrown IllegalArgumentException

        where:
        effect                                                               | args
        new RoundStrategy.Builder().setPlayersQuant(2).build().setTalonCards | [new Card(ACE, SPADES)] as HashSet
        new RoundStrategy.Builder().setPlayersQuant(3).build().setTalonCards | [new Card(ACE, SPADES)] as HashSet
        new RoundStrategy.Builder().setPlayersQuant(4).build().setTalonCards | [new Card(ACE, SPADES)] as HashSet
    }

}
