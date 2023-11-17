package service

import entity.*
import kotlin.test.Test
import kotlin.test.assertEquals

/** [CriteriaObjectForAIServiceTest] test [CriteriaObjectForAIService] */
class CriteriaObjectForAIServiceTest {

    private val rootService = RootService()

    private var testTile = mutableListOf<Tile>(
        Tile(
            0, 10, 0, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(false, TileArrow.BOTTOM, TileSpirit.EARTH)),
            TileSpirit.FIRE),
        Tile(
            0, 10, 1, 0, 0,
            mutableListOf(DiscColor.BLACK),
            mutableListOf(Arrow(true, TileArrow.BOTTOM, TileSpirit.EARTH)),
            TileSpirit.FIRE
        )
    )

    private var player1 = Player(
        points = 74,
        name = "Bob 1",
        discsLeftCount = 4,
        cacoCount = 0,
        placedTiles = mutableListOf(),
        color = DiscColor.BLACK,
        playerType = PlayerType.RANDOM_AI
    )

    /** [testCountOccupiedArrows] tests if [CriteriaObjectForAIService.countOccupiedArrows] works fine
     */
    @Test
    fun testCountOccupiedArrows(){
        val counted = rootService.aiService.criteriaObjectService.countOccupiedArrows(testTile)
        assertEquals(1, counted)
    }

    /** [testCountTileWithOnlyOneArrowLeft] tests if [CriteriaObjectForAIService.countTileWithOnlyOneArrowLeft]
     * works fine */
    @Test
    fun testCountTileWithOnlyOneArrowLeft(){
        val counted = rootService.aiService.criteriaObjectService.countTileWithOnlyOneArrowLeft(testTile)
        assertEquals(1, counted)
    }

    /** [testCopyPlayer] tests if [CriteriaObjectForAIService.copyPlayer] works fine
     */
    @Test
    fun testCopyPlayer(){

        val copiedPlayer = rootService.aiService.criteriaObjectService.copyPlayer(player1)
        assertEquals(copiedPlayer.points, player1.points)
        assertEquals(copiedPlayer.name, player1.name)
        assertEquals(copiedPlayer.discsLeftCount, player1.discsLeftCount)
        assertEquals(copiedPlayer.cacoCount, player1.cacoCount)
        assertEquals(copiedPlayer.placedTiles, player1.placedTiles)
        assertEquals(copiedPlayer.color, player1.color)
        assertEquals(copiedPlayer, player1)
    }

    /** [testPlaceSoundDiscs] tests if placing sound discs work fine (increase of cacoCount and decrease/increase of
     * discsLeft */
    @Test
    fun testPlaceSoundDiscs(){
        player1.discsLeftCount = 0
        testTile[0].soundDiscs = mutableListOf()
        testTile[0].arrows[0].occupied = false
        rootService.aiService.criteriaObjectService.placeSoundDiscs(player1, testTile[0])
        assertEquals(1, player1.cacoCount)

        player1.discsLeftCount = 3
        player1.cacoCount = 0
        testTile[1].soundDiscs = mutableListOf()
        testTile[1].arrows[0].occupied = false
        rootService.aiService.criteriaObjectService.placeSoundDiscs(player1, testTile[1])
        assertEquals(2, player1.discsLeftCount)
    }





}