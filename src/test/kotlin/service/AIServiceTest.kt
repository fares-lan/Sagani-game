package service

import entity.*
import kotlin.test.*

/**
 * Class with tests for the class AIService.
 */
class AIServiceTest {

    // Helping variables for tests
    private var listOfTiles1: MutableList<Tile> = mutableListOf()
    private var listOfTiles2: MutableList<Tile> = mutableListOf()
    private var listOfTiles3: MutableList<Tile> = mutableListOf()
    private var player1: Player? = null
    private var player2: Player? = null
    private var emptyGame = Sagani(
        startPlayerMarkerIndex = 0,
        playerIndex = 0,
        intermezzoMoveCounter = 0,
        nextState = null,
        previousState = null,
        offerDisplay = mutableListOf(),
        drawStack = mutableListOf(),
        intermezzoDisplay = mutableListOf(),
        players = mutableListOf(),
        simSpeed = SimulationSpeed.VERY_SLOW
    )

    /**
     * Assigning values to all internal variables of a class for tests
     * (From TestTiles)
     */
    @BeforeTest
    fun initializeStartVariables() {
        println("BeforeTest")

        val testable = TestTiles()

        testable.createTiles1()
        testable.createTiles2()

        listOfTiles1 = testable.listOfTiles1
        listOfTiles2 = testable.listOfTiles2
        listOfTiles3 = testable.listOfTiles3

        player1 = testable.player1
        player2 = testable.player2

        println()
    }

    /**
     * Test for the method allPossibleOptions()
     *
     *                      X :
     *          -3  -2  -1   0   1   2   3   4
     *      3    -   -   -   -   -   -   -   -
     *      2    -   -   -   P   -   -   -   -
     *      1    -   -   P   #   P   -   -   -
     * Y :  0    -   -   P   #   P   -   -   -
     *     -1    -   -   P   #   P   -   -   -
     *     -2    -   -   -   P   -   -   -   -
     *     -3    -   -   -   -   -   -   -   -
     *     -4    -   -   -   -   -   -   -   -
     *
     *      # - Placed tiles
     *      P - Possible free positions
     */
    @Test
    fun testAllPossibleOptions1() {
        println("testAllPossibleOptions1()")

        // Placed tiles
        // Each tile has no arrows for clarity
        val listOfTiles = mutableListOf(
            Tile(0,10, 0,0,0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE
            ),
            Tile(1,10, 0,1,0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE
            ),
            Tile(2,10, 0,-1,0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE
            ))

        // Display
        // Each tile has no arrows for clarity
        val listOfDisplay = mutableListOf(
            Tile(3,6, 0,0,0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE
            ),
            Tile(4,3, 0,0,0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(),
                TileSpirit.FIRE
            ))

        val rootService = RootService()
        val aiService = rootService.aiService
        val lis = aiService.allPossibleOptions(listOfDisplay, listOfTiles)

        println("All possible tiles at all placements:")
        for (i in 0 until lis.size) {
            println(lis[i].toString())
        }

        println("Count of possible options for the move: " + lis.size)
        println()

        // Check borders of the tile with min / max X-position and Y-position
        check(lis.maxByOrNull { it.posX }?.posX == 1)
        check(lis.minByOrNull { it.posX }?.posX == -1)
        check(lis.maxByOrNull { it.posY }?.posY == 2)
        check(lis.minByOrNull { it.posY }?.posY == -2)

        // 2 - display tiles, 8 - placement possibilities, 4 - rotations
        check(lis.size == 2 * 8 * 4)
    }

