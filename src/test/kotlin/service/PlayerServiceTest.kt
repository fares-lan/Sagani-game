package service

import entity.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.*


/**
 * Contains the tests for all public methods provided by the [PlayerService].
 * All public methods are more or less direct actions the player actively chooses.
 */
class PlayerServiceTest {

    private val arrowList1 = listOf(
        Arrow(false, TileArrow.TOP, TileSpirit.AIR),
        Arrow(false, TileArrow.RIGHT, TileSpirit.EARTH),
        Arrow(false, TileArrow.BOTTOM, TileSpirit.FIRE),
        Arrow(false, TileArrow.LEFT, TileSpirit.WATER)
    )
    private val arrowList2 = listOf(
        Arrow(true, TileArrow.TOP, TileSpirit.EARTH),
        Arrow(true, TileArrow.RIGHT, TileSpirit.FIRE),
    )

    private val soundDiscs2 = mutableListOf(
        DiscColor.BLACK, DiscColor.BLACK
    )

    //Tile for checkPlaceIsValidTest()
    private val tile1 = Tile(0, 10, 2, 5, 0, mutableListOf(), arrowList1, TileSpirit.AIR)

    //Tile for checkAndMoveDisksTest()
    private val tile2 = Tile(0, 10, 0, 1, 0, soundDiscs2, arrowList2, TileSpirit.FIRE)

    //tile for makeMoveTest()
    private val tile3 = Tile(0, 10, 1, 1, 0, mutableListOf(), arrowList2, TileSpirit.WATER)

    private val tiles = mutableListOf(tile1, tile2, tile1, tile1, tile1)

    private val player1 = Player(
        points = 0,
        pointsOrder = 0,
        name = "Spieler1",
        discsLeftCount = 22,
        cacoCount = 0,
        placedTiles = mutableListOf(),
        color = DiscColor.BLACK,
        playerType = PlayerType.HUMAN
    )

    private val player2 = Player(
        points = 0,
        pointsOrder = 0,
        name = "Spieler2",
        discsLeftCount = 24,
        cacoCount = 0,
        placedTiles = mutableListOf(),
        color = DiscColor.BROWN,
        playerType = PlayerType.HUMAN
    )

    private val player3 = Player(
        points = 0,
        pointsOrder = 0,
        name = "Spieler3",
        discsLeftCount = 22,
        cacoCount = 0,
        placedTiles = mutableListOf(),
        color = DiscColor.BLACK,
        playerType = PlayerType.HUMAN
    )

    private val player4 = Player(
        points = 0,
        pointsOrder = 0,
        name = "Spieler4",
        discsLeftCount = 24,
        cacoCount = 0,
        placedTiles = mutableListOf(),
        color = DiscColor.BROWN,
        playerType = PlayerType.HUMAN
    )
/*
    /**
     * Tests the functionality of makeMove().
     */
    @Test
    fun makeMoveTest() {
        val rootService = RootService()

        rootService.currentGame = Sagani(
            0,
            0, -1, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(),
            mutableListOf(player1, player2), SimulationSpeed.NO_SIM,
            intermezzoOrder = mutableListOf(0,1), startedIntermezzo = false,
        )

        var tile = Tile(0, 10, 0, 0, 0, mutableListOf(), arrowList2, TileSpirit.WATER)

        rootService.currentGame!!.offerDisplay.add(tile)

        rootService.currentGame!!.drawStack = tiles

        println(rootService.currentGame!!.playerIndex)

        rootService.playerService.makeMove(tile, 0, 0, 180, "")

        var  player = rootService.currentGame!!.players[0]
        //see, if the tiles attributes have been updated
        assertEquals(0, tile.posX)
        assertEquals(0, tile.posY)
        assertEquals(180, tile.rotation)

        //see, if the tile got put in placedTiles
        assertEquals(player.placedTiles[0], tile)

        //see, if tile got removed from where it came (offerDisplay)
        assertNotEquals(tile3, rootService.currentGame!!.offerDisplay[0])

        assertEquals(24-tile.arrows.size, player.discsLeftCount)

        //add tile to intermezzoDisplay for testing
        tile = Tile(6, 10, 0, 0, 0, mutableListOf(), arrowList2, TileSpirit.WATER)
        rootService.currentGame!!.intermezzoDisplay.add(tile)
        rootService.currentGame!!.offerDisplay.remove(tile)
        println()
        println(rootService.currentGame!!.intermezzoDisplay)
        println(rootService.currentGame!!.offerDisplay)

        println(rootService.currentGame!!.playerIndex)

        rootService.playerService.makeMove(tile, 0, 0, 180, "")
        println( rootService.currentGame!!.intermezzoDisplay)

        player = rootService.currentGame!!.players[1]
        assertEquals(player.placedTiles[0], tile)

        //see, if tile got removed from where it came (intermezzoDisplay)
        println("intermezzo size: " + rootService.currentGame!!.intermezzoDisplay.size)
        assertEquals(0, rootService.currentGame!!.intermezzoDisplay.size )

        //add tile to drawStack for testing
        tile = Tile(16, 10, 0, 0, 0, mutableListOf(), arrowList2, TileSpirit.WATER)
        rootService.currentGame!!.drawStack.add(tile)
        rootService.currentGame!!.intermezzoDisplay.remove(tile)

        rootService.playerService.makeMove(tile, 1, 0, 180,"")

        player = rootService.currentGame!!.players[0]
        assertContains(player.placedTiles, tile)

        //see, if tile got removed from where it came (drawStack)
        assert(rootService.currentGame!!.drawStack.isEmpty())

        //checks, if an exception gets thrown if checkPlaceIsValid returns false
        val exception: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.makeMove(tile3, 100, 200, 180,"") }

