package service


import entity.*
import kotlin.math.abs

/**
 * GameService provides the necessary functionalities to run a game of Sagani. The actions taken in this class may be
 * triggered by a player but are not player turn specific moves.
 *
 * @property rootService where the current Sagani game state is saved and functions as a link
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

	/**
	 * Main function to start a game and initialise the first state of a [Sagani] object.
	 *
	 * @param players Is the list of players that play locally via hotseat-mode or via network. players.size = [2..4]
	 */
	fun startGame(players: List<Player>, fileName: String = "/tiles_colornames_v2.csv") {

		val inputStream = GameService::class.java.getResourceAsStream(fileName)
		checkNotNull(inputStream) { "Correct resource for tile creation needed" }
		val tiles = rootService.fileService.readCSVcreateTiles(inputStream)
		tiles.shuffle()

		rootService.currentGame = Sagani(
			startPlayerMarkerIndex = 0,
			playerIndex = 0,
			intermezzoMoveCounter = -1,
			nextState = null,
			previousState = null,
			offerDisplay = tiles.subList(0, 5).toMutableList(),
			drawStack = tiles.subList(5, 72).toMutableList(),
			intermezzoDisplay = mutableListOf(),
			players = players,
			simSpeed = SimulationSpeed.VERY_SLOW
		)

		// change game state once before the game starts
		val game = rootService.currentGame
		checkNotNull(game) { "There is no ongoing game!" }

		// call Refreshables
		onAllRefreshables { refreshAfterStartGame() }
	}

	/**
	 * Calculates the points of a specific player according if all arrows of a tile in his placedTiles list are occupied
	 * @param player Object for which the points should be calculated and returned.
	 */
	fun calculatePoints(player: Player): Int {
		var points = 0
		val predicate: (Arrow) -> Boolean = { it.occupied }
		for (tile in player.placedTiles) if (tile.arrows.all(predicate)) points += tile.points
		return points - player.cacoCount * 2
	}

	/**
	 * Calculates the order in which players achieved equal points.
	 * Lower means earlier. 0 = first, 1 = second, ...
	 *
	 * @param player object for which the pointsOrder should be calculated
	 */
	fun calcuatePointsOrder(player: Player) {
		val game = rootService.currentGame
		checkNotNull(game) { "There is no ongoing game!" }
		val samePoints = game.players.filter { it.points == player.points }
		player.pointsOrder = (samePoints.size - 1)
	}

	/**
	 * Calculates the Intermezzo-order by sorting the players by their amount of points
	 * and their pointsOrder.
	 */
	fun calculateIntermezzoOrder() {
		val game = rootService.currentGame
		checkNotNull(game)
		// convert players list to mutable
		val mutablePlayers = game.players.map { it.clone() }.toMutableList()
		// create the intermezzoOrder list we're working with
		val intermezzoOrder = mutableListOf<Int>()
		// sort the mutablePlayers primarily by points and secondarily by pointsOrder
		mutablePlayers.sortWith(compareBy<Player> { it.points }.thenBy { it.pointsOrder })
		// get the index of the sorted player objects in game.players
		for (i in 0 until mutablePlayers.size)
			intermezzoOrder.add(game.players.indexOf(mutablePlayers[i]))
		game.intermezzoOrder = intermezzoOrder
	}

	/**
	 * Assumption: Cartesian coordinates. First placed tile sets the origin of (0,0).
	 * Methods checks if an arrow is occupied by a sounddisc by checking in the direction the arrow is pointing.
	 * Accounting for rotation of the tile.
	 *
	 * @param player which placed the tile
	 * @param tile that the arrow we're checking is located on
	 * @param arrow that we're checking if it's condition is met and therefor occupied by sounddisc
	 * @return boolean value, true -> the arrow is occupied by a sounddisc
	 */
	fun checkOccupied(player: Player, tile: Tile, arrow: Arrow): Boolean {

		val tiles = player.placedTiles

		if (invalidTile(player, tile)) return false

		// variable that contains the orientation of the arrow
		val actOrientation = adjustForRotation(tile, arrow)

		//if (tile.rotation % 360 != 0) actOrientation = adjustForRotation(tile, arrow)

		return when (actOrientation) {
			TileArrow.TOP -> tiles.filter { it.spirit == arrow.spirit }.any {
				vertOrHoriz(it.posX, tile.posX, it.posY, tile.posY, TileArrow.TOP)
			}

			TileArrow.TOPRIGHT -> tiles.filter { it.spirit == arrow.spirit }.any {
				confirmQuadrant(it.posX, tile.posX, it.posY, tile.posY, 2) &&
						isDiagonal(tile.posX, it.posX, tile.posY, it.posY)
			}

			TileArrow.RIGHT -> tiles.filter { it.spirit == arrow.spirit }.any {
				vertOrHoriz(it.posX, tile.posX, it.posY, tile.posY, TileArrow.RIGHT)
			}

			TileArrow.BOTTOMRIGHT -> tiles.filter { it.spirit == arrow.spirit }.any {
				confirmQuadrant(it.posX, tile.posX, it.posY, tile.posY, 3) &&
						isDiagonal(tile.posX, it.posX, tile.posY, it.posY)
			}

			TileArrow.BOTTOM -> tiles.filter { it.spirit == arrow.spirit }.any {
				vertOrHoriz(it.posX, tile.posX, it.posY, tile.posY, TileArrow.BOTTOM)
			}

			TileArrow.BOTTOMLEFT -> tiles.filter { it.spirit == arrow.spirit }.any {
				confirmQuadrant(it.posX, tile.posX, it.posY, tile.posY, 4) &&
						isDiagonal(tile.posX, it.posX, tile.posY, it.posY)
			}

			TileArrow.LEFT -> tiles.filter { it.spirit == arrow.spirit }.any {
				vertOrHoriz(it.posX, tile.posX, it.posY, tile.posY, TileArrow.LEFT)
			}

			TileArrow.TOPLEFT -> tiles.filter { it.spirit == arrow.spirit }.any {
				confirmQuadrant(it.posX, tile.posX, it.posY, tile.posY, 1) &&
						isDiagonal(tile.posX, it.posX, tile.posY, it.posY)
			}
		}
	}

	/**
	 * Checks if the tiles of the given coordinates are on either of the main diagonals
	 */
	private fun isDiagonal(posX1: Int, posX2: Int, posY1: Int, posY2: Int): Boolean {
		return (abs(posX1 - posX2).toDouble() / abs(posY1 - posY2).toDouble() == 1.0)
	}

	private fun vertOrHoriz(posX1: Int, posX2: Int, posY1: Int, posY2: Int, ori: TileArrow): Boolean {
		return when (ori) {
			TileArrow.TOP -> (posX1 == posX2 && posY1 > posY2)
			TileArrow.BOTTOM -> (posX1 == posX2 && posY1 < posY2)
			TileArrow.LEFT -> (posX1 < posX2 && posY1 == posY2)
			TileArrow.RIGHT -> (posX1 > posX2 && posY1 == posY2)
			else -> false
		}
	}

	private fun invalidTile(player: Player, tile: Tile): Boolean {
		val tiles = player.placedTiles
		return (tiles.isEmpty() || tile !in tiles)
	}

	/**
	 * Considering the tile that is checked for occupied arrows as the "new" cartesian origin. This method checks
	 * if the tile is located in the cartesian quadrant (read: direction) required by the arrow that's checked.
	 */
	private fun confirmQuadrant(posItX1: Int, posX2: Int, posItY1: Int, posY2: Int, quadrant: Int): Boolean {
		return when (quadrant) {
			1 -> (posItX1 < posX2 && posItY1 > posY2)
			2 -> (posItX1 > posX2 && posItY1 > posY2)
			3 -> (posItX1 > posX2 && posItY1 < posY2)
			4 -> (posItX1 < posX2 && posItY1 < posY2)
			else -> false
		}
	}

	/**
	 * 'Changes' the orientation that we're looking for in the placed tiles by accounting for the rotation
	 * of the tile that the arrow is on. Example: if the tile is rotated by 180 degrees, an arrow that has the
	 * orientation arrow.orientation = TileArrow.TOP now faces BOTTOM (= actOrientation).
	 *
	 * @param tile that's rotated
	 * @param arrow whose actual orientation (due to rotation) we are looking for
	 */
	private fun adjustForRotation(tile: Tile, arrow: Arrow): TileArrow {
		var actOrientation = arrow.orientation
		if (tile.rotation % 360 != 0) {
			val orientationList = listOf(
				TileArrow.TOP,
				TileArrow.TOPRIGHT,
				TileArrow.RIGHT,
				TileArrow.BOTTOMRIGHT,
				TileArrow.BOTTOM,
				TileArrow.BOTTOMLEFT,
				TileArrow.LEFT,
				TileArrow.TOPLEFT
			)
			when (tile.rotation % 360) {
				90 -> actOrientation =
					orientationList[(orientationList.indexOf(actOrientation) + 2) % orientationList.size]

				180 -> actOrientation =
					orientationList[(orientationList.indexOf(actOrientation) + 4) % orientationList.size]

				270 -> actOrientation =
					orientationList[(orientationList.indexOf(actOrientation) + 6) % orientationList.size]
			}
		}
		return actOrientation
	}

}