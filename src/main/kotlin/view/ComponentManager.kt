package view

import entity.Player
import entity.Tile
import service.RootService
import service.TileImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Coordinate
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.Visual

const val CELL_SIZE = 1000.0 / 22

/**
 * manages game-components that are used by multiple scenes
 */
class ComponentManager(private val rootService: RootService) : Refreshable {

    private val tileImageLoader = TileImageLoader()

    // PlayerGrids:
    private var playerGrid1 = CartesianGridPane<CardView>(
        posX = 300-CELL_SIZE, posY = 280-CELL_SIZE,
        rows = 1, columns = 1, spacing = 0, visual = Visual.EMPTY
    )

    private var playerGrid2 = CartesianGridPane<CardView>(
        posX = 1620, posY = 280-CELL_SIZE,
        rows = 1, columns = 1, spacing = 0, visual = Visual.EMPTY
    )

    private var playerGrid3 = CartesianGridPane<CardView>(
        posX = 300-CELL_SIZE, posY = 800,
        rows = 1, columns = 1, spacing = 0, visual = Visual.EMPTY
    )

    private var playerGrid4 = CartesianGridPane<CardView>(
        posX = 1620, posY = 800,
        rows = 1, columns = 1, spacing = 0, visual = Visual.EMPTY
    )

    var playerGridList = mutableListOf(playerGrid1, playerGrid2, playerGrid3, playerGrid4)

    /**
     * contains pairs of [Tile]s and corresponding [ImageVisual]s.
     * This way the visuals have to be loaded only once and not every time a CardView is changed.
     */
    private val tileFrontMap: BidirectionalMap<Int, ImageVisual> = BidirectionalMap()
    private val tileBackMap: BidirectionalMap<Int, ImageVisual> = BidirectionalMap()

    /**
     * initializes both tileMaps with all tiles in the drawStack
     */
    fun initializeTileMaps() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        game.offerDisplay.forEach { tile ->
            val frontImage = ImageVisual(tileImageLoader.frontImageFor(tile.id))
            val backImage = ImageVisual(tileImageLoader.backImageFor(tile.id))
            tileFrontMap.add(tile.id to frontImage)
            tileBackMap.add(tile.id to backImage)
        }