        assertEquals("Invalid move! The tile cannot be placed at the specified position.", exception.message)

        val rootService2 = RootService()

        rootService2.gameService.startGame(listOf(player3,player4))
        rootService2.playerService.makeMove(rootService.currentGame!!.offerDisplay[0],0,0,0,"")
    }

 */



    /**
     * Checks if the checkPlaceIsValid function works correctly with different given coordinates.
     */
    @Test
    fun checkPlaceIsValidTest() {
        val rootService = RootService()

        rootService.currentGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(),
            mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        //placedTiles is empty, first tile is placed at (0,0)
        val firstPlacedTile = rootService.playerService.checkPlaceIsValid(0, 0)

        //see, if the player has no placed tiles
        assertTrue(player1.placedTiles.isEmpty())

        //checks, if checkPlaceIsValid returns true
        assertEquals(true, firstPlacedTile)

        //adds a tile to the placedTiles of the player for the next test
        player1.placedTiles.add(tile1)

        //tries to add a tile to where another tile is already placed
        val equalCoordinates = rootService.playerService.checkPlaceIsValid(2, 5)

        assertEquals(false, equalCoordinates)

        //should be true, because there is a tile at (2,5)
        val tileRightOfPlacedTile = rootService.playerService.checkPlaceIsValid(3, 5)

        assertEquals(true, tileRightOfPlacedTile)

        //should be true, because there is a tile at (2,5)
        val tileAboveOfPlacedTile = rootService.playerService.checkPlaceIsValid(2, 6)

        assertEquals(true, tileAboveOfPlacedTile)

        //should be false, because none of the if's in checkPlaceIsValid is entered
        val placeTileAtRandomPosition = rootService.playerService.checkPlaceIsValid(7, 17)

        assertEquals(false, placeTileAtRandomPosition)

        // try placing it diagonally without a vertical or horizontal tile to connect
        val placeTileDiagonally = rootService.playerService.checkPlaceIsValid(3,6)
        assertFalse(placeTileDiagonally)
    }

    /**
     * Checks if the soundDiscs are correctly placed and moved if the condition of an arrow is met.
     * As well as removing all discs and returning them correctly to the owner if all arrows are fulfilled.
     */
    @Test
    fun checkAndMoveDiscsTest() {
        val rootService = RootService()

        rootService.currentGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(),
            mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        //add tile2, which has all of its arrows occupied
        player1.placedTiles.add(tile2)

        //check, if the placed tile has sound discs before executing function
        assert(player1.placedTiles[0].soundDiscs.isNotEmpty())

        rootService.playerService.checkAndMoveDiscs()

        //check, if the sound discs got removed from the completed tile
        assert(player1.placedTiles[0].soundDiscs.isEmpty())

        //check, if the 2 sound discs that were on the tile got returned to the player (Player1)
        assertEquals(24, rootService.currentGame!!.players[0].discsLeftCount)

        //set the occupation of one arrow of tile1 to false in order to test the checkOccupied-part of the function
        player1.placedTiles[0].arrows[0].occupied = false

        rootService.playerService.checkAndMoveDiscs()

        //checks, if occupation stays false
        //(it stays false, because there are no placed tiles other than tile2)
        assertEquals(false, rootService.currentGame!!.players[0].placedTiles[0].arrows[0].occupied)
    }

    /**
     * Checks if sound-Discs are being placed properly on to a tile.
     */
    @Test
    fun placeSoundDiscsTest() {
        val rootService = RootService()

        rootService.currentGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(),
            mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        rootService.playerService.placeSoundDiscs(tile1)

        //checks, if the players discsLeftCount got decreased by the number of arrows tile1 has (4)
        assertEquals(18, player1.discsLeftCount)

        //checks, if the disks have been put on to tile1 and if the color matches player1's color
        for (i in 0 until 4) {
            assertEquals(DiscColor.BLACK, tile1.soundDiscs[i])
        }

        //takes all discs from player1 to test placement of cacophony discs
        player1.discsLeftCount = 0
        tile1.soundDiscs.clear()

        rootService.playerService.placeSoundDiscs(tile1)

        //checks, if the players cacoCount got increased by the number of arrows tile1 has (4)
        assertEquals(4, player1.cacoCount)

        //checks, if the disks have been put on to tile1 and if the color is red (-> caco)
        for (i in 0 until 4) {
            assertEquals(DiscColor.RED, tile1.soundDiscs[i])
        }

    }

    /**
     * Checks if the simulationSpeed of the AI Players is correctly changed.
     */
    @Test
    fun changeSimSpeedTest() {
        val rootService = RootService()

        rootService.currentGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        rootService.playerService.changeSimSpeed(SimulationSpeed.SLOW)

        assertEquals(SimulationSpeed.SLOW, rootService.currentGame!!.simSpeed)
    }

    /**
     * Checks if finished move is correctly undone and the previous game state is restored.
     */
    /*@Test
    fun undoTest() {
        val rootService = RootService()

        val previousGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        val currentGame = Sagani(
            0,
            0, 0, null, previousGame,
            mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        rootService.currentGame = currentGame

        assertEquals(currentGame, rootService.currentGame)

        rootService.playerService.undo()

        //checks, if the current game has become the previous game
        assertEquals(previousGame, rootService.currentGame)

        val rootService2 = RootService()

        rootService2.gameService.startGame(mutableListOf(player1, player2))

        val stateBefore = rootService2.currentGame
        rootService2.playerService.makeMove(tile1, 0, 0, 0)

        assertTrue(rootService2.currentGame!!.previousState != null)
        assertEquals(stateBefore, rootService2.currentGame!!.previousState)

        rootService2.playerService.undo()

        assertEquals(stateBefore, rootService2.currentGame)
    }

     */

    /**
     * Checks if a previously undone move is correctly restored.
     */
    @Test
    fun redoTest() {
        val rootService = RootService()

        val nextGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        val currentGame = Sagani(
            0,
            0, 0, nextGame, null,
            mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        rootService.currentGame = currentGame

        assertEquals(currentGame, rootService.currentGame)

        rootService.playerService.redo()

        //checks, if the current game has become the next game
        assertEquals(nextGame, rootService.currentGame)
    }
/*
    /**
     * Checks if the pass move is working correctly in the intermezzo phase.
     */
    @Test
    fun passMoveIntermezzoTest() {
        val testTiles = TestTiles()
        testTiles.createTiles()

        val rootService = RootService()

        rootService.currentGame = Sagani(
            0,
            0,
            -1,
            null,
            null,
            testTiles.listOfTiles1.subList(0, 5),
            mutableListOf(),
            mutableListOf(),
            listOf(player1, player2),
            SimulationSpeed.NO_SIM,
            lastRound = false,
            startedIntermezzo = true,
            startedLastRound = false,
            intermezzoOrder = mutableListOf(0, 1)
        )

        //save the current number in the counter
        val intermezzoCounterBefore = rootService.currentGame!!.intermezzoMoveCounter

        //add tiles to test first if statement
        rootService.currentGame!!.intermezzoDisplay.add(tile1)
        rootService.currentGame!!.intermezzoDisplay.add(tile2)
        rootService.currentGame!!.intermezzoDisplay.add(tile3)
        rootService.currentGame!!.intermezzoDisplay.add(tile1)

        //this is the move to trigger the intermezzo phase!!!!!!
        rootService.playerService.passMoveIntermezzo()

        //check, if number got increased/is different after a pass move
        assertNotEquals(intermezzoCounterBefore, rootService.currentGame!!.intermezzoMoveCounter)
        assertEquals( 0, rootService.currentGame!!.intermezzoMoveCounter)
        rootService.playerService.passMoveIntermezzo()
        assertEquals(1, rootService.currentGame!!.intermezzoMoveCounter)
        //see, if the intermezzo-display gets cleared
        rootService.playerService.passMoveIntermezzo()
        assertEquals(-1, rootService.currentGame!!.intermezzoMoveCounter)
        assertEquals(0, rootService.currentGame!!.intermezzoDisplay.size)

        //set to 1 so that at the next calling of the function it becomes 2 (== players.size)
        /*rootService.currentGame!!.intermezzoMoveCounter = 1



        //see, if intermezzoMoveCounter got reset
        assertEquals(-1, rootService.currentGame!!.intermezzoMoveCounter)

         */
    }

 */

    /**
     * Checks for various functions, if an exception gets thrown if there is no currentGame.
     */
    @Test
    fun noOngoingGameTest() {
        val rootService = RootService()

        val exception1: Throwable = assertThrows<IllegalStateException>

        { rootService.playerService.makeMove(tile1, 0, 0, 0, "") }
        println("makeMove")

        assertEquals("There is no ongoing game!", exception1.message)



        val exception2: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.checkPlaceIsValid(0, 0) }
        println("checkPlacedTiles")

        assertEquals("There is no ongoing game!", exception2.message)

        val exception3: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.checkAndMoveDiscs() }
        println("checkAndMoveDiscs")

        assertEquals("There is no ongoing game!", exception3.message)

        val exception4: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.placeSoundDiscs(tile2) }
        println("placeSoundDiscs")

        assertEquals("There is no ongoing game!", exception4.message)

        val exception5: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.changeSimSpeed(SimulationSpeed.SLOW) }
        println("changeSimSpeed")

        assertEquals("There is no ongoing game!", exception5.message)

        val exception6: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.undo() }
        println("undo")

        assertEquals("There is no ongoing game!", exception6.message)

        val exception7: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.redo() }
        println("redo")

        assertEquals("There is no ongoing game!", exception7.message)

        val exception8: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.passMoveIntermezzo()}
        println("PassMoveIntermezzo")
        assertEquals("There is no ongoing game!", exception8.message)

        val exception9: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.endTurn("") }
        println("endTurn")

        assertEquals("There is no ongoing game!", exception9.message)

        val exception10: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.crossedThresholdFirst(0, 0) }
        println("crossedThresholdFirst")

        assertEquals("There is no ongoing game!", exception10.message)

        val exception11: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.checkAndSetStartedIntermezzo() }
        println("checkAndSetStartIntermezzo")

        assertEquals("There is no ongoing game!", exception11.message)

        val exception12: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.checkAndSetStartedLastRound(false) }
        println("checkAndSetStartedLasRound")

        assertEquals("There is no ongoing game!", exception12.message)

        val exception13: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.nextPlayerIndex() }
        println("nextPlayerIndex")

        assertEquals("There is no ongoing game!", exception13.message)
    }


    /**
     * Tests the throwing of exceptions at the special cases of undo() and redo().
     */
    @Test
    fun undoRedoSpecialCases() {
        val rootService = RootService()

        rootService.currentGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(player1, player2), SimulationSpeed.NO_SIM
        )

        val exception1: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.undo() }

        assertEquals("This is the first move!", exception1.message)

        val exception2: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.redo() }

        assertEquals("This is the most recent move!", exception2.message)
    }

    /**
     * test to check if crossedThresholdFirst works properly
     */
    @Test
    fun crossedThresholdFirstTest() {
        val rootService = RootService()
        val testTiles = TestTiles()
        testTiles.createTiles()

        //game with 2 players
        rootService.currentGame = Sagani(
            0,
            0, -1, null, null,
            testTiles.listOfTiles1.subList(0, 5), testTiles.listOfTiles1.subList(5, 10), mutableListOf(),
            mutableListOf(testTiles.player1, testTiles.player2), SimulationSpeed.NO_SIM
        )
        val game = rootService.currentGame
        val player = game!!.players[game.playerIndex]

        // scenario 1: player1 crossed the 2-player-threshold first
        val tile = testTiles.listOfTiles2[0].clone()
        tile.points = 76
        tile.arrows[0].occupied = true
        player.placedTiles.add(tile)
        val newPoints = rootService.gameService.calculatePoints(player)
        val oldPoints = player.points
        assertEquals(true, rootService.playerService.crossedThresholdFirst(newPoints, oldPoints))

        // scenario 2: there is already a player over the threshold
        game.players[1].points = 80
        assertEquals(false, rootService.playerService.crossedThresholdFirst(newPoints, oldPoints))

        // scenario 3: same as 1 but with 3 players
        val rootService2 = RootService()
        val player3 = player1.clone()
        rootService2.currentGame = Sagani(
            0,
            0, -1, null, null,
            testTiles.listOfTiles1.subList(0, 5), testTiles.listOfTiles1.subList(5, 10), mutableListOf(),
            mutableListOf(testTiles.player1, testTiles.player2, player3), SimulationSpeed.NO_SIM
        )
        val game2 = rootService2.currentGame
        val playerNew = game2!!.players[game.playerIndex]
        playerNew.placedTiles.add(tile)
        game2.players[1].points = 30
        val pointsNew = rootService2.gameService.calculatePoints(playerNew)
        val pointsOld = playerNew.points
        assertEquals(true, rootService.playerService.crossedThresholdFirst(pointsNew, pointsOld))

        // scenario 4: same as 1 but with 3 players
        game2.players[1].points = 61
        game2.players[2].points = 60
        val booleanRes = rootService2.playerService.crossedThresholdFirst(pointsNew, pointsOld)
        assertEquals(false, booleanRes)
    }

    /**
     * Tests the correct operation of checkAndSetStartedIntermezzo
     */
    /*@Test
    fun checkAndSetStartedIntermezzoTest() {
        val rootService = RootService()
        val testTiles = TestTiles()
        testTiles.createTiles()
        val player3 = testTiles.player1.clone()

        rootService.currentGame = Sagani(
            0,
            0, -1, null, null,
            testTiles.listOfTiles1.subList(0, 5), testTiles.listOfTiles1.subList(5, 10), mutableListOf(),
            mutableListOf(testTiles.player1, testTiles.player2, player3), SimulationSpeed.NO_SIM
        )
        val game = rootService.currentGame
        checkNotNull(game)

        // default situation should not change after tested method is called
        rootService.playerService.checkAndSetStartedIntermezzo()
        assertEquals(false, game.startedIntermezzo)
        assertEquals(-1, game.intermezzoMoveCounter)

        // add tiles to intermezzoDisplay to check the execution of the first if-condition
        game.intermezzoDisplay.addAll(testTiles.listOfTiles1.subList(0, 4))
        rootService.playerService.checkAndSetStartedIntermezzo()
        assertEquals(true, game.startedIntermezzo)
        assertEquals(0, game.intermezzoMoveCounter)

        // state of the attributes after another call of checkAndSetStartedIntermezzo
        rootService.playerService.checkAndSetStartedIntermezzo()
        assertEquals(false, game.startedIntermezzo)
        assertEquals(0, game.intermezzoMoveCounter)
    }

     */

    @Test
    fun checkAndSetStartedLastRoundTest() {
        val rootService = RootService()
        val testTiles = TestTiles()
        testTiles.createTiles()
        val player3 = testTiles.player1.clone()

        rootService.currentGame = Sagani(
            0,
            0, -1, null, null,
            testTiles.listOfTiles1.subList(0, 5), testTiles.listOfTiles1.subList(5, 10), mutableListOf(),
            mutableListOf(testTiles.player1, testTiles.player2, player3), SimulationSpeed.NO_SIM
        )
        val game = rootService.currentGame
        checkNotNull(game)

        // test if condition with different variants of the boolean chains
        // firstOverThreshold true, everything else false
        rootService.playerService.checkAndSetStartedLastRound(true)
        assertEquals(true, game.lastRound)
        assertEquals(true, game.startedLastRound)

        // firstOverThreshold false, both else true
        game.offerDisplay = mutableListOf()
        rootService.playerService.checkAndSetStartedLastRound(false)
        assertEquals(true, game.lastRound)
        assertEquals(true, game.lastRound)

        // firstOverThreshold false, only offerDisplay empty
        game.lastRound = false
        game.drawStack.removeLast()
        rootService.playerService.checkAndSetStartedLastRound(false)
        assertEquals(4, game.drawStack.size)
        assertEquals(false, game.lastRound)
        assertEquals(false, game.startedLastRound)

        // firstOverThreshold false, drawStack filled, offerDisplay not empty
        game.offerDisplay.add(testTiles.listOfTiles1[0])
        game.drawStack.add(testTiles.listOfTiles1[1])
        rootService.playerService.checkAndSetStartedLastRound(false)
        assertEquals(1, game.offerDisplay.size)
        assertEquals(5, game.drawStack.size)
        assertEquals(false, game.lastRound)
        assertEquals(false, game.startedLastRound)
    }
