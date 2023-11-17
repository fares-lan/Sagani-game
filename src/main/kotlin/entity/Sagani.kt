package entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data class to represent the Sagani game
 *
 * @constructor Creates a new object of [Sagani]
 * @param startPlayerMarkerIndex Index of the starting player who starts the game   (Value: min 0, max [players].size - 1)
 * @param playerIndex Index of the current player during his turn   (Value: min 0, max [players].size - 1)
 * @param intermezzoMoveCounter Number of players, who have already made a move during Intermezzo
 * (Value: min 0, max [players].size)
 * @param nextState Reference to an object that represents the state of play after the current move
 * @param previousState Reference to an object that represents the state of play before the current move
 * @param offerDisplay The top 5 cards from the draw stack  (Size: min 0, max 5)
 * @param drawStack Stack to draw tiles from   (Size: min 0, max 72)
 * @param intermezzoDisplay Tiles for intermezzo    (Size: min 0, max 5)
 * @param players Players in the current Sagani game    (Size: min 2, max 4)
 * @param simSpeed Game simulation speed for the AI
 */

@Serializable
data class Sagani(
    var startPlayerMarkerIndex: Int,
    var playerIndex: Int,
    var intermezzoMoveCounter: Int = -1,
    @Transient var nextState: Sagani? = null,
    @Transient var previousState: Sagani? = null,
    var offerDisplay: MutableList<Tile>,
    var drawStack: MutableList<Tile>,
    var intermezzoDisplay: MutableList<Tile>,
    var players: List<Player>,
    var simSpeed: SimulationSpeed,
    var lastRound: Boolean = false,
    var startedIntermezzo: Boolean = false,
    var startedLastRound: Boolean = false,
    var intermezzoOrder: MutableList<Int> = (players.indices).toMutableList()
) {
    /**
     * Creating deep copy of Sagani objects.
     * @param prev Calling Sagani object that's automatically set as the previousState in the deep copied obejct.
     * @return Deep copy of the calling Sagani object.
     */
    fun clone(prev: Sagani): Sagani {
        return Sagani(this.startPlayerMarkerIndex,
            this.playerIndex,
            this.intermezzoMoveCounter,
            null,
            prev,
            this.offerDisplay.map { it.clone() }.toMutableList(),
            this.drawStack.map { it.clone() }.toMutableList(),
            this.intermezzoDisplay.map { it.clone() }.toMutableList(),
            this.players.map { it.clone() },
            this.simSpeed,
            this.lastRound,
            this.startedIntermezzo,
            this.startedLastRound,
            this.intermezzoOrder.map { it }.toMutableList()
        )
    }
}