package service

import entity.*
import kotlin.random.Random

/**
 * [AIService] is a service that simulates a Person, also called AI, that tries to be as good as possible or just doing
 * a random "I don´t want to play anymore you are all shit" childlike move (or just for testing). It also contains
 * helping function so that the best move can be possible.
 * @param rootService Root service that controls the behavior of all services in the application
 */
class AIService(private val rootService: RootService): AbstractRefreshingService() {

    val criteriaObjectService = CriteriaObjectForAIService(rootService)

    /**
     * [moveByAI] is the function that has to be called by a refresh (that´s look if the next
     * player is an AI) so that a [makeSmartMove] or [makeRandomMove] can be performed.
     */
    fun moveByAI() {
        // If the game has already started
        check(rootService.currentGame != null) { return }

        val player = rootService.currentGame!!.players[rootService.currentGame!!.playerIndex]
        // If the current player is AI
        check(player.playerType != PlayerType.HUMAN) { return }

        if (player.playerType == PlayerType.RANDOM_AI) {
            makeRandomMove()
        } else {
            makeSmartMove()
        }
    }


    /**
     * [makeRandomMove] is move that´s is done by an AI player. By random, it chooses a Tile from the offer or
     * intermezzo display and make a move at a random position with a random rotation.
     */
    fun makeRandomMove() {
        val player = rootService.currentGame!!.players[rootService.currentGame!!.playerIndex]

        val offerDisplay = rootService.currentGame!!.offerDisplay
        val intermezzoDisplay = rootService.currentGame!!.intermezzoDisplay
        val isIntermezzo =
            if(rootService.currentGame!!.intermezzoMoveCounter!=-1){
                true
            }
            else false
        val freePositions = getFreePositions(player.placedTiles)
        var randomFreePosition = 0

        // Choose free position randomly only if more then one freePositions are available
        if(freePositions.size > 1){
            randomFreePosition = Random.nextInt(0, freePositions.size)
        }

        if (!isIntermezzo){
            if (offerDisplay.size==1 && rootService.currentGame!!.drawStack.size>0){
                val headOrTails = Random.nextBoolean()
                if (headOrTails){
                    rootService.playerService.makeMove(
                        offerDisplay[0],
                        freePositions[randomFreePosition].first,
                        freePositions[randomFreePosition].second,
                        // Random rotation for the tile
                        Random.nextInt(0, 3) * 90,
                    "localPlayer")
                }
                else{
                    rootService.playerService.makeMove(
                        rootService.currentGame!!.drawStack.first(),
                        freePositions[randomFreePosition].first,
                        freePositions[randomFreePosition].second,
                        // Random rotation for the tile
                        Random.nextInt(0, 3) * 90,
                        "localPlayer")
                }
            }
            else{
                rootService.playerService.makeMove(
                    offerDisplay[Random.nextInt(0, offerDisplay.size)],
                    freePositions[randomFreePosition].first,
                    freePositions[randomFreePosition].second,
                    // Random rotation for the tile
                    Random.nextInt(0, 3) * 90,
                    "localPlayer")

            }
        }
        else{
            rootService.playerService.makeMove(
                intermezzoDisplay[Random.nextInt(0, intermezzoDisplay.size)],
                freePositions[randomFreePosition].first,
                freePositions[randomFreePosition].second,
                // Random rotation for the tile
                Random.nextInt(0, 3) * 90,
                "localPlayer")
        }
    }

