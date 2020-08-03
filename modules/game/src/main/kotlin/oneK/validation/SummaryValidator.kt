package oneK.validation

import oneK.state.State
import oneK.variant.Variant

interface SummaryValidator {
    fun canStart(state: State.Summary): State.Summary?
}

class SummaryStateValidatorImpl(private val variant: Variant) : SummaryValidator, StateValidator() {
    override fun canStart(state: State.Summary): State.Summary? {
        return state.ensureValid {
            state.ranking.none { it.value >= variant.getGameGoal() }
        }
    }
}