package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TurnMessage
import entity.PlayerType
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.client.NetworkLogging
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.notification.PlayerLeftNotification
import tools.aqua.bgw.net.common.response.*

/** [SaganiNetworkClient] is the client that provides the connection between you and the server^.
 */
class SaganiNetworkClient(playerName: String, host: String, secret: String, var networkService: NetworkService)
    : BoardGameClient( playerName = playerName, host, secret, NetworkLogging.VERBOSE) {

    /** the identifier of this game session; can be null if no session started yet. */
    var sessionID: String? = null

    /** the name of the opponent players; can be null if no message from the opponents received yet */
    var otherPlayerNames: MutableList<String>? = null

    var playerType:PlayerType? = null

    /** Handle a [CreateGameResponse] sent by the server. Will await the guest players when its
     * status is [CreateGameResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented in Sagani, the method disconnects from the server and throws an
     * [IllegalStateException] otherwise.
     * @throws IllegalStateException if status != success or currently not waiting for a game creation response. */
    @Suppress("unused")
    override fun onCreateGameResponse(response: CreateGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
            { "unexpected CreateGameResponse" }

            when (response.status) {
                CreateGameResponseStatus.SUCCESS -> {
                    networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
                    sessionID = response.sessionID
                }
                else -> disconnectAndError(response.status)
            }
        }
    }

    /** Handle a [JoinGameResponse] sent by the server. Will await the init message when its
     * status is [JoinGameResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented in NetWar, the method disconnects from the server and throws an
     * [IllegalStateException] otherwise.
     * @throws IllegalStateException if status != success or currently not waiting for a join game response. */
    override fun onJoinGameResponse(response: JoinGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_JOIN_CONFIRMATION)
            { "unexpected JoinGameResponse" }

            when (response.status) {
                JoinGameResponseStatus.SUCCESS -> {
                    sessionID = response.sessionID
                    networkService.connectionState=ConnectionState.WAITING_FOR_INIT
                }
                else -> disconnectAndError(response.status)
            }
        }
    }
    /** Handle a [PlayerJoinedNotification] sent by the server and send the init message to the opponent.
     * In Sagani we have 2 to 4 players, so we need to wait until all have joined and then
     * start the startNewHostedGame
     * @throws IllegalStateException if not currently expecting any guests to join.
     */
    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_GUESTS )
            { "not awaiting any guests."}
            if (otherPlayerNames == null){
                otherPlayerNames = mutableListOf()
            }
            otherPlayerNames?.add(notification.sender)
            networkService.refreshPlayerJoined(notification.sender)
        }

    }

    override fun onPlayerLeft(notification: PlayerLeftNotification) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_GUESTS )
            { "not awaiting any guests."}
            otherPlayerNames?.remove(notification.sender)
            networkService.refreshPlayerLeft(notification.sender)
        }
    }

    /** Handle a [GameActionResponse] sent by the server. Does nothing when its
     * status is [GameActionResponseStatus.SUCCESS]. As recovery from network problems is not
     * implemented in Sagani, the method disconnects from the server and throws an
     * [IllegalStateException] otherwise. */
    override fun onGameActionResponse(response: GameActionResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.PLAYING_MY_TURN ||
                    networkService.connectionState == ConnectionState.PLAYING_WAITING_FOR_OPPONENTS)
            { "not currently playing in a network game."}

            when (response.status) {
                GameActionResponseStatus.SUCCESS -> {} // do nothing in this case
                else -> disconnectAndError(response.status)
            }
        }
    }
    /** Handles the received GameInitMessage when joining a game.
     * @param message The received GameInitMessage.*/
    @Suppress("UNUSED_PARAMETER", "unused")
    @GameActionReceiver
    fun onJoinedGameInitReceived(message: GameInitMessage, sender: String) {
        BoardGameApplication.runOnGUIThread {
            networkService.startNewJoinedGame(
                message = message
            )
        }
    }

    /** Handles the received TurnMessage from the server.
     * @param message The received TurnMessage.*/
    @Suppress("unused")
    @GameActionReceiver
    fun onTurnMessageReceived(message: TurnMessage, sender: String) {
        BoardGameApplication.runOnGUIThread {
            //val receivedTurnMessage = message
            networkService.receivedTurnMessage(message)
        }
    }


    /** Disconnects from the server and throws an error with the specified message.
     * @param message The error message. */
    private fun

            disconnectAndError(message: Any) {
        networkService.disconnect()
        error(message)
    }
}