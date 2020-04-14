package oneK.round.events

class RoundEventPublisher {
    private val listeners: MutableSet<RoundEventListener> = mutableSetOf()

    public fun addListener(listener: RoundEventListener) {
        this.listeners.add(listener)
    }

    public fun publish(event: RoundEvent) {
        this.listeners.forEach { it.onEvent(event) }
    }
}