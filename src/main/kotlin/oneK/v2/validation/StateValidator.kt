package oneK.v2.validation

abstract class StateValidator {
    fun <T> T.ensureValid(condition: () -> Boolean): T? {
        return if (condition()) this else null
    }
}