    /**
     * 1 Test for the method getSortedTilesByCriteria()
     *
     *                      X :
     *          -3  -2  -1   0   1   2   3   4
     *      3    -   -   -   -   -   -   -   -
     *      2    -   -   -   P   -   -   -   -
     *      1    -   P   P   #   P   -   -   -
     * Y :  0    P   #   #   #   #   P   -   -
     *     -1    -   P   #   P   P   -   -   -
     *     -2    -   -   P   -   -   -   -   -
     *     -3    -   -   -   -   -   -   -   -
     *     -4    -   -   -   -   -   -   -   -
     *
     *      # - Placed tiles
     *      P - Possible free positions
     */
    @Test
    fun testGetCriteriaObject1() {
        println("testGetCriteriaObject1()")

        val displayTiles = mutableListOf(
            Tile(0,1, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.FIRE),
            Tile(1,3, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.AIR),
            Tile(2,1, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.WATER),
            Tile(4,10, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.EARTH))

        val player = Player(points = 0, name = "Bob", discsLeftCount = 16, cacoCount = 0,
            placedTiles = listOfTiles2, color = DiscColor.BLACK, playerType = PlayerType.RANDOM_AI)

        val rootServicePOINTS = RootService()
        val rootServiceDISCS = RootService()
        val aiServicePOINTS = rootServicePOINTS.aiService
        val aiServiceDISCS = rootServiceDISCS.aiService
        val allPossibleOptionsPOINTS = aiServicePOINTS.allPossibleOptions(displayTiles, listOfTiles2)
        val allPossibleOptionsDISCS = aiServiceDISCS.allPossibleOptions(displayTiles, listOfTiles2)
        val criteriaObjectPOINTS = aiServicePOINTS.criteriaObjectService
            .getCriteriaObjects(player, allPossibleOptionsPOINTS, "POINTS")
        val criteriaObjectDISCS = aiServiceDISCS.criteriaObjectService
            .getCriteriaObjects(player, allPossibleOptionsDISCS, "DISCS")
        val criteriaObjectARROWS = aiServiceDISCS.criteriaObjectService
            .getCriteriaObjects(player, allPossibleOptionsDISCS, "ARROWS")

        var maxPoints = criteriaObjectPOINTS[0].points
        print(criteriaObjectPOINTS[0])
        for (bundle in criteriaObjectPOINTS) {
            print(bundle.points.toString() + ", " + bundle.leftDiscs)
            println()
            assertTrue(bundle.points <= maxPoints)
            assertNotNull(bundle.tile)
            assertNotNull(bundle.leftDiscs)
            maxPoints = bundle.points
        }

        var maxDiscs = criteriaObjectDISCS[0].leftDiscs
        for (bundle in criteriaObjectPOINTS) {
            print(bundle.leftDiscs.toString() + ", " + bundle.points)
            println()
            assertTrue(bundle.leftDiscs <= maxDiscs)
            assertNotNull(bundle.tile)
            assertNotNull(bundle.points)
            maxDiscs = bundle.leftDiscs
        }

        var maxArrows = criteriaObjectARROWS[0].occupiedArrows
        print(criteriaObjectARROWS[0])
        for (bundle in criteriaObjectARROWS) {
            print(bundle.points.toString() + ", " + bundle.leftDiscs)
            println()
            assertTrue(bundle.occupiedArrows <= maxArrows)
            assertNotNull(bundle.tile)
            maxArrows = bundle.occupiedArrows
        }
    }