    /**
     * Method for the AI to make a smart move.
     * Depending on the situation, the player chooses a tile to move:
     * - Either the player has few discs, then you must choose a tile, putting which the player will have more discs,
     * - or the player chooses a tile so that only tiles with 1 uncovered arrow remain for future moves
     * - or the player aims for more points.
     */
    fun makeSmartMove() {
        val player = rootService.currentGame!!.players[rootService.currentGame!!.playerIndex]

        val offerDisplay = rootService.currentGame!!.offerDisplay
        val intermezzoDisplay = rootService.currentGame!!.intermezzoDisplay
        val isIntermezzo = (rootService.currentGame!!.intermezzoMoveCounter) != -1
        println()
        println("isIntermezzo: " + isIntermezzo)
        println()
        val fromOfferOrDrawStack =
            if (offerDisplay.size==1 && rootService.currentGame!!.drawStack.size>1){
                (offerDisplay+rootService.currentGame!!.drawStack.first()).toMutableList()
            }
            else mutableListOf()

        val possibleOptions =
            if (!isIntermezzo) {
                if (fromOfferOrDrawStack.size==2){
                    println("in allPossibleOptions(fromOfferOrDrawStack, player.placedTiles)")
                    allPossibleOptions(fromOfferOrDrawStack, player.placedTiles)

                }
                else{
                    println("in allPossibleOptions(offerDisplay, player.placedTiles)")
                    allPossibleOptions(offerDisplay, player.placedTiles)

                }
            }
            else{println("in allPossibleOptions(intermezzoDisplay, player.placedTiles)")
                allPossibleOptions(intermezzoDisplay, player.placedTiles)
                }

        // Selected tile for the future AI move
        val chosenTile = chooseTileForMove(player, possibleOptions)
        println("Chosen tile by AI: " + chosenTile.posX + ", " + chosenTile.posY)
        val rotation = chosenTile.rotation

        //somewhere in the calculations we write into the tiles.soundDiscs in the display, here we just fix that
        for (tile in rootService.currentGame!!.offerDisplay) tile.soundDiscs = mutableListOf()
        for (tile in rootService.currentGame!!.intermezzoDisplay) tile.soundDiscs = mutableListOf()
        for (tile in rootService.currentGame!!.drawStack) tile.soundDiscs = mutableListOf()

        val tileToPlay =
            if (offerDisplay.any{it.id == chosenTile.id}){
                rootService.currentGame!!.offerDisplay.first { it.id == chosenTile.id }
            }
            else {
                if (rootService.currentGame!!.drawStack.any{it.id == chosenTile.id}){
                    rootService.currentGame!!.drawStack.first()
                }
                else rootService.currentGame!!.intermezzoDisplay.first { it.id == chosenTile.id }
            }


        rootService.playerService.makeMove(tileToPlay, chosenTile.posX, chosenTile.posY, rotation,
            "localPlayer")

    }

    private fun chooseTileForMove(player: Player, possibleOptions: MutableList<Tile>): Tile {
        val thereIsALeader = checkForOpponent(player)

        val chosenTile : Tile?

        var finalOptions = possibleOptions

        if (thereIsALeader.second) {
            // println("!!! BEFORE BITCHY MOVE: !!!")
            // println(thereIsALeader.first.name)
            val leaderTile = makeBitchyOpponentMove(thereIsALeader.first)
            leaderTile.posX = 0
            leaderTile.posY = 0

            val leaderOptions = allPossibleOptions(mutableListOf(leaderTile), player.placedTiles)
            finalOptions = leaderOptions
        }

        // from here on decide what action should take place:
        val criteriaList:MutableList<CriteriaObjectForAIService.CriteriaObject> =
        // If a player has less than 4 discs left, he will theoretically not be able to
        // cover a tile worth 10 points and not go into deficit. So we sort first by
            // discs and then by number of points.
            if (player.discsLeftCount < 4) {
                criteriaObjectService.getCriteriaObjects(player, finalOptions, "DISCS")
            }
            // If a player has less than 5 points left until the end of the game.
            // So it would be worth sorting by the number of tiles with only 1 uncovered arrow.
            else if (checkForQuickWin(player)) {
                criteriaObjectService.getCriteriaObjects(player, finalOptions, "ARROWS")
            }
            // Otherwise we sort first by the number of points and then by the number of
            // remaining discs.
            else {
                criteriaObjectService.getCriteriaObjects(player, finalOptions, "POINTS")
            }

        // Select randomly one of the best k tiles from the list by the criterion.
        var k = 1
        for (i in 1 until criteriaList.size) {
            if ((criteriaList.get(0).onlyOneArrow == criteriaList.get(i).onlyOneArrow) &&
                (criteriaList.get(0).leftDiscs == criteriaList.get(i).leftDiscs)){
                if ((criteriaList.get(0).points == criteriaList.get(i).points) &&
                    (criteriaList.get(0).occupiedArrows == criteriaList.get(i).occupiedArrows)){
                    k++
                }
            }
        }

        println("K : = " + k)

        val randomIndex = Random.nextInt(0, k)

        chosenTile = criteriaList.get(randomIndex).tile

        return chosenTile
    }

    /**
     * [checkForOpponent] looks up if you are the leader oder the is someone who has more points than you, that´s than
     * your opponent.
     * @param player
     * @return Pair of the leader ,you or an opponent, if you are the leader a false, if someone else true
     */
    fun checkForOpponent(player: Player): Pair<Player, Boolean> {
        println()
        println("checkForOpponent: ")

        val playerList = rootService.currentGame!!.players
        var leader = playerList[0]
        for (opponent in playerList) {
            if (opponent.points > leader.points) {
                leader = opponent
            }
        }

        println(leader.name + " : " + leader.points)
        println(player.name + " : " + player.points)

        // if not fulfills, you are the leader
        return if (leader.points - player.points > 15) {
            println("true")
            Pair(leader, true)
        } else {
            println("false")
            Pair(leader, false)
        }
    }

