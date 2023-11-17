package entity

import kotlinx.serialization.Serializable

/**
 * Enum class to distinguish between the different player types, seen in the game configuration
 *
 * VERY_SLOW / SLOW / FAST  -  Simulation speeds with bots
 * NO_SIM - No simulation speed
 */
@Serializable
enum class SimulationSpeed {
    VERY_SLOW,
    SLOW,
    NO_SIM,
    FAST,
    ;
}