    /**
     * 2 Test for the method testGetCriteriaObject()
     */
    @Test
    fun testGetCriteriaObject2() {
        println("testGetCriteriaObject2()")

        val displayTiles = mutableListOf(
            Tile(0,1, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.FIRE),
            Tile(1,3, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.AIR),
            Tile(2,1, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.WATER),
            Tile(4,10, 0,0,0, mutableListOf(), mutableListOf(Arrow(false,
                TileArrow.TOP, TileSpirit.FIRE)), TileSpirit.EARTH))

        val player = Player(points = 0, name = "Bob", discsLeftCount = 0, cacoCount = 0,
            placedTiles = listOfTiles2, color = DiscColor.BLACK, playerType = PlayerType.RANDOM_AI)

        val rootServicePOINTS = RootService()
        val rootServiceDISCS = RootService()
        val aiServicePOINTS = rootServicePOINTS.aiService
        val aiServiceDISCS = rootServiceDISCS.aiService
        val allPossibleOptionsPOINTS = aiServicePOINTS.allPossibleOptions(displayTiles, listOfTiles2)
        val allPossibleOptionsDISCS = aiServiceDISCS.allPossibleOptions(displayTiles, listOfTiles2)
        val criteriaObjectPOINTS = aiServicePOINTS.criteriaObjectService
            .getCriteriaObjects(player, allPossibleOptionsPOINTS, "POINTS")
        val criteriaObjectDISCS = aiServiceDISCS.criteriaObjectService
            .getCriteriaObjects(player, allPossibleOptionsDISCS, "DISCS")

        var maxPoints = criteriaObjectPOINTS[0].points
        print(criteriaObjectPOINTS[0])
        for (bundle in criteriaObjectPOINTS) {
            print(bundle.points.toString() + ", " + bundle.leftDiscs)
            println()
            assertTrue(bundle.points <= maxPoints)
            assertNotNull(bundle.tile)
            assertNotNull(bundle.leftDiscs)
            maxPoints = bundle.points
        }

        var maxDiscs = criteriaObjectDISCS[0].leftDiscs
        for (bundle in criteriaObjectPOINTS) {
            print(bundle.leftDiscs.toString() + ", " + bundle.points)
            println()
            assertTrue(bundle.leftDiscs <= maxDiscs)
            assertNotNull(bundle.tile)
            assertNotNull(bundle.points)
            maxDiscs = bundle.leftDiscs
        }
    }

    /**
     * 3 Test for the method testGetCriteriaObject()
     */
    @Test
    fun testGetCriteriaObject3() {
        val rootService = RootService()
        val aiService = rootService.aiService

        assertFailsWith<IllegalStateException>
            { aiService.criteriaObjectService.getCriteriaObjects(player1!!, mutableListOf(), "") }
    }

    /**
     * 1 Test for the method allPossibleOptions()
    */
    @Test
    fun testGetFreePositions1() {
        println("testGetFreePositionsCase1()")

        val rootService = RootService()
        val freePositions = rootService.aiService.getFreePositions(mutableListOf())

        println("Free Positions : $freePositions")
        println()

        // No tiles placed -> Only 1 possible free position - (0, 0)
        check(freePositions.size == 1)
        check(freePositions[0] == Pair(0, 0))

        println()
    }

    /**
     * 3 Test for the method getFreePositions()
     *
     *                      X :
     *          -3  -2  -1   0   1   2   3   4  5
     *      3    -   -   -   -   -   -   -   -  -
     *      2    -   -   -   P   P   P   P   P  -
     *      1    -   -   P   #   #   #   #   #  P
     * Y :  0    -   -   P   #   #   #   #   #  P
     *     -1    -   -   -   P   P   P   P   P  -
     *     -2    -   -   -   -   -   -   -   -  -
     *     -3    -   -   -   -   -   -   -   -  -
     *     -4    -   -   -   -   -   -   -   -  -
     *
     *      # - Placed tiles
     *      P - Possible free positions
     */
    @Test
    fun testGetFreePositions3() {
        println("testGetFreePositions3()")

        val rootService = RootService()
        val freePositions = rootService.aiService.getFreePositions(listOfTiles1)

        println("Free Positions : $freePositions")
        println()

        // Check borders of the tile with min / max X-position and Y-position
        check(freePositions.maxByOrNull { it.first }?.first == 5)
        check(freePositions.minByOrNull { it.first }?.first == -1)
        check(freePositions.maxByOrNull { it.second }?.second == 2)
        check(freePositions.minByOrNull { it.second }?.second == -1)

        // 2 - display tiles, 8 - placement possibilities, 4 - rotations
        check(freePositions.size == 14)
    }

