package oneK.deck

import spock.lang.Specification

import static oneK.deck.Figure.*
import static oneK.deck.Color.*


/**
 * Created by Jakub Licznerski on 04.11.2017.
 */
class HandTestSpec extends Specification {
    def "should parse string #hand to hand #expected"(String hand, Hand expected) {
        expect:
        Hand.fromString(hand) == expected

        //todo add more cases
        where:
        hand          | expected
        "TH,JH,QH,KH" | [new Card(TEN, HEARTS), new Card(JACK, HEARTS), new Card(QUEEN, HEARTS), new Card(KING, HEARTS)]
        "9C,AS,KD,TS" | [new Card(NINE, CLUBS), new Card(ACE, SPADES), new Card(KING, DIAMONDS), new Card(TEN, SPADES)]
    }
}
