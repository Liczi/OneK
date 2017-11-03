package engine

/**
 * Created by Jakub Licznerski on 03.11.2017.
 */
interface OneKStrategy {

    fun positionAssignment()
    fun auction()
    fun gameInit()
    fun talonPicking()
    fun talonRedistribution()

    fun gamePunctation()
    fun winningCondition()
    fun win()
}

//TODO
// setting - factory convention which allows us to easily extend strategy object (!!!) cover default values
// based on anonymous subclasses of one interface