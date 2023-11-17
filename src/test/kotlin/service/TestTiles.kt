package service

import entity.*

/**
 * [TestTiles] is not a TestClass! It just provides tiles for tests
 */
class TestTiles {

    var listOfTiles1: MutableList<Tile> = mutableListOf()
    var listOfTiles2: MutableList<Tile> = mutableListOf()
    var listOfTiles3: MutableList<Tile> = mutableListOf()
    private var arrowList1 = listOf<Arrow>()
    private var arrowList2 = listOf<Arrow>()
    private var arrowList3 = listOf<Arrow>()
    private var arrowList4 = listOf<Arrow>()
    var checkOccupiedTiles = mutableListOf<Tile>()

    var player1 = Player(
        points = 0,
        name = "Bob 1",
        discsLeftCount = 0,
        cacoCount = 0,
        placedTiles = mutableListOf(),
        color = DiscColor.BLACK,
        playerType = PlayerType.RANDOM_AI
    )
    var player2 = Player(
        points = 0,
        name = "Bob 2",
        discsLeftCount = 10, cacoCount = 0,
        placedTiles = mutableListOf(),
        color = DiscColor.BLACK,
        playerType = PlayerType.RANDOM_AI
    )

    /**
     * Starts the creation of all functions in this utility class.
     */
    fun createTiles() {
        createTiles1()
        createTiles2()
        createTile3()
    }

    /**
     *                      X :
     *          -3  -2  -1   0   1   2   3   4  5
     *      3    -   -   -   -   -   -   -   -  -
     *      2    -   -   -   -   -   -   -   -  -
     *      1    -   -   -   #   #   #   #   #  -
     * Y :  0    -   -   -   #   #   #   #   #  -
     *     -1    -   -   -   -   -   -   -   -  -
     *     -2    -   -   -   -   -   -   -   -  -
     *     -3    -   -   -   -   -   -   -   -  -
     *     -4    -   -   -   -   -   -   -   -  -
     *
     *      # - Placed tiles
     */
    fun createTiles1() {
        // Each tile has no arrows for clarity
        listOfTiles1 = mutableListOf(
            Tile(
                0, 10, 0, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 1, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 2, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 3, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 0, 1, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 1, 1, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 2, 1, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 3, 1, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 4, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE),
            Tile(
                0, 10, 4, 1, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE))
    }

