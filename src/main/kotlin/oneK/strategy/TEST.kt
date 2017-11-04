package oneK.strategy

/**
 * Created by Jakub Licznerski on 03.11.2017.
 */

open class Interface {
    open fun exec(): Int {
        return 11
    }
    open fun exec2(): Int = 12
    internal var prop: () -> Int = { 12 }

}

class OnePlusOne : Interface() {
    override fun exec(): Int = 21
//    override fun exec2(): Int = 22
}

class OnePlusOneFactory {
    fun getInstance(inter: Interface): Interface {
        inter.prop = {
            println("koks")
            38 }
        return inter
    }
}

fun main(args: Array<String>){
    var inter = Interface()
    println(inter.prop())
    inter = OnePlusOneFactory().getInstance(inter)
    println(inter.prop())
    println(inter.prop())
}