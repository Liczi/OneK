package oneK.v2

fun <T> List<T>.rotate() = this.drop(1) + this.first()


