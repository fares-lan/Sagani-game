package entity

import kotlinx.serialization.Serializable

/**
 * Data class to represent a tile in the Sagani game
 *
 * @constructor Creates a new object of [Tile]
 * @param points Number of points for all coverage of all arrows on the tile     (Value: min 1, max 10)
 * @param posX Position of the tile in the X-axis on the player's field
 * @param posY Position of the tile in the Y-axis on the player's field
 * @param rotation Indicator of how many degrees the tile is rotated     (Value: min 0, max 270)
 * @param soundDiscs List of coloured discs to cover all arrows on the tile
 * (Color: RED / Color of player, Size: min 0, max 4)
 * @param arrows List of arrows on the tile, that must be covered with discs    (Size: min 0, max 4)
 * @param spirit 1 of 4 possible spirits for the tile
 */
@Serializable
data class Tile(
    val id: Int,
    var points: Int,
    var posX: Int,
    var posY: Int,
    var rotation: Int,
    var soundDiscs: MutableList<DiscColor>,
    var arrows: List<Arrow>,
    val spirit: TileSpirit,
) {
    /**
     * Creating a deep copy of Tile objects.
     * @return Deep copy of the calling Tile object.
     */
    fun clone(): Tile {
        return Tile(
            this.id,
            this.points,
            this.posX,
            this.posY,
            this.rotation,
            this.soundDiscs.map { it }.toMutableList(),
            this.arrows.map { it.copy() },
            this.spirit
        )
    }
}