        game.drawStack.forEach { tile ->
            val frontImage = ImageVisual(tileImageLoader.frontImageFor(tile.id))
            val backImage = ImageVisual(tileImageLoader.backImageFor(tile.id))
            tileFrontMap.add(tile.id to frontImage)
            tileBackMap.add(tile.id to backImage)
        }
    }

    /**
     * changes [CardView] visuals according to given [Tile].
     *
     * @param cardView where it shall be displayed.
     * @param tile is the new [Tile] that shall be displayed. if this parameter is null, the cardView visuals will be
     *        set to empty.
     * @param front determines whether front or back will be shown (defaults to front).
     */
    fun changeCardView(cardView: CardView, tile: Tile?, front: Boolean = true) {
        if (tile == null) {
            cardView.frontVisual = Visual.EMPTY
            cardView.backVisual = Visual.EMPTY
        }
        else {
            cardView.frontVisual = tileFrontMap.forward(tile.id)
            cardView.backVisual = tileBackMap.forward(tile.id)
        }
        if (front) {
            cardView.showFront()
        } else {
            cardView.showBack()
        }
    }

    /**
     * checks for each tile in [tileList] if it is placed on a border of [grid]. If so an additional row/column will be
     * added to support further moves.
     * @param grid in which the tiles are placed
     * @param tileList contains the tiles that are placed in the grid
     */
    private fun checkForGrow(grid: CartesianGridPane<CardView>, tileList: MutableList<Tile>) {
        tileList.forEach { tile ->
            // check left border
            if (tile.posX + grid.originX <= 0) {
                grid.growCartesian(1,0,0,0)
            }
            // check right border
            if (tile.posX + grid.originX >= grid.columns-1) {
                grid.growCartesian(0,1,0,0)
            }
            // check top border
            if (tile.posY - grid.originY >= 0) {
                grid.growCartesian(0,0,1,0)
            }
            // check bottom border
            if (-(tile.posY - grid.originY) >= grid.rows-1) {
                grid.growCartesian(0,0,0,1)
            }
        }
    }

    /**
     * updates playerGrid by growing and clearing it first and then updating visuals to match the placedTiles
     * of the player.
     * @param playerGrid where tiles will be displayed
     * @param player whose placedTiles will be displayed
     */
    fun updatePlayerGrid(playerGrid: CartesianGridPane<CardView>, player: Player) {
        // checking if grid supports placedTiles of player
        checkForGrow(playerGrid, player.placedTiles)
        // clearing grid
        playerGrid.apply {
            for (i in 0 until columns) {
                for (j in 0 until rows) {
                    if (this[i, j] == null) {
                        this[i, j] = CardView(height = CELL_SIZE, width = CELL_SIZE, front = Visual.EMPTY)
                    }
                    else {
                        this[i, j]?.frontVisual = Visual.EMPTY
                        this[i, j]?.backVisual = Visual.EMPTY
                    }
                }
            }
        }
        // placing all tiles
        player.placedTiles.forEach { tile ->
            playerGrid.getCartesian(tile.posX, tile.posY)?.let { cardView ->
                changeCardView(cardView, tile)
                cardView.rotation = tile.rotation.toDouble()
                if (tile.arrows.all { it.occupied }) {
                    cardView.showBack()
                }
                else {
                    cardView.showFront()
                }
            }
        }
    }

    /**
     * updates all components to match their current state in the entity-layer
     */
    fun updateComponents() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        // update playerGrids:
        game.players.forEachIndexed { pInd, player ->
            updatePlayerGrid(playerGridList[pInd], player)
        }
    }

    /**
     * pointsPosMap contains all coordinates for every score on the game-board
     */
    private val step = 33
    val pointsPosMap: BidirectionalMap<Int, Coordinate> = BidirectionalMap(
        (1 to Coordinate(0, 0)),
        (2 to Coordinate(step, 0)),
        (3 to Coordinate(step*2, 0)),
        (4 to Coordinate(step*2, -step)),
        (5 to Coordinate(step*3, -step)),
        (6 to Coordinate(step*4, -step)),
        (7 to Coordinate(step*4, 0)),
        (8 to Coordinate(step*5, 0)),
        (9 to Coordinate(step*6, 0)),
        (10 to Coordinate(step*6, -step)),
        (11 to Coordinate(step*7, -step)),
        (12 to Coordinate(step*8, -step)),
        (13 to Coordinate(step*8, 0)),
        (14 to Coordinate(step*9, 0)),
        (15 to Coordinate(step*10, 0)),
        (16 to Coordinate(step*10, -step)),
        (17 to Coordinate(step*10, -step*2)),
        (18 to Coordinate(step*10, -step*3)),
        (19 to Coordinate(step*10, -step*4)),
        (20 to Coordinate(step*9, -step*4)),
        (21 to Coordinate(step*8, -step*4)),
        (22 to Coordinate(step*8, -step*3)),
        (23 to Coordinate(step*7, -step*3)),
        (24 to Coordinate(step*6, -step*3)),
        (25 to Coordinate(step*6, -step*4)),
        (26 to Coordinate(step*5, -step*4)),
        (27 to Coordinate(step*4, -step*4)),
        (28 to Coordinate(step*4, -step*3)),
        (29 to Coordinate(step*3, -step*3)),
        (30 to Coordinate(step*2, -step*3)),
        (31 to Coordinate(step*2, -step*4)),
        (32 to Coordinate(step, -step*4)),
        (33 to Coordinate(0, -step*4)),
        (34 to Coordinate(0, -step*3)),
        (35 to Coordinate(-step, -step*3)),
        (36 to Coordinate(-step*2, -step*3)),
        (37 to Coordinate(-step*2, -step*4)),
        (38 to Coordinate(-step*2, -step*5)),
        (39 to Coordinate(-step*2, -step*6)),
        (40 to Coordinate(-step*2, -step*7)),
        (41 to Coordinate(-step, -step*7)),
        (42 to Coordinate(0, -step*7)),
        (43 to Coordinate(0, -step*6)),
        (44 to Coordinate(step, -step*6)),
        (45 to Coordinate(step*2, -step*6)),
        (46 to Coordinate(step*2, -step*7)),
        (47 to Coordinate(step*3, -step*7)),
        (48 to Coordinate(step*4, -step*7)),
        (49 to Coordinate(step*4, -step*6)),
        (50 to Coordinate(step*5, -step*6)),
        (51 to Coordinate(step*6, -step*6)),
        (52 to Coordinate(step*6, -step*7)),
        (53 to Coordinate(step*7, -step*7)),
        (54 to Coordinate(step*8, -step*7)),
        (55 to Coordinate(step*8, -step*6)),
        (56 to Coordinate(step*9, -step*6)),
        (57 to Coordinate(step*10, -step*6)),
        (58 to Coordinate(step*10, -step*7)),
        (59 to Coordinate(step*10, -step*8)),
        (60 to Coordinate(step*10, -step*9)),
        (61 to Coordinate(step*10, -step*10)),
        (62 to Coordinate(step*9, -step*10)),
        (63 to Coordinate(step*8, -step*10)),
        (64 to Coordinate(step*8, -step*9)),
        (65 to Coordinate(step*7, -step*9)),
        (66 to Coordinate(step*6, -step*9)),
        (67 to Coordinate(step*6, -step*10)),
        (68 to Coordinate(step*5, -step*10)),
        (69 to Coordinate(step*4, -step*10)),
        (70 to Coordinate(step*4, -step*9)),
        (71 to Coordinate(step*3, -step*9)),
        (72 to Coordinate(step*2, -step*9)),
        (73 to Coordinate(step*2, -step*10)),
        (74 to Coordinate(step, -step*10)),
        (75 to Coordinate(0, -step*10)),
        (76 to Coordinate(0, -step*9)),
        (77 to Coordinate(-step, -step*9)),
        (78 to Coordinate(-step*2, -step*9)),
        (79 to Coordinate(-step*2, -step*10)),
    )

}