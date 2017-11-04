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
        figure | color | expected
        'T'    | 'H'   | new Card(TEN, HEARTS)
        '9'    | 'C'   | new Card(NINE, CLUBS)
        'A'    | 'S'   | new Card(ACE, SPADES)
        'Q'    | 'D'   | new Card(QUEEN, DIAMONDS)
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
}
