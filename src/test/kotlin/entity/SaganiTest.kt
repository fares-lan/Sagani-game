package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * test cases for Sagani
 */

class SaganiTest {

    // players to create Sagani

    private val player1 = Player(
        0, 0, "Alice", 24, 0, mutableListOf(), DiscColor.GREY,
        PlayerType.HUMAN
    )
    private val player2 = Player(
        0, 0, "Bob", 24, 0, mutableListOf(), DiscColor.GREY,
        PlayerType.RANDOM_AI
    )

    // gameStates to test with

    private var currentTurn: Sagani = Sagani(
        0, 0, 0, null, null,
        mutableListOf(
            Tile(
                0, 1, 0, 0, 0, mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)), TileSpirit.WATER
            )
        ),
        mutableListOf(
            Tile(
                0, 1, 0, 0, 0, mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)), TileSpirit.WATER
            )
        ),
        mutableListOf(
            Tile(
                0, 1, 0, 0, 0, mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)), TileSpirit.WATER
            )
        ),
        listOf(player1, player2), SimulationSpeed.SLOW

    )
    private var nextTurn: Sagani = Sagani(
        1, 0, 0, null, null,
        mutableListOf(
            Tile(
                0, 1, 0, 0, 0, mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)), TileSpirit.WATER
            )
        ),
        mutableListOf(
            Tile(
                0, 1, 0, 0, 0, mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)), TileSpirit.WATER
            )
        ),
        mutableListOf(
            Tile(
                0, 1, 0, 0, 0, mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)), TileSpirit.WATER
            )
        ),
        listOf(player1, player2), SimulationSpeed.SLOW
    )

    /**
     * function to set gameStates, to work around recursive problem
     */
    private fun setGameStates(currentTurn: Sagani) {
        nextTurn = currentTurn.copy(previousState = currentTurn, playerIndex = 1)
        currentTurn.nextState = nextTurn
    }

    /**
     * function to test if order of gameState is correct
     */

    @Test
    fun gameStateTest() {
        setGameStates(currentTurn)
        assertEquals(nextTurn.previousState, currentTurn)
        assertEquals(currentTurn.nextState, nextTurn)
        assertEquals(currentTurn.playerIndex, 0)
        assertEquals(nextTurn.playerIndex, 1)
        assertNotEquals(currentTurn.previousState, nextTurn)
        assertNotEquals(nextTurn.nextState, currentTurn)

    }

    /**
     * Testfunction to ensure the correct execution of the clone()-function to create deep copies of [Sagani] entities.
     */
    @Test
    fun cloneSaganiTest() {
        assert(currentTurn != nextTurn)
        assert(currentTurn != currentTurn.clone(currentTurn))
        val overNextTurn = nextTurn.clone(nextTurn)
        currentTurn.nextState = nextTurn
        nextTurn.previousState = currentTurn
        assert(currentTurn.nextState == nextTurn)
        assert(currentTurn.nextState!!.previousState == currentTurn)
        currentTurn.nextState!!.nextState = overNextTurn
        currentTurn.nextState!!.nextState!!.previousState = currentTurn.nextState
        assert(currentTurn.nextState!!.nextState != nextTurn)
        assert(currentTurn.nextState!!.nextState != currentTurn)
        val turn1 = currentTurn
        val turn2 = currentTurn.nextState
        val turn3 = currentTurn.nextState!!.nextState
        assert(turn1 == currentTurn)
        assert(turn2 == nextTurn)
        assert(turn3 == overNextTurn)
        assert(turn3!!.previousState == nextTurn)
        assert(turn3.previousState!!.previousState == currentTurn)
        assert(turn1.nextState!!.nextState!!.previousState == nextTurn)
        assert(turn1.nextState!!.nextState!!.previousState == currentTurn.nextState)

//        assert(currentTurn.offerDisplay[0] != overNextTurn.offerDisplay[0])
        assertEquals(currentTurn.offerDisplay[0], overNextTurn.offerDisplay[0])
    }


}