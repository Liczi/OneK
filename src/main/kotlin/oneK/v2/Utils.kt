package oneK.v2

internal fun <T> List<T>.rotate() = this.drop(1) + this.first()


