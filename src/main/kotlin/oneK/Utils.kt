package oneK

internal fun <T> List<T>.rotate() = this.drop(1) + this.first()