    /**
     * [makeBitchyOpponentMove] is an edited version of [makeSmartMove] witch calculates which tile your opponent would
     * choose
     * @param opponent: captain obvious
     * @return the tile your opponent would choose
     */
    fun makeBitchyOpponentMove(opponent: Player): Tile {
        val offerDisplay = rootService.currentGame!!.offerDisplay
        val intermezzoDisplay = rootService.currentGame!!.intermezzoDisplay
        // Total display
        val display = (offerDisplay + intermezzoDisplay).toMutableList()

        // Getting all possible combinations of all tiles from the offerDisplay at all possible positions
        val possibleOptions = allPossibleOptions(display, opponent.placedTiles)
        val criteriaList:MutableList<CriteriaObjectForAIService.CriteriaObject> =
            if (opponent.discsLeftCount < 4) {
                criteriaObjectService.getCriteriaObjects(opponent, possibleOptions, "DISCS")
            } else {
                criteriaObjectService.getCriteriaObjects(opponent, possibleOptions, "POINTS")
            }

        return criteriaList[0].tile
    }


    /**
     * [getFreePositions] is an almost linear methode to get the possible free positions to lay down a tile.
     * It works like a vertical and horizontal scanline. First you fix a posY, search for tiles with the same posY and
     * put the posX of the tiles in a list. Then take the posX and add/sub 1 to/off and take these in a new List.
     * Build the difference of both lists, so you have no duplicates. At the end you have a list for free posX in that
     * posY height.
     * @param placedTiles, is the placedTiles list of the given AI player.
     * @return Unique list with all possible pairs of positions for tiles.
     */
    fun getFreePositions(placedTiles: MutableList<Tile>): List<Pair<Int, Int>>{
        // list to return the free positions
        val freePositions = mutableListOf<Pair<Int, Int>>()
        // if the placedTile is empty, there is just 1 possible Position (0,0)
        if (placedTiles.size == 0){
            freePositions.add(Pair(0, 0))
            return freePositions
        }

        var scanlinePosY = 0
        var scanlinePosX = 0
        // if y is fully scanned switch to x by switching to true, means that y is finished
        var switch = false
        // if both, horizontal and vertical, scan finished
        var fullyScanned = false
        while (!fullyScanned) {
            val listOfPositions = mutableListOf<Int>()
            for (tile in placedTiles) {
                if (!switch && tile.posY == scanlinePosY){
                    listOfPositions.add(tile.posX)
                }
                if (switch && tile.posX == scanlinePosX){
                    listOfPositions.add(tile.posY)
                }
            }
            var listOfFreePositions = mutableListOf<Int>()
            for (position in listOfPositions) {
                listOfFreePositions.add(position + 1)
                listOfFreePositions.add(position - 1)
            }
            listOfFreePositions = listOfFreePositions.minus(listOfPositions.toSet()).toMutableList()
            for (position in listOfFreePositions) {
                if (!switch) {
                    freePositions.add(Pair(position, scanlinePosY))
                }
                else freePositions.add(Pair(scanlinePosX, position))
            }
            if (!switch) {
                val (a,b) = getScanlinePosition(scanlinePosY,true, placedTiles)
                scanlinePosY = a
                switch = b
            }
            else{
                val (a,b) = getScanlinePosition(scanlinePosX,false,  placedTiles)
                scanlinePosX = a
                fullyScanned = b
            }
        }

        return freePositions.distinct()
    }

    /**
     * [getScanlinePosition] is a helping functions that returns a Pair of the next position to scan and boolean, depends
     * on which scanline it is, the return means a switch from y to x or that x is fully scanned to so that the complete
     * scan is finished.
     * @param paramPosition: position to get the next position for
     * @param isY: if you want the next y position it´s true, when you want the x position its false
     * @param tiles: placedTiles to proof if there is a tile with the new possible position
     * @return: a pair of int, the new position, and a bool, if a switch/full-scan happened
     */
    fun getScanlinePosition(paramPosition: Int, isY: Boolean, tiles: MutableList<Tile>):Pair<Int, Boolean>{
        var position = paramPosition
        var switch = false
        if (isY) {
            if (position >= 0) {
                if (tiles.any { it.posY == position + 1 }) position++
                else position = -1
            } else {
                if (tiles.any { it.posY == position - 1 }) position--
                else switch = true
            }
        } else {
            if (position >= 0) {
                if (tiles.any { it.posX == position + 1 }) position++
                else position = -1
            } else {
                if (tiles.any { it.posX == position - 1 }) position--
                else switch = true
            }
        }

        return Pair(position, switch)
    }

