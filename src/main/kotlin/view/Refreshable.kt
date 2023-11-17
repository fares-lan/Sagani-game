package view
import entity.Sagani
import service.ConnectionState

/**
 * Enables implementing classes to define necessary updates in the GUI after certain actions were taken and entity layer
 * changes happened.
 */
interface Refreshable {

    /**
     * Executes necessary refreshes after a new game was started
     */
    fun refreshAfterStartGame() {}

    /**
     * Executes necessary refreshes after a move was made
     */
    fun refreshAfterMakeMove() {}

    /**
     * Executes necessary refreshes before an intermezzo phase
     */
    fun refreshForIntermezzo(){}

    /**
     * Executes necessary refreshes after a game ends
     */
    fun refreshAfterGameEnds() {}

    /**
     * Executes necessary refreshes when the ConnectionState changes
     */

    fun refreshConnectionState(state: ConnectionState) {}

    /**
     * Executes necessary refreshes after a turn ends
     */
    fun refreshAfterTurnEnds() {}

    /**
     * Executes necessary refreshes after a player joined an online Lobby.
     */
    fun refreshOnUserJoined(sender: String){}

    /**
     * Executes necessary refreshes after a player left an online Lobby.
     */
    fun refreshOnUserLeft(sender: String){}

}