package oneK.validation

import oneK.state.State
import oneK.variant.Variant

interface SummaryValidator {
    fun canStart(state: State.Summary): Boolean
}

class SummaryStateValidatorImpl(private val variant: Variant) : SummaryValidator {

    override fun canStart(state: State.Summary): Boolean = state.ranking.none { it.value >= variant.getGameGoal() }
}