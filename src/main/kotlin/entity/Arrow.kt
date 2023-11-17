package entity

import kotlinx.serialization.Serializable

/**
 * Data class to represent an arrow on the tiles in the Sagani game
 *
 * @constructor Creates a new object of [Arrow]
 * @param occupied Variable which shows whether the arrow is covered
 * @param orientation Orientation of an arrow on the tile
 * @param spirit 1 of 4 possible spirits for the arrow to cover it later
 */
@Serializable
data class Arrow(var occupied: Boolean, var orientation: TileArrow, val spirit: TileSpirit)