    /**
     * 2 Test for the method allPossibleOptions()
     */
    @Test
    fun testAllPossibleOptions2() {
        println("testAllPossibleOptions2()")

        val rootService = RootService()

        val displayTile = Tile(0,1, 0,0,0,
                                mutableListOf(),
                                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                                TileSpirit.FIRE)

        val possibleOptions1 = rootService.aiService.allPossibleOptions(mutableListOf(displayTile), mutableListOf())

        // 1 display tile and 0 placed tiles -> 4 possible options
        check(possibleOptions1.size == 4)
        // 0 tiles in display -> 0 possible options
        assertFailsWith<IllegalStateException> { rootService.aiService.allPossibleOptions(mutableListOf(), mutableListOf()) }

        println()
    }

    /*
    /**
     * General test 1
     * Random move
     */
    @Test
    fun testGeneral1() {
        println("testGeneral1()")

        val rootService = RootService()
        val aiService = rootService.aiService

        player2!!.placedTiles = listOfTiles1
        player1!!.playerType = PlayerType.HUMAN

        // Game rules are also omitted for clarity
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            nextState = null,
            previousState = null,
            offerDisplay = listOfTiles1,
            drawStack = listOfTiles1,
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(player2!!, player1!!),
            simSpeed = SimulationSpeed.VERY_SLOW,
            intermezzoOrder = mutableListOf(0, 1)
        )

        aiService.makeRandomMove()
        println()
    }


    /**
     * General test 2
     * (Smart move -> Random move)
     */
    @Test
    fun testGeneral2() {
        println("testGeneral2()")

        val rootService = RootService()
        val aiService = rootService.aiService

        player2!!.placedTiles = listOfTiles1
        player1!!.playerType = PlayerType.HUMAN

        // Game rules are also omitted for clarity
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            nextState = null,
            previousState = null,
            offerDisplay = listOfTiles1,
            drawStack = listOfTiles1,
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(player2!!, player1!!),
            simSpeed = SimulationSpeed.VERY_SLOW,
            intermezzoOrder = mutableListOf(0, 1)
        )

        aiService.makeSmartMove()
        println()
    }

    /**
     * General test 3
     */
    @Test
    fun testGeneral3() {
        println("testGeneral3()")

        player2!!.placedTiles = listOfTiles2

        val rootService = RootService()
        val aiService = rootService.aiService

        // Game rules are also omitted for clarity
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            nextState = null,
            previousState = null,
            offerDisplay = listOfTiles1,
            drawStack = listOfTiles1,
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(),
            simSpeed = SimulationSpeed.VERY_SLOW,
            intermezzoOrder = mutableListOf(0, 1)
        )

        player1!!.placedTiles = listOfTiles2
        player1!!.playerType = PlayerType.RANDOM_AI
        player2!!.playerType = PlayerType.HUMAN

        rootService.currentGame!!.players = mutableListOf(player1!!, player2!!)

        aiService.moveByAI()

        println()
    }

     */

    /**
     * General test 4
     */
    /*@Test
    fun testGeneral4() {
        println("testCheckForOpponent4()")

        player1!!.placedTiles = listOfTiles2
        player1!!.points = 73
        player1!!.discsLeftCount = 20
        player2!!.placedTiles = listOfTiles2
        player2!!.playerType = PlayerType.HUMAN
        player2!!.points = 74


        val rootService = RootService()
        val aiService = rootService.aiService

        // Game rules are also omitted for clarity
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = 1,
            nextState = null,
            previousState = null,
            offerDisplay = listOfTiles1,
            drawStack = listOfTiles1,
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(player1!!, player2!!),
            simSpeed = SimulationSpeed.VERY_SLOW,
            intermezzoOrder = mutableListOf(1, 0)
        )

        aiService.makeSmartMove()

        println()
    }

     */

    /**
     * General test 5
     */
    /*
    @Test
    fun testGeneral5() {
        println("testGeneral5()")

        val rootService = RootService()
        val aiService = rootService.aiService

        player1!!.placedTiles = listOfTiles1
        player2!!.placedTiles = listOfTiles1
        player2!!.points = 20

        // Game rules are also omitted for clarity
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = 0,
            nextState = null,
            previousState = null,
            offerDisplay = listOfTiles1,
            drawStack = listOfTiles1,
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(player2!!, player1!!),
            simSpeed = SimulationSpeed.VERY_SLOW,
            intermezzoOrder = mutableListOf(1,0)
        )

        player2!!.playerType = PlayerType.HUMAN

        aiService.makeRandomMove()
        println()
    }

     */

    /*
    /**
     * General test 6
     */
    @Test
    fun testGeneral6() {
        println("testGeneral6()")

        player2!!.placedTiles = listOfTiles2

        val rootService = RootService()
        val aiService = rootService.aiService

        // Game rules are also omitted for clarity
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            nextState = null,
            previousState = null,
            offerDisplay = listOfTiles1,
            drawStack = listOfTiles1,
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(),
            simSpeed = SimulationSpeed.VERY_SLOW,
            intermezzoOrder = mutableListOf(0, 1)
        )

        player1!!.placedTiles = listOfTiles2
        player1!!.playerType = PlayerType.SMART_AI
        player2!!.playerType = PlayerType.HUMAN

        rootService.currentGame!!.players = mutableListOf(player1!!, player2!!)

        aiService.moveByAI()
    }

     */