    /**
     * [allPossibleOptions] is a function that gets a display list (offer or intermezzo) and a players placedTile.
     * It takes a Tile from the display list, and creates a new tile with same arrows, tiles, points and spirit, but
     * changes the position to the free positions and every possible rotation. At the End you should have
     * display.size * freePositions * 4 (for all rotations) options.
     *
     * @param display, is a list of offer Display or intermezzoDisplay.
     * @param placedTiles is the list of the players placed tiles.
     * @return All tiles from some display at all possible positions and rotations.
     */
    fun allPossibleOptions(display: MutableList<Tile>, placedTiles: MutableList<Tile>): MutableList<Tile> {
        check(display.size != 0) { "Display is empty." }
        println()
        println("free positions: ")
        val freePositions = getFreePositions(placedTiles)
        for (position in freePositions){
            print("(" +position.first +", "+position.second+") ;")
        }
        println()
        println("placed tiles: ")
        for (position in placedTiles){
            print("(" +position.posX +", "+position.posY+") ;")
        }
        val listOfAllOptions = mutableListOf<Tile>()
        for (i in 0 until display.size) {
            for (position in freePositions) {
                val tile = display[i]
                var rotation = -90
                val posX = position.first
                val posY = position.second

                // Combinations with all possible rotations
                repeat(4) {
                    rotation += 90
                    val newTile = Tile(
                        tile.id, tile.points, posX, posY, rotation, tile.soundDiscs, tile.arrows, tile.spirit)
                    listOfAllOptions.add(newTile)
                }
            }
        }
        return listOfAllOptions
    }

    /**
     * Help method to check if the play can quickly win a game with only 1 move
     *
     * @param player Curent player
     * @return If a player has less than 5 points left
     *         (Number of points depending on the number of players in the game)
     */
    fun checkForQuickWin(player: Player): Boolean {
        val playerPoints = player.points
        val playersSize = rootService.currentGame!!.players.size

        // If a player has less than 5 points left until the end of the game
        // - playerSize = 2 -> 105 - 15 * 2 = 75
        // - playerSize = 3 -> 105 - 15 * 3 = 60
        // - playerSize = 4 -> 105 - 15 * 4 = 45
        if (105 - 15 * playersSize - playerPoints <= 5) {
            return true
        }

        return false
    }

    /*
    /**
     * Help method to copy an object of the class Player
     *
     * @param player Object of the class Player
     * @return Copied object of this object
     */
    private fun copyPlayer(player: Player): Player {
        val playerCopyPlacedTiles = mutableListOf<Tile>()
        for (i in 0 until player.placedTiles.size) {
            val tile = player.placedTiles[i]

            val playerCopyArrows = mutableListOf<Arrow>()
            for (j in 0 until tile.arrows.size) {
                val arrow = tile.arrows[j]
                playerCopyArrows.add(Arrow(arrow.occupied, arrow.orientation, arrow.spirit))
            }

            val playerCopyTile = Tile(
                id = tile.id,
                points = tile.points,
                posX = tile.posX,
                posY = tile.posY,
                rotation = tile.rotation,
                soundDiscs = tile.soundDiscs,
                arrows = playerCopyArrows,
                spirit = tile.spirit
            )
            playerCopyPlacedTiles.add(playerCopyTile)
        }

        return Player(
            points = player.points,
            pointsOrder = player.pointsOrder,
            name = player.name,
            discsLeftCount = player.discsLeftCount,
            cacoCount = player.cacoCount,
            placedTiles = playerCopyPlacedTiles,
            color = player.color,
            playerType = player.playerType
        )
    }

    /**
     * Shortened and method from the service layer.
     *
     * @param player Current AI player.
     * @param tile Selected and moved tile.
     */
    private fun placeSoundDiscs(player: Player, tile: Tile) {

        for (arrow in tile.arrows) {
            if (player.discsLeftCount > 0) {
                tile.soundDiscs.add(player.color)
                player.discsLeftCount--
            } else {
                tile.soundDiscs.add(DiscColor.RED)
                player.cacoCount++
            }
        }
    }

    /**
     * Shortened and method from the service layer.
     *
     * @param player Current AI player.
     * @param tiles Old player tiles with a new selected tile for the turn.
     */
    private fun checkAndMoveDiscs(player: Player, tiles: MutableList<Tile>) {
        for (tile in tiles.filter { it.soundDiscs.isNotEmpty() }) {
            for (arrow in tile.arrows) {
                arrow.occupied = rootService.gameService.checkOccupied(player, tile, arrow)
            }

            // If occupation on the arrows is changed and every arrow is covered now
            if (tile.arrows.all { it.occupied }) {
                player.discsLeftCount += tile.arrows.size
            }
        }
    }

     */
}