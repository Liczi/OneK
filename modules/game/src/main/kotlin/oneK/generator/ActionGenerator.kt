package oneK.generator

import oneK.state.Action
import oneK.state.State
import oneK.validation.GameValidator

class ActionGenerator(private val validator: GameValidator) {

    fun generate(state: State.Summary): List<Action.Summary> {
        TODO()
    }

    fun generate(state: State.Bidding): List<Action.Bidding> {
        TODO()
    }

    fun generate(state: State.Review): List<Action.Review> {
        TODO()
    }

    fun generate(state: State.Strife): List<Action.Strife> {
        val cards = state.order.current().cards
        cards.filter { validator.canPlay(state, it) != null }
        TODO()
    }
}