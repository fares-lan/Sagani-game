package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * test cases for Tile
 */

class TileTest {
    /**
     * four tiles for testing, all having different point value
     */

    private val tile1 = Tile(
        0, 1, 1, 400, 0, mutableListOf(DiscColor.BLACK),
        mutableListOf(Arrow(false, TileArrow.RIGHT, TileSpirit.AIR)), TileSpirit.EARTH
    )
    private val tile2 = Tile(
        0, 3, -1, 1, 90, mutableListOf(DiscColor.GREY),
        mutableListOf(
            Arrow(false, TileArrow.TOP, TileSpirit.AIR),
            Arrow(true, TileArrow.LEFT, TileSpirit.EARTH)
        ), TileSpirit.FIRE
    )
    private val tile3 = Tile(
        0, 6, 0, 0, 180, mutableListOf(DiscColor.BLACK),
        mutableListOf(
            Arrow(false, TileArrow.RIGHT, TileSpirit.AIR),
            Arrow(false, TileArrow.TOPRIGHT, TileSpirit.AIR),
            Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)
        ), TileSpirit.WATER
    )
    private val tile4 = Tile(
        0, 10, 1000, -600, 270, mutableListOf(DiscColor.BLACK),
        mutableListOf(
            Arrow(false, TileArrow.BOTTOMRIGHT, TileSpirit.AIR),
            Arrow(false, TileArrow.BOTTOMLEFT, TileSpirit.AIR),
            Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR),
            Arrow(false, TileArrow.BOTTOM, TileSpirit.AIR)
        ), TileSpirit.AIR
    )

    /**
     * test orientation
     */

    @Test
    fun testOrientation() {
        assertEquals(tile1.arrows[0].orientation, TileArrow.RIGHT)
        assertEquals(tile2.arrows[0].orientation, TileArrow.TOP)
        assertEquals(tile2.arrows[1].orientation, TileArrow.LEFT)
        assertEquals(tile3.arrows[0].orientation, TileArrow.RIGHT)
        assertEquals(tile3.arrows[1].orientation, TileArrow.TOPRIGHT)
        assertEquals(tile3.arrows[2].orientation, TileArrow.TOPLEFT)
        assertEquals(tile4.arrows[0].orientation, TileArrow.BOTTOMRIGHT)
        assertEquals(tile4.arrows[1].orientation, TileArrow.BOTTOMLEFT)
        assertEquals(tile4.arrows[2].orientation, TileArrow.TOPLEFT)
        assertEquals(tile4.arrows[3].orientation, TileArrow.BOTTOM)
        assertNotEquals(tile1.arrows[0].orientation, TileArrow.BOTTOMRIGHT)
    }

    /**
     * test rotation
     */
    @Test
    fun testRotation() {
        assertEquals(tile1.rotation, 0)
        assertEquals(tile2.rotation, 90)
        assertEquals(tile3.rotation, 180)
        assertEquals(tile4.rotation, 270)
        assertNotEquals(tile1.rotation, 270)
    }

    /**
     * test points
     */
    @Test
    fun testPoints() {
        assertEquals(tile1.points, 1)
        assertEquals(tile2.points, 3)
        assertEquals(tile3.points, 6)
        assertEquals(tile4.points, 10)
        assertNotEquals(tile1.points, 2)
    }

    /**
     * test postion
     */

    @Test
    fun testPosition() {
        assertEquals(tile1.posX, 1)
        assertEquals(tile2.posX, -1)
        assertEquals(tile3.posX, 0)
        assertEquals(tile4.posX, 1000)
        assertEquals(tile1.posY, 400)
        assertEquals(tile2.posY, 1)
        assertEquals(tile3.posY, 0)
        assertEquals(tile4.posY, -600)
        assertNotEquals(tile1.posX, 0)
        assertNotEquals(tile4.posY, 600)

    }

    /**
     * test spirit
     */

    @Test
    fun testSpirit() {
        assertEquals(tile1.spirit, TileSpirit.EARTH)
        assertEquals(tile2.spirit, TileSpirit.FIRE)
        assertEquals(tile3.spirit, TileSpirit.WATER)
        assertEquals(tile4.spirit, TileSpirit.AIR)
        assertNotEquals(tile1.spirit, TileSpirit.WATER)
    }

    /**
     * Testfunction to ensure the correct execution of the clone()-function to create deep copies of [Tile] entities.
     */
    @Test
    fun cloneTileTest() {
        // all testtiles different objects?
        assert(tile1 != tile2)
        assert(tile1 != tile3)
        assert(tile2 != tile3)

        val tile1Clone = tile1.clone()
//        assert(tile1 != tile1Clone)

        tile1.posX = 5
        assert(tile1.posX != tile1Clone.posX)
        assertEquals(tile1Clone.posX,1)
        assertEquals(tile1.posX, 5)


        
        val tile3Clone = tile3.clone()
//        assert(tile3Clone != tile3)
        assertEquals(tile3Clone.id, 0)
        assertEquals(tile3Clone.points, 6)
        assertEquals(tile3Clone.posX, 0)
        assertEquals(tile3Clone.posY, 0)
        assertEquals(tile3Clone.rotation, 180)
        assertEquals(tile3Clone.soundDiscs, mutableListOf(DiscColor.BLACK))
        assertEquals(
            tile3Clone.arrows, mutableListOf(
                Arrow(false, TileArrow.RIGHT, TileSpirit.AIR),
                Arrow(false, TileArrow.TOPRIGHT, TileSpirit.AIR),
                Arrow(false, TileArrow.TOPLEFT, TileSpirit.AIR)
            )
        )
        assertEquals(tile3Clone.spirit, TileSpirit.WATER)
        val list = mutableListOf(tile1, tile2, tile3)
        val listClone = list.map { it.clone() }.toMutableList()
//        assert(list != listClone)
        assertEquals(list[0].id, listClone[0].id)
        assertEquals(list[1].posX, listClone[1].posX)
        assertEquals(list[2].soundDiscs,listClone[2].soundDiscs)

        list[2] = tile4
        assert(list[2].spirit != listClone[2].spirit)
    }

}