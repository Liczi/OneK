package oneK.round.events

class RoundEventEmitter {
    private val listeners: MutableList<RoundEventListener> = mutableListOf()

    public fun addListener(listener: RoundEventListener) {
        this.listeners.add(listener)
    }

    public fun emit(event: RoundEvent) {
        this.listeners.forEach { it.onEvent(event) }
    }
}