/*
    /**
     * General test 7
     */
    @Test
    fun testGeneral7() {
        println("testGeneral7()")

        player2!!.placedTiles = listOfTiles2

        val rootService = RootService()
        val aiService = rootService.aiService

        // Game rules are also omitted for clarity
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            nextState = null,
            previousState = null,
            offerDisplay = listOfTiles1,
            drawStack = listOfTiles1,
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(),
            simSpeed = SimulationSpeed.VERY_SLOW,
            intermezzoOrder = mutableListOf(0, 1)
        )

        player2!!.playerType = PlayerType.HUMAN

        rootService.currentGame!!.players = mutableListOf(player1!!, player2!!)

        rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].playerType = PlayerType.HUMAN
        assertFailsWith<IllegalStateException> { aiService.moveByAI() }

        rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].playerType = PlayerType.RANDOM_AI

        rootService.currentGame = null
        assertFailsWith<IllegalStateException> { aiService.moveByAI() }
        println()
    }

 */

/*
    /**
     * Test for the method countTileWithOnlyOneArrowLeft()
     */
    @Test
    fun testCountTileWithOnlyOneArrowLeft() {
        println("testCountTileWithOnlyOneArrowLeft()")

        val rootService = RootService()
        val testable = TestTiles()
        val tiles = testable.justOneArrowNotOccupiedTile

        player1!!.placedTiles = tiles
        val count = rootService.aiService.countTileWithOnlyOneArrowLeft(player1!!)

        assertEquals(count ,1)

        println()
    }

 */

    /**
     * 1 Test for the method checkForOpponent()
     */
    @Test
    fun testCheckForOpponent1() {
        println("testCheckForOpponent1()")

        val rootService = RootService()
        val aiService = rootService.aiService

        rootService.currentGame = emptyGame

        emptyGame.players = mutableListOf(player1!!, player2!!)

        player1!!.points = 16
        player2!!.points = 0

        val result = aiService.checkForOpponent(player1!!)

        check(result.first == player1)
        check(result.first.points - player1!!.points == 0)
        check(!result.second)

        println()
    }

    /**
     * 2 Test for the method checkForOpponent()
     */
    @Test
    fun testCheckForOpponent2() {
        println("testCheckForOpponent2()")

        val rootService = RootService()
        val aiService = rootService.aiService

        rootService.currentGame = emptyGame

        emptyGame.players = mutableListOf(player1!!, player2!!)

        player1!!.points = 0
        player2!!.points = 16

        val result = aiService.checkForOpponent(player1!!)

        check(result.first != player1)
        check(result.first.points - player1!!.points > 15)
        check(result.second)

        println()
    }

    /**
     * Test for the method checkForQuickWin()
     */
    @Test
    fun testCheckForQuickWin() {
        println("testCheckForQuickWin()")

        val rootService = RootService()
        val aiService = rootService.aiService

        rootService.currentGame = emptyGame

        val player3 = Player(points = 0,
                            name = "Bob",
                            discsLeftCount = 0,
                            cacoCount = 0,
                            placedTiles = mutableListOf(),
                            color = DiscColor.BLACK,
                            playerType = PlayerType.RANDOM_AI)
        val player4 = Player(points = 0,
                            name = "Bob",
                            discsLeftCount = 0,
                            cacoCount = 0,
                            placedTiles = mutableListOf(),
                            color = DiscColor.BLACK,
                            playerType = PlayerType.RANDOM_AI)

        emptyGame.players = mutableListOf(player1!!, player2!!)
        player1!!.points = 70
        check(aiService.checkForQuickWin(player1!!))
        emptyGame.players = mutableListOf(player1!!, player2!!, player3)
        player1!!.points = 55
        check(aiService.checkForQuickWin(player1!!))
        emptyGame.players = mutableListOf(player1!!, player2!!, player3, player4)
        player1!!.points = 40
        check(aiService.checkForQuickWin(player1!!))
        emptyGame.players = mutableListOf(player1!!, player2!!, player3, player4)
        player1!!.points = 10
        check(!aiService.checkForQuickWin(player1!!))

        println()
    }

    /**
     * 1 Test for the method makeBitchyOpponentMove()
     */
    @Test
    fun testMakeBitchyOpponentMove1() {
        println("testMakeBitchyOpponentMove1()")

        val rootService = RootService()
        val aiService = rootService.aiService

        rootService.currentGame = emptyGame
        rootService.currentGame!!.offerDisplay = listOfTiles2

        val player3 = Player(points = 0,
            name = "Bob",
            discsLeftCount = 0,
            cacoCount = 0,
            placedTiles = listOfTiles1,
            color = DiscColor.BLACK,
            playerType = PlayerType.RANDOM_AI)

        player1!!.playerType = PlayerType.HUMAN
        rootService.currentGame!!.players = mutableListOf(player3, player1!!)

        aiService.makeBitchyOpponentMove(player3)

        println()
    }

    /**
     * 2 Test for the method makeBitchyOpponentMove()
     */
    @Test
    fun testMakeBitchyOpponentMove2() {
        val rootService = RootService()
        val aiService = rootService.aiService

        rootService.currentGame = emptyGame
        rootService.currentGame!!.offerDisplay = listOfTiles2

        val player3 = Player(points = 0,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = listOfTiles1,
            color = DiscColor.BLACK,
            playerType = PlayerType.RANDOM_AI)

        player1!!.playerType = PlayerType.HUMAN
        rootService.currentGame!!.players = mutableListOf(player3, player1!!)

        aiService.makeBitchyOpponentMove(player3)
    }
