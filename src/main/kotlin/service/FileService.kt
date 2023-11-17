package service

import entity.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream


/**
 * Provides the service layer with the ability to read and write external files.
 * The implemented methods enable the player to save the current game and load previously saved games.
 */

class FileService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Loads a game of Sagani from an external file.
     * @param saveGame is the file-object from which the game should be loaded
     * with the chosen name.
     */
    // Hier statt String direkt ein file übergeben als Parameter
    fun loadGame(saveGame: File) {

        println(if(rootService.currentGame == null){
            "current game = null"
        }else{
            "current game not null"
        })
        if(rootService.currentGame == null){
            val player1 = Player(
                points = 0,
                pointsOrder = 0,
                name = "Spieler1",
                discsLeftCount = 22,
                cacoCount = 0,
                placedTiles = mutableListOf(),
                color = DiscColor.BLACK,
                playerType = PlayerType.HUMAN
            )

            val player2 = Player(
                points = 0,
                pointsOrder = 0,
                name = "Spieler2",
                discsLeftCount = 24,
                cacoCount = 0,
                placedTiles = mutableListOf(),
                color = DiscColor.BROWN,
                playerType = PlayerType.HUMAN
            )

            //val loadedGame = rootService.currentGame
            rootService.gameService.startGame(mutableListOf(player1, player2))
            //rootService.currentGame = loadedGame
        }


        saveGame.bufferedReader().use { reader ->
            // skipNextLine is set true if the currentGameMarker is found before the Json String that was the
            // currentGame before saving
            var skipNextLine = false
            var prevState: Sagani? = null
            var currentState: Sagani?
            reader.forEachLine { line ->
                // currentGameMarker found in iteration before
                // set currentGame and connect linkedList as usual
                if (skipNextLine) {
                    skipNextLine = false
                    rootService.currentGame = Json.decodeFromString(line)
                    rootService.currentGame!!.previousState = prevState
                    if (prevState!=null){
                        prevState!!.nextState = rootService.currentGame
                    }
                // skipp line that only contains the currentGameMarker "---"
                } else if (line.startsWith("---")) {
                    skipNextLine = true
                }
                // connect the linkedList as usual
                else {
                    currentState = Json.decodeFromString(line)
                    currentState!!.previousState = prevState
                    if (prevState != null) {
                        prevState!!.nextState = currentState
                    }
                    prevState = currentState
                }
            }
        }
        onAllRefreshables { refreshAfterStartGame() }
        onAllRefreshables { refreshAfterMakeMove() }
    }

    /**
     * Saves the current game and all necessary objects in a file. The current game is saved at the same path the
     * executed file is located. If the chosen name already exists, current date and time are added to the name to make
     * it unique.
     * Marks the currentGame in the series of Json strings with a marker "---" for later recognition when loading.
     * @param saveGame is the file-object in which the game should be saved and written to the hard drive as .txt file
     * with the chosen name.
     */
    fun saveGame(saveGame:File) {
        /*
        var name = saveName
        val currentPath = Paths.get("").toAbsolutePath()
        val file = File("$currentPath/$saveName.txt")
        */
        val json = Json { prettyPrint = true }
        val marker = "---"
        /*
        if (file.exists()) {
            val date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
            val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))

            name = saveName + "_" + date + "_" + time
        }
        */
        val currentGameNo = hasXPrevious()
        val gameList = linkedListToList()

        saveGame.bufferedWriter().use { writer ->
            gameList.forEachIndexed { index, sagani ->
                val jsonString = Json.encodeToString(sagani)

                if (index == currentGameNo) {
                    writer.write("$marker\n$jsonString\n")
                } else {
                    writer.write("$jsonString\n")
                }
            }
        }
        
    }

    private fun hasXPrevious(): Int {
        var game = rootService.currentGame
        var prevGames = 0
        while (game!!.previousState != null) {
            prevGames++
            game = game.previousState
        }
        return prevGames
    }

    private fun linkedListToList(): List<Sagani> {
        var game = rootService.currentGame
        val gameList: MutableList<Sagani> = mutableListOf()
        while (game!!.previousState != null) {
            game = game.previousState
        }
        while (game != null) {
            gameList.add(game)
            game = game.nextState
        }
        return gameList
    }

    /**
     * Function readCSVcreateTiles creates the [Tile]-objects with all its attributes.
     *
     * @param inputStream should be a file of .csv format with, comma separated and the columns in the necessary order
     * @return is the complete list of [Tile]s that are necessary for the game in shuffled order.
     */
    fun readCSVcreateTiles(inputStream: InputStream): MutableList<Tile> {
        val reader = inputStream.bufferedReader()
        // "remove" first line -> headlines
        reader.readLine()
        var line: String?
        val tileList: MutableList<Tile> = mutableListOf()
        while (reader.readLine().also { line = it } != null) {
            if (line!!.isNotBlank()) {
                // splitting into 3 different declarations because Kotlin is limited to 5 variables at a time
                // id variable may be used in future for mapping the individual visual images to the tiles
                val (id, color, top, topright, restComponents) = line!!.split(
                    ",", ignoreCase = false, limit = 5
                )
                val (right, bottomright, bottom, bottomleft, restComponents2) = restComponents.split(
                    ",", ignoreCase = false, limit = 5
                )
                val (left, topleft, score) = restComponents2.split(",", ignoreCase = false)
                tileList.add(
                    Tile(
                        id = id.toInt(),
                        points = score.trim().toInt(),
                        posX = 0,
                        posY = 0,
                        rotation = 0,
                        soundDiscs = mutableListOf(),
                        arrows = createArrowsList(
                            listOf(
                                top, topright, right, bottomright, bottom, bottomleft, left, topleft
                            )
                        ),
                        spirit = TileSpirit.valueOf(color)
                    )
                )
            }
        }
        return tileList
    }

    /**
     * Auxiliary method of readCSVcreateTiles to create a list of [Arrow]s for the Tiles
     *
     * @param list Contains the Strings from the .csv-file. Allowed Strings are the elements of the [TileSpirit]-Enum
     */
    fun createArrowsList(list: List<String>): List<Arrow> {
        val workingList = mutableListOf<Arrow>()
        for (i in 0 until 8) {
            if (list[i] != "NONE") {
                workingList.add(
                    Arrow(
                        occupied = false, orientation = when (i) {
                            0 -> TileArrow.TOP
                            1 -> TileArrow.TOPRIGHT
                            2 -> TileArrow.RIGHT
                            3 -> TileArrow.BOTTOMRIGHT
                            4 -> TileArrow.BOTTOM
                            5 -> TileArrow.BOTTOMLEFT
                            6 -> TileArrow.LEFT
                            7 -> TileArrow.TOPLEFT
                            else -> error("i reached a value that should not be possible...")
                        }, spirit = TileSpirit.valueOf(list[i])
                    )
                )
            }
        }
        return workingList.toList()
    }

}