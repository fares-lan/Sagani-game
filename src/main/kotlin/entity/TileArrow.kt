package entity

import kotlinx.serialization.Serializable

/**
 * Enum class to distinguish between the arrow directions on the tile
 */
@Serializable
enum class TileArrow {

    TOP,
    TOPRIGHT,
    RIGHT,
    BOTTOMRIGHT,
    BOTTOM,
    BOTTOMLEFT,
    LEFT,
    TOPLEFT,
    ;
}