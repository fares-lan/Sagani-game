package entity

import kotlinx.serialization.Serializable

/**
 * Enum class to distinguish between the different player types, seen in the game configuration
 *
 * RANDOM_AI -  Move of the AI is determined randomly
 * SMART_AI - AI calculates the best possible moves
 * HUMAN - Player is not a bot
 */
@Serializable
enum class PlayerType {
    RANDOM_AI,
    SMART_AI,
    HUMAN,
    ;
}