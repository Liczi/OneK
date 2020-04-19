package oneK.round.strategy

import kotlin.UninitializedPropertyAccessException
import oneK.deck.Card
import oneK.deck.Hand
import spock.lang.Specification
import spock.lang.Unroll

import static oneK.deck.Figure.*
import static oneK.deck.Color.*


class RoundVariantTestSpec extends Specification {
    def "builder should build default strategy"() {
        expect:
        built.getBombPoints.invoke() == constructed.getBombPoints.invoke()
        built.getBombAllowedBidThreshold.invoke() == constructed.getBombAllowedBidThreshold.invoke()

        where:
        built = new RoundVariant.Builder().build()
        constructed = new DefaultRoundRoundVariant()
    }

    def "builder should override default strategy"() {
        given:
        def builder = new RoundVariant.Builder()
        def constructed = new DefaultRoundRoundVariant()

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
        def builder = new RoundVariant.Builder()
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
        new RoundVariant.Builder().build().getTalons         || UninitializedPropertyAccessException
        new RoundVariant.Builder().build().getTalonSize      || UninitializedPropertyAccessException
        new RoundVariant.Builder().build().getTalonsQuantity || UninitializedPropertyAccessException
    }

    def "improper usage of strategy"() {
        when:
        effect.invoke(args)

        then:
        thrown IllegalArgumentException

        where:
        effect                                                               | args
        new RoundVariant.Builder().setPlayersQuant(2).build().setTalonCards | [new Card(ACE, SPADES)] as HashSet
        new RoundVariant.Builder().setPlayersQuant(3).build().setTalonCards | [new Card(ACE, SPADES)] as HashSet
        new RoundVariant.Builder().setPlayersQuant(4).build().setTalonCards | [new Card(ACE, SPADES)] as HashSet
    }

    def "qualifiesForRestart test"() {
        setup:
        def strategy = new RoundVariant.Builder()
                .setQualifiesForRestart({ hand -> hand.containsAll(Hand.fromString("9H,9C,9D,9S")) })
                .build()

        when:
        def r1 = strategy.getQualifiesForRestart().invoke(Hand.fromString("9H,9C,9D,9S, AS, KD"))
        def r2 = strategy.getQualifiesForRestart().invoke(Hand.fromString("9H,9C,9D,AS"))

        then:
        r1
        !r2
    }
}
