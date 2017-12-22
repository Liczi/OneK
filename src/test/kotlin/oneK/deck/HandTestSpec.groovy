package oneK.deck

import spock.lang.Specification
import spock.lang.Unroll

import static oneK.deck.Figure.*
import static oneK.deck.Color.*


/**
 * Created by Jakub Licznerski on 04.11.2017.
 */
class HandTestSpec extends Specification {
    @Unroll
    def "should parse string #hand to hand #expected"(String hand, Hand expected) {
        expect:
        Hand.fromString(hand) == expected

        //todo add more cases
        where:
        hand           | expected
        "TH,JH,QH,KH"  | [new Card(TEN, HEARTS), new Card(JACK, HEARTS), new Card(QUEEN, HEARTS), new Card(KING, HEARTS)]
        "9C,AS, KD,TS" | [new Card(NINE, CLUBS), new Card(ACE, SPADES), new Card(KING, DIAMONDS), new Card(TEN, SPADES)]
        " "            | []
        "KS "          | [new Card(KING, SPADES)]
    }

    @Unroll
    def "#hand1 should not be equal to #hand2"(String hand1, String hand2) {
        expect:
        Hand.fromString(hand1) != Hand.fromString(hand2)

        where:
        hand1           | hand2
        "TH ,JH, QH,KH" | "AS,JH,QH,KH"
        "TH,JH,QH"      | "TH,JH,QH,KH"
        "JH,QH,KH"      | "TH,JH,QH,KH"
        "TH,JH,QH,KH"   | "JH,QH,KH"
        "TH,JH,QH,KH"   | "TH,JH,KH"
    }

    def "duplicate hand should be ignored"(String hand, String parsedHand) {
        expect:
        Hand.fromString(hand) == Hand.fromString(parsedHand)

        where:
        hand                     | parsedHand
        "9C,9C"                  | "9C"
        "AS, KH,   QS, KH   "    | "AS, KH, QS"
        "AS, AS, AS, AS, AS, AS" | "AS"

    }

    @Unroll
    def "has triumph on #hand should return #result"() {
        when:
        def hasTriumph = Hand.fromString(hand).hasTriumph()

        then:
        hasTriumph == result

        where:
        hand                 | result
        "9D"                 | false
        "AD, KD, 9D"         | false
        "KD, QD"             | true
        "KS, QS, AD, AS"     | true
        "KS, QS, KD, QD"     | true
        "KS, QS, KD, QD, AS" | true
    }
}
