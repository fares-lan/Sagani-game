package entity

import kotlinx.serialization.Serializable

/**
 * Enum class to distinguish between the different player colors, seen in the sound discs and the player markers, as
 * well as in the cacophony discs.
 */
@Serializable
enum class DiscColor {
    GREY,
    WHITE,
    BLACK,
    BROWN,
    RED,
    ;

    /**
     * Returns the according string of the sound/cacophony disc or the player markers
     */
    override fun toString() = when(this) {
        GREY -> "grey"
        WHITE -> "white"
        BLACK -> "black"
        BROWN -> "brown"
        RED -> "RED"
    }
}