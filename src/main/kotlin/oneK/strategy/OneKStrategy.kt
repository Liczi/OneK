package oneK.strategy

import java.util.logging.Logger

/**
 * Created by Jakub Licznerski on 03.11.2017.
 */
abstract class OneKStrategy {

    private val LOGGER = Logger.getLogger(this.javaClass.toString())

    //todo verify sequence
    fun start() {
        shuffleCards()
        assignPlayerOrder() //should'nt it be a parameter ?
        bidding()
        initGame()
        pickTalon()
        redistributeTalon()
        do {
            playRound()
            reassignPlayerOrder()

            saveRound()
            updateScore()
        } while (!gameEnded())
    }

    //todo define a game strategy
    internal abstract var shuffleCards: () -> Unit
    internal abstract var assignPlayerOrder: () -> Unit
    internal abstract var bidding: () -> Unit
    internal abstract var initGame: () -> Unit
    internal abstract var pickTalon: () -> Unit
    internal abstract var redistributeTalon: () -> Unit
    internal abstract var playRound: () -> Unit
    internal abstract var reassignPlayerOrder: () -> Unit //check if it may be used with assignPlayerOrder
    internal abstract var saveRound: () -> Unit
    internal abstract var updateScore: () -> Unit

    internal abstract var changeTrumpSuit: () -> Unit

    //think about extracting to other strategy
    internal abstract var gameEnded: () -> Boolean

    //todo think about scoring strategy separately
    // OneKStrategy should consist of scoring strategy and game strategy separately

//    fun gameScoring()
//    fun winningCondition()
//    fun win()
}

//TODO
// setting - factory convention which allows us to easily extend strategy object (!!!) cover default values
// based on anonymous subclasses of one interface

//TODO
// game serialization - save state after each change

//TODO maybe split implementation to separate methods not only start().
// This will allow creating base classes implementing Strategy interface directly (another level of flexibility)