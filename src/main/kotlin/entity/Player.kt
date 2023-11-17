package entity

import kotlinx.serialization.Serializable

/**
 * Data class to represent a player in the Sagani game
 *
 * @constructor Creates a new object of [Player]
 * @param points Number of points scored by a player per game
 * @param pointsOrder Order of the player especially for the Intermezzo  (Value: min 0, max 3)
 * @param name Player name
 * @param discsLeftCount Number of unused discs of a player's colour remaining  (Value: min 0, max 24)
 * @param cacoCount Number of used caco discs
 * @param placedTiles List of tiles placed on the player's field
 * @param color A player's colour that identifies him   (Color: GREY / WHITE / BLACK / BROWN)
 * @param playerType Player type: AI or Human
 */
@Serializable
data class Player(
    var points: Int = 0,
    var pointsOrder: Int = 0,
    var name: String,
    var discsLeftCount: Int = 24,
    var cacoCount: Int = 0,
    var placedTiles: MutableList<Tile>,
    var color: DiscColor,
    var playerType: PlayerType,
) {
    /**
     * Creating deep copy of the calling Player objects.
     * @return Deep copy of the calling player object.
     */
    fun clone(): Player {
        return Player(
            this.points,
            this.pointsOrder,
            this.name,
            this.discsLeftCount,
            this.cacoCount,
            this.placedTiles.map { it.clone() }.toMutableList(),
            this.color,
            this.playerType
        )
    }
}