    /**
     *                      X :
     *          -3  -2  -1   0   1   2   3   4
     *      3    -   -   -   -   -   -   -   -
     *      2    -   -   -   -   -   -   -   -
     *      1    -   -   #   -   -   -   -   -
     * Y :  0    -   #   #   #   #   -   -   -
     *     -1    -   -   -   #   -   -   -   -
     *     -2    -   -   -   -   -   -   -   -
     *     -3    -   -   -   -   -   -   -   -
     *     -4    -   -   -   -   -   -   -   -
     *
     *      # - Placed tiles
     */
    fun createTiles2() {
        // Each tile has no arrows for clarity
        listOfTiles2 = mutableListOf(
            Tile(
                5, 1, 0, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.BOTTOM, TileSpirit.EARTH)),
                TileSpirit.WATER
            ),
            Tile(
                6, 1, 0, -1, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.RIGHT, TileSpirit.AIR)),
                TileSpirit.WATER
            ),
            Tile(
                7, 1, 1, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.AIR)),
                TileSpirit.EARTH
            ),
            Tile(
                8, 10, -1, 0, 0,
                mutableListOf(DiscColor.BLACK, DiscColor.BLACK, DiscColor.BLACK, DiscColor.BLACK),
                mutableListOf(
                    Arrow(false, TileArrow.TOP, TileSpirit.FIRE),
                    Arrow(true, TileArrow.LEFT, TileSpirit.WATER),
                    Arrow(true, TileArrow.RIGHT, TileSpirit.EARTH),
                    Arrow(true, TileArrow.BOTTOM, TileSpirit.WATER)
                ),
                TileSpirit.WATER
            ),
            Tile(
                9, 1, -2, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.BOTTOM, TileSpirit.WATER)),
                TileSpirit.WATER
            ),
            Tile(
                10, 1, -1, 1, 0,
                mutableListOf(),
                mutableListOf(Arrow(true, TileArrow.BOTTOM, TileSpirit.WATER)),
                TileSpirit.WATER
            )
        )
    }

    /**
     * Creates a list of tiles of size 4, with nearly identical tiles. All infos the same besides varying tile id's and
     * tile spirits.
     */
    fun createTile3() {
        // Each tile has no arrows for clarity
        listOfTiles3 = mutableListOf(
            Tile(
                0, 1, 0, 0, 0,
                mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.FIRE
            ),
            Tile(
                1, 1, 0, 0, 0,
                mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.AIR
            ),
            Tile(
                2, 1, 0, 0, 0,
                mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.WATER
            ),
            Tile(
                4, 1, 0, 0, 0,
                mutableListOf(),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.EARTH
            )
        )
    }

    /**
     *                      X :
     *          -3  -2  -1   0   1   2   3   4
     *      3    -   -   -   -   -   -   -   -
     *      2    -   -   -   -   -   -   -   -
     *      1    -   -   -   #   -   -   -   -
     * Y :  0    -   -   -   #   -   -   -   -
     *     -1    -   -   -   -   -   -   -   -
     *     -2    -   -   -   -   -   -   -   -
     *     -3    -   -   -   -   -   -   -   -
     *     -4    -   -   -   -   -   -   -   -
     *
     *      # - Placed tiles
     */
    val justOneArrowNotOccupiedTile = mutableListOf<Tile>(
        Tile(
            0, 10, 0, 0, 0, mutableListOf(),
            listOf(
                Arrow(true, TileArrow.TOP, TileSpirit.AIR),
                Arrow(true, TileArrow.TOPLEFT, TileSpirit.EARTH),
                Arrow(false, TileArrow.BOTTOM, TileSpirit.FIRE),
                Arrow(false, TileArrow.LEFT, TileSpirit.WATER)
            ), TileSpirit.AIR
        ),
        Tile(
            0, 10, 0, 1, 0, mutableListOf(),
            listOf(
                Arrow(true, TileArrow.TOP, TileSpirit.AIR),
                Arrow(true, TileArrow.TOPLEFT, TileSpirit.EARTH),
                Arrow(true, TileArrow.BOTTOM, TileSpirit.FIRE),
                Arrow(false, TileArrow.LEFT, TileSpirit.WATER)
            ), TileSpirit.AIR
        )
    )

    private fun createArrowLists() {
        arrowList1 = listOf(
            Arrow(false, TileArrow.TOP, TileSpirit.AIR),
            Arrow(false, TileArrow.TOPLEFT, TileSpirit.EARTH),
            Arrow(false, TileArrow.BOTTOM, TileSpirit.FIRE),
            Arrow(false, TileArrow.LEFT, TileSpirit.WATER)
        )
        arrowList2 = listOf(
            Arrow(false, TileArrow.TOP, TileSpirit.EARTH),
            Arrow(false, TileArrow.RIGHT, TileSpirit.FIRE),
            Arrow(false, TileArrow.BOTTOM, TileSpirit.WATER),
            Arrow(false, TileArrow.LEFT, TileSpirit.AIR)
        )
        arrowList3 = listOf(
            Arrow(false, TileArrow.TOP, TileSpirit.FIRE),
            Arrow(false, TileArrow.RIGHT, TileSpirit.WATER),
            Arrow(false, TileArrow.BOTTOM, TileSpirit.AIR),
            Arrow(false, TileArrow.LEFT, TileSpirit.EARTH)
        )
        arrowList4 = listOf(
            Arrow(false, TileArrow.TOP, TileSpirit.WATER),
            Arrow(false, TileArrow.RIGHT, TileSpirit.AIR),
            Arrow(false, TileArrow.BOTTOM, TileSpirit.EARTH),
            Arrow(false, TileArrow.LEFT, TileSpirit.FIRE)
        )
    }

    /**
     * Creates the tiles and the list necessary for the test of checkOccupied.
     */
    fun createCheckOccupiedTiles() {
        this.createArrowLists()
        val tile1 = Tile(0, 10, 0, 0, 0, mutableListOf(), arrowList1, TileSpirit.AIR)
        val tile2 = Tile(0, 10, 0, 1, 0, mutableListOf(), arrowList2, TileSpirit.FIRE)
        val tile3 = Tile(0, 10, 0, 2, 0, mutableListOf(), arrowList3, TileSpirit.WATER)
        val tile4 = Tile(0, 10, 0, 3, 0, mutableListOf(), arrowList4, TileSpirit.EARTH)
        val tile5 = Tile(0, 10, -1, 2, 0, mutableListOf(), arrowList4, TileSpirit.EARTH)
        val tile6 = Tile(0, 10, -2, 2, 0, mutableListOf(), arrowList4, TileSpirit.EARTH)
        checkOccupiedTiles = mutableListOf(tile1, tile2, tile3, tile4, tile5, tile6)
    }
}