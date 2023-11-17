package service

import entity.*
import kotlin.test.*

/**
 * Testclass for the [GameService] class. Tests the basic functions provided by GameService to run the game.
 */
class GameServiceTest {

    /**
     * Tests if arrows are correctly identified and marked as occupied if the criteria is met.
     */
    @Test
    fun checkOccupiedTest() {
        val testTiles = TestTiles()
        testTiles.createCheckOccupiedTiles()
        val tiles = testTiles.checkOccupiedTiles

        val player1 = Player(
            points = 0,
            pointsOrder = 0,
            name = "Spieler1",
            discsLeftCount = 0,
            cacoCount = 0,
            placedTiles = tiles,
            color = DiscColor.BLACK,
            playerType = PlayerType.HUMAN
        )

        val player2 = Player(
            points = 0,
            pointsOrder = 0,
            name = "Spieler2",
            discsLeftCount = 24,
            cacoCount = 0,
            placedTiles = mutableListOf<Tile>(),
            color = DiscColor.GREY,
            playerType = PlayerType.HUMAN
        )

        val rootService = RootService()
        rootService.currentGame = Sagani(
            0,
            0,
            0,
            null,
            null,
            mutableListOf<Tile>(),
            mutableListOf(),
            mutableListOf(),
            listOf(player1, player2),
            SimulationSpeed.VERY_SLOW
        )

        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                tiles[0],
                Arrow(false, TileArrow.TOPLEFT, TileSpirit.EARTH)
            ), true
        )
        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                tiles[0],
                Arrow(false, TileArrow.TOP, TileSpirit.AIR)
            ), false
        )

        player1.placedTiles[0].rotation = 90

        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                player1.placedTiles[0],
                Arrow(false, TileArrow.LEFT, TileSpirit.WATER)
            ), true
        )


        // Test coverage for checkOccupied()
        // (TileArrow.TOPRIGHT, TileArrow.BOTTOMRIGHT, TileArrow.BOTTOMLEFT)
        val listOfArrows = mutableListOf(
            Arrow(
                false,
                TileArrow.TOPRIGHT,
                TileSpirit.AIR
            ),
            Arrow(
                false,
                TileArrow.BOTTOMRIGHT,
                TileSpirit.AIR
            ),
            Arrow(
                false,
                TileArrow.BOTTOMLEFT,
                TileSpirit.AIR
            )
        )

        val listOfTiles = mutableListOf<Tile>()
        listOfTiles.add(
            Tile(
                0,
                0,
                0,
                0,
                0,
                mutableListOf(),
                listOfArrows,
                TileSpirit.AIR
            )
        )

        for (i in -1..1) {
            for (j in -1..1) {
                if (i != 0 || j != 0) {
                    listOfTiles.add(
                        Tile(
                            0,
                            0,
                            i,
                            j,
                            0,
                            mutableListOf(),
                            mutableListOf(),
                            TileSpirit.AIR
                        )
                    )
                }
            }
        }

        player1.placedTiles = listOfTiles

        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                player1.placedTiles[0],
                listOfArrows[0]
            ), true
        )

        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                player1.placedTiles[0],
                listOfArrows[1]
            ), true
        )

        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                player1.placedTiles[0],
                listOfArrows[2]
            ), true
        )

        // (invalideTile(player, tile)
        val testTile = Tile(
            0,
            10,
            99,
            99,
            0,
            mutableListOf(),
            mutableListOf(),
            TileSpirit.AIR
        )

        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                testTile,
                listOfArrows[0]
            ), false
        )

        player1.placedTiles = mutableListOf()
        assertEquals(
            rootService.gameService.checkOccupied(
                player1,
                testTile,
                listOfArrows[0]
            ), false
        )

        /////////////////////////////

        // Fourth tile of listOfTiles2 has 4 arrow with 3 of them already
        // occupied. The Bottom facing arrow is wrongly set to true and this
        // test evaluates if checkOccupied correctly switches it to false.
        player2.placedTiles.clear()
        testTiles.createTiles()
        player2.placedTiles = testTiles.listOfTiles2
        val tile = player2.placedTiles[3]
        /*for (i in 0..3) {
            println("Vorher: " + player2.placedTiles[3].arrows[i])
        }*/
        player2.placedTiles[3].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, tile, it)
        }
        /*for (i in 0..3) {
            println("Nachher: " + player2.placedTiles[3].arrows[i])
        }*/
        assertFalse(player2.placedTiles[3].arrows[3].occupied)

        // Follow up to test if it correctly sets occupied to true false if tiles are rotated
        player2.placedTiles[3].rotation = 270
        player2.placedTiles[3].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, tile, it)
        }
        assert(player2.placedTiles[3].arrows[3].occupied)
        assertFalse(player2.placedTiles[3].arrows[1].occupied)


        // Tests if diagonal arrows are correctly set from false to  true and otherwise
        val diagTile = Tile(
            11, 6, 0, 1, 0,
            mutableListOf(DiscColor.BROWN, DiscColor.BROWN, DiscColor.BROWN),
            listOf(
                Arrow(false, TileArrow.BOTTOMLEFT, TileSpirit.WATER),
                Arrow(false, TileArrow.BOTTOMRIGHT, TileSpirit.EARTH),
                Arrow(false, TileArrow.TOPRIGHT, TileSpirit.AIR),
                Arrow(true, TileArrow.TOPLEFT, TileSpirit.FIRE)
            ),
            TileSpirit.FIRE
        )
        player2.placedTiles.add(diagTile)
        player2.placedTiles[6].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, player2.placedTiles[6], it)
        }
        val placedTiles = player2.placedTiles
        assert(placedTiles[6].arrows[0].occupied)
        assert(placedTiles[6].arrows[1].occupied)
        assertFalse(placedTiles[6].arrows[2].occupied)
        assertFalse(placedTiles[6].arrows[3].occupied)

        // Test checkOccupied for diagonal arrows over longer distance without any tiles in between
        // (theorectically illegally placed tile but possible situation)
        val tile2 = Tile(
            12, 6, -2, 3, 0,
            mutableListOf(DiscColor.BROWN, DiscColor.BROWN, DiscColor.BROWN),
            listOf(Arrow(false, TileArrow.BOTTOMRIGHT, TileSpirit.EARTH),
                Arrow(false, TileArrow.TOPRIGHT,TileSpirit.AIR)
            ),
            TileSpirit.FIRE
        )
        placedTiles.add(tile2)
        player2.placedTiles[6].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, player2.placedTiles[6], it)
        }
        assert(placedTiles[6].arrows[3].occupied)

        // Test checkOccupied for diagonal arrows over longer distance with tiles in between
        player2.placedTiles[7].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, player2.placedTiles[7], it)
        }
        assert(placedTiles[7].arrows[0].occupied)

        // Test checkOccupied if it acts correctly with multiple tiles fulfilling the arrow condition
        val tile3 = Tile(
            13, 1, -1, 2, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.AIR)),
            TileSpirit.EARTH)
        player2.placedTiles.add(tile3)
        player2.placedTiles[7].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, player2.placedTiles[7], it)
        }
        assert(placedTiles[7].arrows[0].occupied)

        // Tests if checkOccupied works for diagonal arrows on rotated tiles
        val tile4 = Tile(
            14, 1, 2, -1, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.AIR)),
            TileSpirit.AIR)
        player2.placedTiles.add(tile4)
        player2.placedTiles[7].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, player2.placedTiles[7], it)
        }
        assert(placedTiles[7].arrows[0].occupied)


        player2.placedTiles[7].rotation = 90
        println(player2.placedTiles[7])
        player2.placedTiles[7].arrows.forEach {
            it.occupied = rootService.gameService.checkOccupied(player2, player2.placedTiles[7], it)
        }
        assert(placedTiles[7].arrows[1].occupied)
    }




    /**
     * Function that test if the game states nextState and previousState are correctly initialized.
     */
    @Test
    fun copyGameStatesTest() {
        val infos = TestTiles()
        infos.createTiles1()
        infos.createTiles2()
        val players = listOf(infos.player1, infos.player2)

        val testState1 = Sagani(
            0,
            0,
            0,
            null,
            null,
            mutableListOf(),
            mutableListOf(),
            infos.listOfTiles1,
            players,
            SimulationSpeed.NO_SIM
        )
        testState1.drawStack = infos.listOfTiles1.subList(0, 5)
        testState1.offerDisplay = infos.listOfTiles1.subList(5, 10)

        val testState2 = testState1.copy()
        assertEquals(testState2.players.size, 2)
        assertEquals(testState2.drawStack.size, 5)
        assertEquals(testState2.offerDisplay.size, 5)
    }

    /**
     * Tests, if a game gets started properly.
     */
    @Test
    fun startGameTest() {
        val testTiles = TestTiles()
        testTiles.createTiles()
        val tile1 = testTiles.listOfTiles1[0].clone()
        println(tile1)
        val rootService = RootService()

        rootService.gameService.startGame(listOf(testTiles.player1, testTiles.player2))
        println(rootService.currentGame!!.offerDisplay.size)
        val game = rootService.currentGame
        game!!.offerDisplay[0] = tile1
        assert(game.offerDisplay.contains(tile1))

        val tile2 = tile1.clone()
        tile2.soundDiscs = mutableListOf(DiscColor.RED)
        println(tile2)

        val offerTile = game.offerDisplay[1].clone()
        println(offerTile)
        assert(game.offerDisplay.contains(offerTile))

        offerTile.arrows[0].occupied = true
    }
}