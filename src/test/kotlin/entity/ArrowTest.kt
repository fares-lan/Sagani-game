package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Test cases for Arrow
 */


class ArrowTest {

    // arrows for testing

    private val arrow1 = Arrow(false,TileArrow.TOPLEFT,TileSpirit.AIR)
    private val arrow2 = Arrow(true,TileArrow.BOTTOM,TileSpirit.WATER)

    /**
     * check if arrows are created correctly
     */

    @Test
    fun arrowsTest(){
        assertEquals(arrow1, Arrow(false,TileArrow.TOPLEFT,TileSpirit.AIR))
        assertEquals(arrow2.orientation, TileArrow.BOTTOM)
        assertEquals(arrow2.occupied,true)
        assertEquals(arrow2.spirit,TileSpirit.WATER)
        assertNotEquals(arrow1.spirit, TileSpirit.WATER)
        assertNotEquals(arrow1.occupied,true)
    }
}