/*
    /**
     * 1 new test to test the modified tile selection for the smart move coverage
     * (Intermezzo phase)
     */
    @Test
    fun testNewSmartMove1() {
        // Root service and AI service
        val rootService = RootService()
        val aiService = rootService.aiService
        for (i in 1..4) {
        // New player for the test (Smart AI)
            val testPlayer1 = Player(
                points = 0,
                name = "John",
                discsLeftCount = 16,
                cacoCount = 0,
                placedTiles = mutableListOf(),
                color = DiscColor.BLACK,
                playerType = PlayerType.SMART_AI)
            val testPlayer2 = Player(
                points = 60,
                name = "John",
                discsLeftCount = 16,
                cacoCount = 0,
                placedTiles = mutableListOf(),
                color = DiscColor.BLACK,
                playerType = PlayerType.HUMAN)
            val testTiles = mutableListOf<Tile>()
            for (j in 1..4) {
                val testTile = Tile (
                    j, 10, 4, 1, 0,
                    mutableListOf(DiscColor.BLACK), mutableListOf(),
                    TileSpirit.FIRE)
                testTiles.add(testTile)
            }
            rootService.currentGame = Sagani(
                startPlayerMarkerIndex = 0,
                playerIndex = 0,
                intermezzoMoveCounter = 0,
                offerDisplay = mutableListOf(),
                drawStack = mutableListOf(),
                intermezzoDisplay = listOfTiles1.subList(0, 1),
                players = mutableListOf(testPlayer1, testPlayer2),
                simSpeed = SimulationSpeed.VERY_SLOW)

            when(i) {
                1 -> {
                    rootService.currentGame!!.offerDisplay = testTiles.subList(0, 2)
                    rootService.currentGame!!.drawStack = mutableListOf() }
                2 -> {
                    rootService.currentGame!!.offerDisplay = mutableListOf(testTiles.get(0))
                    rootService.currentGame!!.drawStack = mutableListOf() }
                3 -> {
                    rootService.currentGame!!.offerDisplay = testTiles.subList(0, 2)
                    rootService.currentGame!!.drawStack = testTiles.subList(2, 4) }
                4 -> {
                    rootService.currentGame!!.offerDisplay = mutableListOf(testTiles.get(0))
                    rootService.currentGame!!.drawStack = testTiles.subList(1, 3) }
            }
            aiService.makeSmartMove()
        }
    }



    /**
     * 2 new test to test the modified tile selection for the smart move coverage
     * (Not intermezzo phase)
     */
    @Test
    fun testNewSmartMove2() {
        // Root service and AI service
        val rootService = RootService()
        val aiService = rootService.aiService
        val testPlayer1 = Player(
            points = 74,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(Tile (
                0, 10, 0, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.FIRE)),
            color = DiscColor.BLACK,
            playerType = PlayerType.SMART_AI)
        val testPlayer2 = Player(
            points = 0,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(),
            color = DiscColor.BLACK,
            playerType = PlayerType.HUMAN)
        val testTiles = mutableListOf<Tile>()
        testTiles.add(Tile (
            1, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH)),
            TileSpirit.EARTH))
        testTiles.add(Tile (
            2, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH)),
            TileSpirit.WATER))
        testTiles.add(Tile (
            3, 3, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
            TileSpirit.FIRE))
        testTiles.add(Tile (
            4, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE),
                Arrow(false, TileArrow.BOTTOM, TileSpirit.WATER)),
            TileSpirit.FIRE))

        // New game
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            offerDisplay = mutableListOf(),
            drawStack = mutableListOf(),
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(testPlayer1, testPlayer2),
            simSpeed = SimulationSpeed.VERY_SLOW
        )
        rootService.currentGame!!.offerDisplay = mutableListOf(testTiles.get(0), testTiles.get(1))
        rootService.currentGame!!.drawStack = mutableListOf(testTiles.get(2), testTiles.get(3))
        aiService.makeSmartMove()
    }

 */

    /**
     * 3 new test to test the modified tile selection for the smart move coverage
     * (Not intermezzo phase)
     */
    @Test
    fun testNewSmartMove3() {
        // Root service and AI service
        val rootService = RootService()
        val aiService = rootService.aiService
        // New player for the test (Smart AI)
        val testPlayer1 = Player(
            points = 74,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(Tile (
                0, 10, 0, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.FIRE)),
            color = DiscColor.BLACK,
            playerType = PlayerType.SMART_AI)
        val testPlayer2 = Player(
            points = 0,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(),
            color = DiscColor.BLACK,
            playerType = PlayerType.HUMAN)
        val testTiles = mutableListOf<Tile>()
        testTiles.add(Tile (
            1, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH),
                Arrow(false, TileArrow.BOTTOM, TileSpirit.EARTH),
                Arrow(false, TileArrow.BOTTOMLEFT, TileSpirit.EARTH)),
            TileSpirit.EARTH))
        testTiles.add(Tile (
            2, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH)),
            TileSpirit.FIRE))
        testTiles.add(Tile (
            3, 3, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
            TileSpirit.FIRE))
        // New game
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            offerDisplay = mutableListOf(),
            drawStack = mutableListOf(),
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(testPlayer1, testPlayer2),
            simSpeed = SimulationSpeed.VERY_SLOW
        )
        rootService.currentGame!!.offerDisplay = mutableListOf(testTiles.get(0))
        rootService.currentGame!!.drawStack = mutableListOf(testTiles.get(1), testTiles.get(2))
        aiService.makeSmartMove()
    }

    /**
     * 1 new test to test the modified tile selection for the random move coverage
     * (Not intermezzo phase)
     */
    @Test
    fun testNewRandomMove2() {
        // Root service and AI service
        val rootService = RootService()
        val aiService = rootService.aiService

        // New player for the test (Smart AI)
        val testPlayer1 = Player(
            points = 74,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(Tile (
                0, 10, 0, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.FIRE)),
            color = DiscColor.BLACK,
            playerType = PlayerType.RANDOM_AI)

        val testPlayer2 = Player(
            points = 0,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(),
            color = DiscColor.BLACK,
            playerType = PlayerType.HUMAN)

        val testTiles = mutableListOf<Tile>()
        testTiles.add(Tile (
            1, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH)),
            TileSpirit.EARTH))
        testTiles.add(Tile (
            2, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH)),
            TileSpirit.WATER))
        testTiles.add(Tile (
            3, 3, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
            TileSpirit.FIRE))
        testTiles.add(Tile (
            4, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE),
                Arrow(false, TileArrow.BOTTOM, TileSpirit.WATER)),
            TileSpirit.FIRE))

        // New game
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = -1,
            offerDisplay = mutableListOf(),
            drawStack = mutableListOf(),
            intermezzoDisplay = mutableListOf(),
            players = mutableListOf(testPlayer1, testPlayer2),
            simSpeed = SimulationSpeed.VERY_SLOW
        )

        rootService.currentGame!!.offerDisplay = mutableListOf(testTiles.get(0))
        rootService.currentGame!!.drawStack = mutableListOf(testTiles.get(2), testTiles.get(3))

        // Smart move
        aiService.makeRandomMove()
    }
