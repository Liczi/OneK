package oneK.v2.validation

import oneK.v2.state.State

abstract class StateValidator {
//    protected inline fun <reified T : State> ensureValidState(state: State, condition: (T) -> Boolean): T? =
//            (state as? T)?.let { return if (condition(state)) state else null }
//
//    protected inline fun <reified T : State> ensureValidState(state: State): T? =
//            ensureValidState<T>(state) { true }

    fun <T> T.ensureValid(condition: () -> Boolean): T? {
        return if (condition()) this else null
    }
}