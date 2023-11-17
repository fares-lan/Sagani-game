package service

import entity.Arrow
import entity.DiscColor
import entity.Player
import entity.Tile

/** [CriteriaObjectForAIService] provides the method [getCriteriaObjects], whitch takes all possible options a player
 * has, lay every tile on every position and looks up how many pints it can gain, how many disc are left, how many
 * arrows are occupied and how many tiles only have one arrow not occupied.
 * @param rootService*/
class CriteriaObjectForAIService (private val rootService: RootService){

    /**[CriteriaObject] is an object that saves everything for the AI to take a decision.
     * @param tile, the tile you save the data for
     * @param points, how many points you have after playing this tile
     * @param leftDiscs, how many discs are left after playing this tile
     * @param onlyOneArrow, how many tiles have just one arrow not occupied after playing this tile
     * @param occupiedArrows, how many arrowes are occupied after playing this tile */
    data class CriteriaObject(
        val tile: Tile,
        val points: Int,
        val leftDiscs: Int,
        val onlyOneArrow: Int,
        val occupiedArrows:Int
    )

    /**
     * [getCriteriaObjects] is a helping function that simulates a [PlayerService.makeMove] to get the gained points,
     * gained discs and amount of Tiles with just 1 not occupied arrow.
     * @param player
     * @param allPossibleTiles: before calculated options of free positions and rotations
     * @param criteria: sort by this criteria ("DISCS"/"POINTS")
     * @return a List with all possible make moves and its gained positions, gained discs and arrows sorted by a
     * criteria
     */
    fun getCriteriaObjects(player:Player,
                           allPossibleTiles: MutableList<Tile>,
                           criteria: String): MutableList<CriteriaObject> {
        // If the criteria is incorrect
        check(criteria == "POINTS" ||
                criteria == "DISCS" ||
                criteria == "ARROWS") { "The criteria is incorrect" }

        val listOfCriteriaObject = mutableListOf<CriteriaObject>()
        for (tile in allPossibleTiles) {
            val playerCopy = copyPlayer(player)
            playerCopy.placedTiles.add(tile)
            placeSoundDiscs(playerCopy, tile)
            checkAndMoveDiscs(playerCopy, playerCopy.placedTiles)

            val playerPoints = rootService.gameService.calculatePoints(playerCopy)
            val oneArrow = countTileWithOnlyOneArrowLeft(playerCopy.placedTiles)
            val occupiedArrows = countOccupiedArrows(playerCopy.placedTiles)

            for (arrow in tile.arrows) arrow.occupied=false
            tile.soundDiscs = mutableListOf()

            listOfCriteriaObject.add(
                CriteriaObject(
                    tile,
                    playerPoints,
                    playerCopy.discsLeftCount,
                    oneArrow,
                    occupiedArrows
                )
            )

        }

        var sortedListsByCriteria = mutableListOf<CriteriaObject>()
        when (criteria) {
            "POINTS" -> sortedListsByCriteria = listOfCriteriaObject.sortedWith(
                compareBy({ it.points }, {it.occupiedArrows}, { it.leftDiscs }, { it.onlyOneArrow }))
                .reversed()
                .toMutableList()
            "DISCS" -> sortedListsByCriteria = listOfCriteriaObject.sortedWith(
                compareBy({ it.leftDiscs }, { it.points }, { it.onlyOneArrow }, {it.occupiedArrows}))
                .reversed()
                .toMutableList()
            "ARROWS" -> sortedListsByCriteria = listOfCriteriaObject.sortedWith(
                compareBy({ it.points },  { it.onlyOneArrow }, { it.leftDiscs }, {it.occupiedArrows},))
                .reversed()
                .toMutableList()
        }

        return sortedListsByCriteria
    }

    /** [countOccupiedArrows] does exactly what it says.
     * @param placedTiles,
     * @return counted occupied arrows
     */
    fun countOccupiedArrows(placedTiles: MutableList<Tile>):Int{
        var count = 0
        for (tile in placedTiles){
            for (arrow in tile.arrows){
                if (arrow.occupied) count++
            }
        }
        return count
    }

    /**
     * [countTileWithOnlyOneArrowLeft] does exactly what it says.
     * @param placedTiles
     * @return the count of Tiles with just one not occupied arrow
     */
    fun countTileWithOnlyOneArrowLeft(placedTiles: MutableList<Tile>): Int {
        var count = 0
        for (tile in placedTiles) {
            var countOccupiedArrows = 0
            for (arrow in tile.arrows) {
                if (arrow.occupied) {
                    countOccupiedArrows++
                }
            }

            if (tile.arrows.size - countOccupiedArrows == 1) {
                count++
            }
        }

        return count
    }


    /**
     * Help method to copy an object of the class Player
     *
     * @param player Object of the class Player
     * @return Copied object of this object
     */
    fun copyPlayer(player: Player): Player {
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
    fun placeSoundDiscs(player: Player, tile: Tile) {

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






}