/*
    /**
     * 2 new test to test the modified tile selection for the random move coverage
     * (Not intermezzo phase)
     */
    @Test
    fun testNewRandomMove1() {
        // Root service and AI service
        val rootService = RootService()
        val aiService = rootService.aiService

        // New player for the test (Smart AI)
        val testPlayer1 = Player(
            points = 74,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(Tile (
                0, 10, 0, 0, 0,
                mutableListOf(DiscColor.BLACK),
                mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
                TileSpirit.FIRE)),
            color = DiscColor.BLACK,
            playerType = PlayerType.RANDOM_AI)

        val testPlayer2 = Player(
            points = 0,
            name = "John",
            discsLeftCount = 16,
            cacoCount = 0,
            placedTiles = mutableListOf(),
            color = DiscColor.BLACK,
            playerType = PlayerType.HUMAN)

        val testTiles = mutableListOf<Tile>()
        testTiles.add(Tile (
            1, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH)),
            TileSpirit.EARTH))
        testTiles.add(Tile (
            2, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.EARTH)),
            TileSpirit.WATER))
        testTiles.add(Tile (
            3, 3, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE)),
            TileSpirit.FIRE))
        testTiles.add(Tile (
            4, 1, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.TOP, TileSpirit.FIRE),
                Arrow(false, TileArrow.BOTTOM, TileSpirit.WATER)),
            TileSpirit.FIRE))

        // New game
        rootService.currentGame = Sagani(
            startPlayerMarkerIndex = 0,
            playerIndex = 0,
            intermezzoMoveCounter = 0,
            offerDisplay = mutableListOf(),
            drawStack = mutableListOf(),
            intermezzoDisplay = mutableListOf(testTiles.get(1)),
            players = mutableListOf(testPlayer1, testPlayer2),
            simSpeed = SimulationSpeed.VERY_SLOW
        )

        rootService.currentGame!!.offerDisplay = mutableListOf(testTiles.get(0))
        rootService.currentGame!!.drawStack = mutableListOf(testTiles.get(2), testTiles.get(3))

        // Smart move
        aiService.makeRandomMove()
    }

 */
}