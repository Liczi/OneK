package oneK.round.events

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
}
