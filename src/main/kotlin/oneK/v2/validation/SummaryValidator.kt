package oneK.v2.validation

import oneK.v2.state.State
import oneK.v2.variant.Variant

interface SummaryValidator {
    fun canStart(state: State.Summary): State.Summary?
}

class SummaryValidatorImpl(private val variant: Variant) : SummaryValidator, StateValidator() {
    override fun canStart(state: State.Summary): State.Summary? {
        return state.ensureValid {
            state.ranking.none { it.value >= variant.getGameGoal() }
        }
    }
}