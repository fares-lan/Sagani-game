package entity

import kotlinx.serialization.Serializable



/**
 * Enum class to distinguish between the spirits, seen on the tiles
 */

@Serializable
enum class TileSpirit {
    FIRE,
    AIR,
    WATER,
    EARTH,
    ;
}