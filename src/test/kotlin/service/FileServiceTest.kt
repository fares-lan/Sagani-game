package service

import entity.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.Files

/**
 * Contains the tests for all public methods provided by the FileService class.
 * All related to reading or writing external files.
 */
class FileServiceTest {

    /**
     * Functionality test if a game is correctly saved, stored and restored.
     */
    @Test
    fun saveAndLoadGameTest() {
        val prefix = "saveGameTests"
        val rootService = RootService()
        val players = listOf(
            Player(0, 0, "a", 20, 0, mutableListOf(), DiscColor.GREY, PlayerType.HUMAN),
            Player(0, 0, "Bulli-Bert", 14, 0, mutableListOf(), DiscColor.BLACK, PlayerType.RANDOM_AI)
        )
        rootService.gameService.startGame(players)
        val saveGame = File("$prefix.txt")

        rootService.fileService.saveGame(saveGame)
        /*
        val path = Paths.get(saveGame).toAbsolutePath()
        val file = File("$path.txt")
        */

        // Tests if the saveGameFile is created as planned
        assert(saveGame.exists())
        // Test if file is not empty
        assert(Files.size(saveGame.toPath()) > 0L)

        // Tests if saveGameFile is correctly created even for games with multiple game states
        println(rootService.currentGame!!.offerDisplay[1])
        println(rootService.currentGame!!.offerDisplay[2])
        rootService.playerService.makeMove(rootService.currentGame!!.offerDisplay[1],0,0,270, "") //P1

        // -> funktioniert nicht, da neuer player (-> hat zu Anfang leeres offerDisplay)
        //rootService.playerService.makeMove(rootService.currentGame!!.offerDisplay[2],0,1,0)

        rootService.playerService.makeMove(rootService.currentGame!!.offerDisplay[2],0,0,90, "") //P2
        rootService.playerService.makeMove(rootService.currentGame!!.offerDisplay[0],0,1,0, "") // P1

        val multipleStates = File("$prefix multipleStates.txt")
        rootService.fileService.saveGame(multipleStates)

        rootService.playerService.undo()
        rootService.playerService.undo()
        rootService.currentGame!!.intermezzoMoveCounter = 20

        val multipleUndos = File("$prefix multipleUndos.txt")
        rootService.fileService.saveGame(multipleUndos)
        rootService.currentGame!!.intermezzoMoveCounter = 5
        // Test if game is correctly loaded
        rootService.fileService.loadGame(multipleUndos)
        assertEquals(20, rootService.currentGame!!.intermezzoMoveCounter)
        assertEquals(-1,rootService.currentGame!!.previousState!!.intermezzoMoveCounter)

        // delete created saveFiles
        val directory = multipleStates.toPath()
        println("directory path is $directory")

        if (Files.exists(directory) && Files.isDirectory(directory)) {
            Files.walk(directory)
                .filter { filePath -> Files.isRegularFile(filePath) && filePath.fileName.toString().startsWith(prefix) }
                .forEach { filePath ->
                    Files.delete(filePath)
                }
        }
    }

    /**
     * Tests the correct creation of the tiles given in the set .csv-file.
     */
    @Test
    fun readCSVcreateTilesTest() {

        val rootService = RootService()
        val file = "/tiles_colornames_v2.csv"
        val inputStream = FileServiceTest::class.java.getResourceAsStream(file)
        checkNotNull(inputStream)
        val tiles = rootService.fileService.readCSVcreateTiles(inputStream)

        assertEquals(tiles[0].id, 1)
        assertEquals(tiles[33].id, 34)
        assertEquals(tiles[33].arrows.size, 4)
        assertEquals(
            tiles[33].arrows, listOf(
                Arrow(false, TileArrow.TOP, TileSpirit.AIR),
                Arrow(false, TileArrow.RIGHT, TileSpirit.AIR), Arrow(false, TileArrow.LEFT, TileSpirit.EARTH),
                Arrow(false, TileArrow.TOPLEFT, TileSpirit.EARTH)
            )
        )
        assertEquals(tiles.size, 72)
    }

    /**
     * Tests the else private function if the Arrows are correctly instantiated given the Strings received from the
     * provided .csv-file.
     */
    @Test
    fun createArrowsListTest() {

        val rootService = RootService()

        val stringList1Arrow = listOf("NONE", "NONE", "NONE", "NONE", "NONE", "NONE", "NONE", "AIR")
        val stringList2Arrows = listOf("FIRE", "AIR", "NONE", "NONE", "NONE", "NONE", "NONE", "NONE")
        val stringList4Arrows = listOf("NONE", "NONE", "NONE", "WATER", "EARTH", "FIRE", "AIR", "NONE")

        val res1Arrow = rootService.fileService.createArrowsList(stringList1Arrow)
        val res2Arrows = rootService.fileService.createArrowsList(stringList2Arrows)
        val res4Arrows = rootService.fileService.createArrowsList(stringList4Arrows)

        // Correct length of the list?
        assertEquals(res1Arrow.size, 1)
        assertEquals(res2Arrows.size, 2)
        assertEquals(res4Arrows.size, 4)

        val arrowSpirit: MutableList<TileSpirit> = mutableListOf()
        for (arrow in res1Arrow) arrowSpirit.add(arrow.spirit)
        val arrowSpirit2: MutableList<TileSpirit> = mutableListOf()
        for (arrow in res2Arrows) arrowSpirit2.add(arrow.spirit)
        val arrowSpirit4: MutableList<TileSpirit> = mutableListOf()
        for (arrow in res4Arrows) arrowSpirit4.add(arrow.spirit)

        // correct elements in the list?
        assertEquals(arrowSpirit.toList(), listOf(TileSpirit.AIR))
        assertEquals(arrowSpirit2.toList(), listOf(TileSpirit.FIRE, TileSpirit.AIR))
        assertEquals(
            arrowSpirit4.toList(),
            listOf(TileSpirit.WATER, TileSpirit.EARTH, TileSpirit.FIRE, TileSpirit.AIR)
        )
    }
}