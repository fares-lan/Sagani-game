package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * test cases for Player
 */

class PlayerTest {

    // players for testing

    private val player1 = Player(0,0,"Alice",24,0, mutableListOf(), DiscColor.GREY,
        PlayerType.HUMAN)
    private val player2 = Player(40,1,"Bob",15,3, mutableListOf(), DiscColor.BLACK,
        PlayerType.RANDOM_AI)

    /**
     * tests for player1
     */

    @Test
    fun player1Test(){
        assertEquals(player1.points,0)
        assertEquals(player1.pointsOrder,0)
        assertEquals(player1.cacoCount,0)
        assertEquals(player1.discsLeftCount,24)
        assertEquals(player1.color, DiscColor.GREY)
        assertEquals(player1.name, "Alice")
        assertNotEquals(player1.name, "Bob")
        assertNotEquals(player1.playerType, PlayerType.SMART_AI)
    }

    /**
     * tests for player2
     */
    @Test
    fun player2Test(){
        assertEquals(player2.points,40)
        assertEquals(player2.pointsOrder,1)
        assertEquals(player2.cacoCount,3)
        assertEquals(player2.discsLeftCount,15)
        assertEquals(player2.color, DiscColor.BLACK)
        assertEquals(player2.name, "Bob")
        assertEquals(player2.playerType,PlayerType.RANDOM_AI)
        assertNotEquals(player2.playerType,PlayerType.SMART_AI)

    }
}