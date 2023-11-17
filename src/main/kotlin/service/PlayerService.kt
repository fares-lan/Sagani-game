package service

import entity.*

/**
 * PlayerService consists of actions the player can perform while playing a game of Sagani.
 *
 * @property rootService includes location of the current Sagani game and serves as link to the RootService.
 */
class PlayerService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * MakeMove takes a [selectedTile], [posX], [posY] and the [rotation] which needs to be given to [selectedTile].
     * It checks, if the tile can be placed (if not, an exception is thrown),
     * if yes, the tile gets its coordinates and rotation updated.
     * After that the tile is being added/removed from the respective lists, sound-discs are being placed and
     * the occupation of the arrows is being checked.
     *
     * @param selectedTile The tile selected by the player. If the conditions are met
     * it is placed on the players placedTiles-list.
     * @param posX The X-coordinate the tile is going to be placed on.
     * @param posY The Y-coordinate the tile is going to be placed on.
     * @param rotation The rotation the tile is going to have.
     */
    fun makeMove(selectedTile: Tile, posX: Int, posY: Int, rotation: Int, online:String) {

        /*
        if (rootService.networkService.client != null){
            when {
                (online == "opponent" &&
                        rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].name !=
                        rootService.networkService.client!!.playerName) -> { }

                (online == "localPlayer" &&
                        rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].name ==
                        rootService.networkService.client!!.playerName) -> {}
                else -> throw IllegalStateException("Not your turn!")
            }
        }
         */


        if (rootService.networkService.client != null){
            if (rootService.currentGame!!.intermezzoMoveCounter != -1){
                val a = rootService.currentGame!!.intermezzoOrder[rootService.currentGame!!.intermezzoMoveCounter]
                when {
                    (online == "opponent" &&
                            rootService.currentGame!!.players[a].name !=
                            rootService.networkService.client!!.playerName) -> { }
                    (online == "localPlayer" &&
                            rootService.currentGame!!.players[a].name ==
                            rootService.networkService.client!!.playerName) -> { }
                    else -> throw IllegalStateException("Not your turn!")
                }
            }
            else{
                when {
                    (online == "opponent" &&
                            rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].name !=
                            rootService.networkService.client!!.playerName) -> { }

                    (online == "localPlayer" &&
                            rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].name ==
                            rootService.networkService.client!!.playerName) -> {}
                    else -> throw IllegalStateException("Not your turn!")
                }
            }

        }




        var game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        changeGameState()
        game = rootService.currentGame
        val player = game!!.players[game.playerIndex]

        println("SelectedTile in playerService.makeMove: " + selectedTile)
        if (checkPlaceIsValid(posX, posY)) {
            println()
            println("checkPlaceIsValid true")
            println()
            selectedTile.posX = posX
            selectedTile.posY = posY
            selectedTile.rotation = rotation



            player.placedTiles.add(selectedTile)

            println(player.placedTiles)
            println(game.offerDisplay)

            var tileFromWhere = ""

            // check einfügen ob selectedTile aus dem drawPile kommt und der offerDisplay .size == 1 ist

            if (game.offerDisplay.any { it.id == selectedTile.id }) {
                game.offerDisplay.removeIf { it.id == selectedTile.id }
                tileFromWhere = "OFFER"
            } else if (game.intermezzoDisplay.any { it.id == selectedTile.id }) {
                game.intermezzoDisplay.removeIf { it.id == selectedTile.id }
                tileFromWhere = "INTERMEZZO"
            } else if (game.drawStack.first().id == selectedTile.id) {
                tileFromWhere = "DRAW"
                game.drawStack.removeFirst() //{ it.id == selectedTile.id }
                game.intermezzoDisplay.add(game.offerDisplay[0])
                game.offerDisplay.remove(game.offerDisplay[0])
            }

            // check all other tiles before checking newly placed tile
            checkAndMoveDiscs()

            // sets the soundDiscs with the according playerColor/cacoDisc Color on the tile
            placeSoundDiscs(selectedTile)

            // checks all placedTiles if any arrows have satisfied/completed and sets occupied = true
            // if a tile has all arrows occupied it is "flipped" by removing and returning all soundDiscs to the player
            checkAndMoveDiscs()
            // end player turn and call refreshable
            endTurn(tileFromWhere)


        } else {
            throw IllegalStateException("Invalid move! The tile cannot be placed at the specified position.")
        }
    }

    /**
     * This method checks, if the conditions are fulfilled so a tile can be placed at the given coordinates.
     *
     * @param posX x-coordinate of the tile
     * @param posY y-coordinate of the tile
     * @return boolean, true if chosen position is valid for placement
     */
    fun checkPlaceIsValid(posX: Int, posY: Int): Boolean {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        val player = game.players[game.playerIndex]

        //if there are no placed tiles and (0,0) is empty
        if (player.placedTiles.isEmpty() && posX == 0 && posY == 0) {
            return true
        }


        //checks coordinates of all already placed tiles and looks for equality
        //with given coordinates, if equal -> return false
        if (player.placedTiles.any { it.posX == posX && it.posY == posY }) {
            return false
        }


        //checks if there are tiles at the top, bottom, left or right of the to-be-placed tile
        for (placedTile in player.placedTiles) {
            if ((posX + 1 == placedTile.posX || posX - 1 == placedTile.posX) && posY == placedTile.posY) {
                return true
            }

            if ((posY + 1 == placedTile.posY || posY - 1 == placedTile.posY) && posX == placedTile.posX) {
                return true
            }
        }

        return false
    }

    /**
     * Looks at every tile that still has soundDiscs on it and checks the arrows if they can be set to occupied.
     * If a tile has all arrows occupied the soundDiscs are returned to the player and the tile is flipped (= soundDiscs
     * is empty)
     */
    fun checkAndMoveDiscs() {
        val gameService = rootService.gameService
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        val player = game.players[game.playerIndex]
        val tiles = player.placedTiles

        // walks through every tile in placedTile that is not yet "flipped"
        for (tile in tiles.filter { it.soundDiscs.isNotEmpty() }) {
            for (arrow in tile.arrows) {
                if (!arrow.occupied) {
                    arrow.occupied = gameService.checkOccupied(player, tile, arrow)
                }
            }
            // if all arrows are occupied, the tile is "flipped" and soundDiscs are removed and returned
            if (tile.arrows.all { it.occupied }) {
                tile.soundDiscs.clear()
                player.discsLeftCount += tile.arrows.size
            }
        }
    }


    /**
     * This method adds discs to a tile.
     * The amount of discs is being determined by the number of arrows the tile has.
     * If the player has no more discs left, cacophony discs are added instead.
     *
     * @param tile The tile which receives sound-/cacophony-discs.
     */
    fun placeSoundDiscs(tile: Tile) {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        val player = game.players[game.playerIndex]

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
     * Function for changing the Simulation-Speed of the current game.
     *
     * @param speed Simulation-Speed to be changed to.
     */
    fun changeSimSpeed(speed: SimulationSpeed) {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        game.simSpeed = speed
    }

    /**
     * Function for the undo-Action. In order to undo the function replaces the current game with
     * the game saved in the previousState attribute of the current Sagani game.
     */
    fun undo() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        checkNotNull(game.previousState) { "This is the first move!" }

        rootService.currentGame = rootService.currentGame!!.previousState

        // call refreshables
        onAllRefreshables { refreshAfterMakeMove() }
    }

    /**
     * Function for the redo-Action. In order to redo the function replaces the current game with
     * the game saved in the nextState attribute of the current Sagani game.
     */
    fun redo() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        checkNotNull(game.nextState) { "This is the most recent move!" }

        //next Sagani-Object(Sagani -> nextState) becomes the current game.
        rootService.currentGame = game.nextState

        // call refreshable
        onAllRefreshables { refreshAfterMakeMove() }
    }

    /**
     * Method implements the pass move in an intermezzo phase.
     * If every player did a turn/passed (intermezzoMoveCounter == players.size) and no tile was taken from the
     * intermezzoDisplay, all tiles are removed.
     * If every player did a turn in the intermezzo and at least one took a tile, the intermezzoDisplay retains all
     * remaining tiles and the intermezzoMoveCounter is set to 0 --> intermezzo phase is over.
     */
    fun passMoveIntermezzo() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        changeGameState()
        endTurn("")
    }

    /**
     * Updates player points, increments playerIndex and checks if finishing criterias are met.
     * Calling the necessary refreshables.
     */
    fun endTurn(tileFromWhere: String) {
        var game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        val player = game.players[game.playerIndex]
        /*
        if (game.intermezzoMoveCounter != -1) {
            player = game.players[game.intermezzoOrder[game.intermezzoMoveCounter]]
        }
        */

        val crossedFirst = crossedThresholdFirst(rootService.gameService.calculatePoints(player), player.points)

        // update player points
        player.points = rootService.gameService.calculatePoints(player)
        rootService.gameService.calcuatePointsOrder(player)
        rootService.gameService.calculateIntermezzoOrder()
        check(game.players.size in (2 until 5)) { "Incorrect player number. Must be two to four." }

        // check if this turn filled the intermezzoDisplay and an intermezzo phase follows
        // if an intermezzo phase is already running or the display is not full or else set startedIntermezzo to false
        checkAndSetStartedIntermezzo()
        // check if there's an intermezzo phase after the turn ended
        /*
        if (game.startedIntermezzo) {
            onAllRefreshables { refreshForIntermezzo() }
        }
        */

        // checks if the currently ending turn starts the last round until right before
        // the game starting players next turn
        checkAndSetStartedLastRound(crossedFirst) 

        // if the offerDisplay has no more cards, fill it up with 5 cards from drawStack
        if (game.offerDisplay.size == 0) {

            //end the game if the drawStack has less than 5 cards and the next player is the start-player
            if (game.drawStack.size in 0 until 5 && game.playerIndex == 0) {
                onAllRefreshables { refreshAfterGameEnds() }
                return
            }
            println("drawStack before:")
            println(rootService.currentGame!!.drawStack.subList(0, 7).toMutableList())
            println()
            // fill up the offerDisplay
            when {
                game.drawStack.size >= 5 -> {
                    game.offerDisplay.add(game.drawStack.removeFirst())
                    game.offerDisplay.add(game.drawStack.removeFirst())
                    game.offerDisplay.add(game.drawStack.removeFirst())
                    game.offerDisplay.add(game.drawStack.removeFirst())
                    game.offerDisplay.add(game.drawStack.removeFirst())
                    println()
                }
                game.drawStack.size < 5 -> {
                    while (game.drawStack.size>0){
                        game.offerDisplay.add(game.drawStack.removeFirst())
                    }
                }
            }
        }

        println("drawStack after:")
        println(rootService.currentGame!!.drawStack.subList(0, 7).toMutableList())
        println("offerDisplay after:")
        println(rootService.currentGame!!.offerDisplay)
        println()

        // call refreshables
        // check if game end criteria are met: lastRound, every player had equal amounts of turns
        // and we're not in an intermezzo phase
        // check if game end criteria are met: lastRound, every player had equal amounts of turns
        // and we're not in an intermezzo phase
        val gameEnds:Boolean =
            if (game.lastRound && game.playerIndex == (game.players.size - 1) && game.intermezzoMoveCounter == -1){
                onAllRefreshables { refreshAfterGameEnds() }
                true
            } else false


        // save gamestate to Sagani-object and extend LinkedList and set new currentgame

        //changeGameState()


        game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        if (rootService.networkService.client != null &&
            rootService.networkService.client!!.playerName == game.players[game.playerIndex].name
        ) {
            rootService.networkService.sendTurnMessage(tileFromWhere, game.startedIntermezzo, game.startedLastRound)
        }


        // increment playerIndex and reset if playerIndex == players.size
        game.playerIndex = nextPlayerIndex()
        onAllRefreshables { refreshAfterMakeMove() }

        // check if the next player is an AI and therefore does not have to choose a tile manually
        if(!gameEnds) onAllRefreshables { refreshAfterTurnEnds() }


        if (game.startedIntermezzo) {
            onAllRefreshables { refreshForIntermezzo() }
        }

    }

    /** [changeGameState] safes the current Sagani state and goes to currentGame.nextState */
    fun changeGameState(){
        val game = rootService.currentGame
        checkNotNull(game)

        rootService.currentGame!!.nextState = rootService.currentGame!!.clone(rootService.currentGame!!)
        rootService.currentGame = rootService.currentGame!!.nextState
        println("in changeState2")
    }



    /**
     * Checks if a player is crossing the points threshold to win the game and if he does as the first player in the
     * game. If both criterias are met it returns true, as the player crossed the threshold first.
     * @param newPoints points after the turn ended and the new standing is calculated
     * @param oldPoints points before the turn ended
     * @return true if the player crossed the finish line / points threshold as first player in the game
     */
    fun crossedThresholdFirst(newPoints: Int, oldPoints: Int): Boolean {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        val threshold = 75 - (game.players.size - 2) * 15
        if (game.players.any { it.points > threshold })
            return false

        return threshold in (oldPoints + 1) until newPoints + 1
    }

    /**
     * Auxiliary function to correctly set the [Sagani] property startedIntermezzo if the current turn started
     * an intermezzo phase and sets the intermezzoMoveCounter from -1 (no intermezzo phase) to 0 (intermezzo
     * currently running)
     */
    fun checkAndSetStartedIntermezzo() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        if (game.intermezzoDisplay.size == 4 && game.intermezzoMoveCounter == -1) {
            game.startedIntermezzo = true
            //game.intermezzoMoveCounter = 0
        } else {
            game.startedIntermezzo = false
        }
    }

    /**
     * Auxiliary function to correctly check and set the [Sagani] properties lastRound and startedLastRound
     * @param firstOverThreshold is the boolean value provided by [crossedThresholdFirst]
     */
    fun checkAndSetStartedLastRound(firstOverThreshold: Boolean) {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        // if the last round was started set lastRound =  true
        if (firstOverThreshold || (game.offerDisplay.size == 0 && game.drawStack.size in 5 until 10))
            game.lastRound = true
        // startedLastRound only marks the player turn which started the last round therefore needs to be set to false
        // for any other turn in the last round
        game.startedLastRound =
            firstOverThreshold || (game.offerDisplay.size == 0 && game.drawStack.size in 5 until 10)
    }

    /**
     * Returns the index of the next player.
     * Next player is not just the increment of playerIndex but has to consider that there may be an intermezzoPhase
     * running which has a different player order than normal turns.
     * @return index of the next player on the players list [0..players.size-1]
     */
    fun nextPlayerIndex(): Int {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        if (game.intermezzoMoveCounter ==-1 && game.startedIntermezzo){
            val returnVal = game.intermezzoOrder[0]
            game.intermezzoMoveCounter = 0
            return returnVal
        }
        // either increases the intermezzoMoveCounter to return the next player from the intermezzoOrder
        // or return the next player index from the normal game order
        return if (game.intermezzoMoveCounter in (0 until game.players.size)) {
            if ((game.intermezzoMoveCounter + 1) % game.players.size == 0){
                game.intermezzoMoveCounter = -1
                if (game.intermezzoDisplay.size==4)game.intermezzoDisplay = mutableListOf()
                (game.playerIndex + 1) % game.players.size
            }
            else{
                game.intermezzoMoveCounter++
                game.intermezzoOrder[game.intermezzoMoveCounter]

            }
        } else {
            game.playerIndex = (game.playerIndex + 1) % game.players.size
            game.playerIndex
        }
    }
}