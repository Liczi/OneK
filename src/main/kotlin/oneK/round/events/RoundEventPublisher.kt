package oneK.round.events

class RoundEventPublisher {
    private val listeners: MutableList<RoundEventListener> = mutableListOf()

    public fun addListener(listener: RoundEventListener) {
        this.listeners.add(listener)
    }

    public fun publish(event: RoundEvent) {
        this.listeners.forEach { it.onEvent(event) }
    }
}