/*
    /**
     * Checks if after a move is finished the turn is properly ended and the game prepared for the next player.
     */
    @Test
    fun endTurnTest() {
        val rootService = RootService()
        val testTiles = TestTiles()
        testTiles.createTiles()
        testTiles.player1.playerType = PlayerType.HUMAN
        testTiles.player2.playerType = PlayerType.HUMAN


        //game with 2 players
        rootService.currentGame = Sagani(
            0,
            0, -1, null, null,
            testTiles.listOfTiles1.subList(0, 5), testTiles.listOfTiles1.subList(5, 10), mutableListOf(),
            mutableListOf(testTiles.player1, testTiles.player2), SimulationSpeed.NO_SIM
        )


        val game = rootService.currentGame
        checkNotNull(game)
        game.players[0].placedTiles = testTiles.listOfTiles1.subList(0,5)
        println(testTiles)
        game.players[1].placedTiles = testTiles.listOfTiles1.subList(5,9)

        rootService.playerService.endTurn("")

        assertEquals(1 ,rootService.currentGame!!.playerIndex)
/*
        // "advance" the game further and manually add tiles to one player to test if player objects are copied
        // correctly

        rootService.currentGame!!.players[0].placedTiles.add(0, testTiles.listOfTiles2[4])

        rootService.playerService.endTurn("")

        rootService.currentGame!!.players[0].placedTiles.add(0, testTiles.listOfTiles2[5])
        rootService.playerService.endTurn("")

        assertEquals(6, rootService.currentGame!!.previousState!!.previousState!!.players[0].placedTiles.size)
        assertEquals(5,
            rootService.currentGame!!.previousState!!.previousState!!.previousState!!.players[0].placedTiles.size)
        assertEquals(9, rootService.currentGame!!.previousState!!.previousState!!.players[0].placedTiles[0].id)
        assertEquals(10, rootService.currentGame!!.previousState!!.players[0].placedTiles[0].id)

        //game with 5 players
        rootService.currentGame = Sagani(
            0,
            0, 0, null, null,
            mutableListOf(tile1), mutableListOf(), mutableListOf(),
            mutableListOf(player1, player2, player2, player1, player2), SimulationSpeed.NO_SIM
        )

        //check if exception gets thrown (for test coverage)
        val exception: Throwable = assertThrows<IllegalStateException>
        { rootService.playerService.endTurn("") }

        assertEquals("Incorrect player number. Must be two to four.", exception.message)
         */
    }

 */



}