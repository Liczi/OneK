package oneK.game.events

class GameEventPublisher {
    private val listeners: MutableSet<GameEventListener> = mutableSetOf()

    public fun addListener(listener: GameEventListener) {
        this.listeners.add(listener)
    }

    public fun publish(event: GameEvent) {
        this.listeners.forEach { it.onEvent(event) }
    }
}