package oneK.round.events

import oneK.deck.Card
import oneK.deck.Color
import oneK.deck.Figure
import oneK.deck.Hand
import oneK.player.Player
import oneK.round.Round
import oneK.round.strategy.RoundStrategy
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

class RoundEventsTestSpec extends Specification {
    def "should send messages to all subscribers"() {
        setup:
        def publisher = new RoundEventPublisher()
        def subscriber = Mock(RoundEventListener)
        def subscriber2 = Mock(RoundEventListener)
        publisher.addListener(subscriber)
        publisher.addListener(subscriber2)

        when:
        publisher.publish(RoundEvent.ROUND_STARTED)

        then:
        1 * subscriber.onEvent(RoundEvent.ROUND_STARTED)
        1 * subscriber2.onEvent(RoundEvent.ROUND_STARTED)
    }

    def "listener should execute onEvent function"() {
        setup:
        def executed = false
        def publisher = new RoundEventPublisher()
        def listener = new RoundEventListener() {
            @Override
            void onEvent(@NotNull RoundEvent event) {
                if (event == RoundEvent.ROUND_STARTED) executed = true
            }
        }
        publisher.addListener(listener)

        when:
        publisher.publish(RoundEvent.ROUND_STARTED)

        then:
        executed
    }

    def "ROUND_ENDED should execute once"() {
        setup:
        def hand1 = "KS, QS"
        def hand2 = "AS, AD"
        def strategy = new RoundStrategy.Builder().setPlayersQuant(2).build()
        def players = [new Player("P1"), new Player("P2")]
        def hands = [
                (players[0]): Hand.fromString(hand1),
                (players[1]): Hand.fromString(hand2)
        ]
        def round = new Round(players, strategy, 10, hands)
        round.gameIsLocked = false

        def listener = Mock(RoundEventListener)
        round.registerListener(listener)

        when:
        round.play(new Card(Figure.KING, Color.SPADES))
        round.play(new Card(Figure.ACE, Color.DIAMONDS))
        round.play(new Card(Figure.QUEEN, Color.SPADES))
        round.play(new Card(Figure.ACE, Color.SPADES))

        then:
        1 * listener.onEvent(RoundEvent.ROUND_ENDED)
        2 * listener.onEvent(RoundEvent.STAGE_ENDED)
        4 * listener.onEvent(RoundEvent.PLAYER_CHANGED)
    }

    def "adding the same listener twice"() {
        def publisher = new RoundEventPublisher()
        def listener = Mock(RoundEventListener)

        when:
        publisher.addListener(listener)
        publisher.addListener(listener)

        then:
        publisher.listeners.size() == 1
    }
}
