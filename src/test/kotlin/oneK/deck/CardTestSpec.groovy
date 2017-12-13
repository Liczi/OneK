package oneK.deck

import spock.lang.Specification
import spock.lang.Unroll

import static oneK.deck.Figure.*
import static oneK.deck.Color.*

/**
 * Created by Jakub Licznerski on 04.11.2017.
 */
class CardTestSpec extends Specification {
    @Unroll
    def "should parse string #figure#color to card #expected"(char figure, char color, Card expected) {
        expect:
        Card.fromString(figure, color) == expected

        where:
        figure | color || expected
        'T'    | 'H'   || new Card(TEN, HEARTS)
        '9'    | 'C'   || new Card(NINE, CLUBS)
        'A'    | 'S'   || new Card(ACE, SPADES)
        'Q'    | 'D'   || new Card(QUEEN, DIAMONDS)
    }

    @Unroll
    def "should return null parsing string #figure#color"(char figure, char color) {
        expect:
        Card.fromString(figure, color) == null

        where:
        figure | color
        'H'    | 'H'
        '1'    | 'D'
        '8'    | 'C'
        'A'    | 'X'
        'K'    | 'Z'
    }

    @Unroll
    def "#card1 should be equal to #card2"(Card card1, Card card2) {
        expect:
        card1 == card2

        where:
        card1                    | card2
        new Card(TEN, SPADES)    | new Card(TEN, SPADES)
        new Card(JACK, DIAMONDS) | new Card(JACK, DIAMONDS)
        new Card(NINE, CLUBS)    | new Card(NINE, CLUBS)
        new Card(QUEEN, HEARTS)  | new Card(QUEEN, HEARTS)
    }

    @Unroll
    def "#card1 should not be equal to #card2"(Card card1, Card card2) {
        expect:
        card1 != card2

        where:
        card1                   | card2
        new Card(TEN, HEARTS)   | new Card(NINE, HEARTS)
        new Card(JACK, SPADES)  | new Card(JACK, HEARTS)
        new Card(QUEEN, HEARTS) | new Card(NINE, SPADES)
        new Card(KING, HEARTS)  | new Card(QUEEN, HEARTS)
    }
}
