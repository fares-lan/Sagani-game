package service

import edu.udo.cs.sopra.ntf.*
import entity.DiscColor
import entity.Player
import entity.PlayerType
import entity.Tile

/** [NetworkService] all needed methods for the connection between game and network
 */
class NetworkService (private val rootService: RootService): AbstractRefreshingService() {

    /** URL of the BGW net server hosted for SoPra participants, Name of the game as registered with the server */
    companion object {
        const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"
        const val GAME_ID = "Sagani"
    }

    /** Network client. Nullable for offline games. */
    var client: SaganiNetworkClient? = null

    /** current state of the connection in a network game. */
    var connectionState: ConnectionState = ConnectionState.DISCONNECTED

/*
    /** [updateConnectionState] updates the [connectionState] to [newState] and notifies all refreshable via
     * [Refreshable.refreshConnectionState] */
    fun updateConnectionState(newState: ConnectionState) {
        this.connectionState = newState
        onAllRefreshables { refreshConnectionState(newState) }
    }

     */

    /** [hostGame] connects to server and creates a new game session.
     * @param secret Server secret.
     * @param name Player name.
     * @param sessionID identifier of the hosted session (to be used by guest on join)
     * @throws IllegalStateException if already connected to another game or connection attempt fails */
    fun hostGame(secret: String, name: String, sessionID: String?) {
        println(secret + ", " +name + ", " + sessionID)
        if (!connect(secret, name)) {
            error("Connection failed")
        }
        this.connectionState = ConnectionState.CONNECTED
        onAllRefreshables { refreshConnectionState( ConnectionState.CONNECTED) }
        //updateConnectionState()

        if (sessionID.isNullOrBlank()) {
            client?.createGame(GAME_ID, "Welcome!")
        } else {
            client?.createGame(GAME_ID, sessionID, "Welcome to Sagani!")
        }
        this.connectionState = ConnectionState.WAITING_FOR_HOST_CONFIRMATION
        onAllRefreshables { refreshConnectionState( ConnectionState.WAITING_FOR_HOST_CONFIRMATION) }
        //updateConnectionState(ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
    }

    /** [joinGame] connects to server and joins a game session as guest player.
     * @param secret Server secret.
     * @param name Player name.
     * @param sessionID identifier of the joined session (as defined by host on create)
     * @throws IllegalStateException if already connected to another game or connection attempt fails*/
    fun joinGame(secret: String, name: String, sessionID: String) {
        if (!connect(secret, name)) {
            error("Connection failed")
        }
        this.connectionState = ConnectionState.CONNECTED
        onAllRefreshables { refreshConnectionState(ConnectionState.CONNECTED) }
        //updateConnectionState(ConnectionState.CONNECTED)

        client?.joinGame(sessionID, "Hello from group 04!")

        this.connectionState = ConnectionState.WAITING_FOR_JOIN_CONFIRMATION
        onAllRefreshables { refreshConnectionState(ConnectionState.WAITING_FOR_JOIN_CONFIRMATION) }
        //updateConnectionState(ConnectionState.WAITING_FOR_JOIN_CONFIRMATION)
    }

    /** Connects to server, sets the [NetworkService.client] if successful and returns `true` on success.
     * @param secret Network secret. Must not be blank (i.e. empty or only whitespaces)
     * @param name Player name. Must not be blank
     * @throws IllegalArgumentException if secret or name is blank
     * @throws IllegalStateException if already connected to another game */
    fun connect(secret: String, name: String): Boolean {
        require(connectionState == ConnectionState.DISCONNECTED && client == null)
        { "already connected to another game" }

        require(secret.isNotBlank()) { "server secret must be given" }
        require(name.isNotEmpty()) { "player name must be given" }
        val newClient =
            SaganiNetworkClient(
                playerName = name,
                host = SERVER_ADDRESS,
                secret = secret,
                networkService = this
            )
        return if (newClient.connect()) {
            this.connectionState = ConnectionState.CONNECTED
            onAllRefreshables { refreshConnectionState(ConnectionState.CONNECTED) }
            //updateConnectionState(ConnectionState.CONNECTED)
            this.client = newClient
            true
        } else {
            false
        }
    }

    /** [disconnect] disconnects the [client] from the server, nulls it and updates the [connectionState] to
     * [ConnectionState.DISCONNECTED]. Can safely be called even if no connection is currently active. */
    fun disconnect() {
        client?.apply {
            if (sessionID != null) leaveGame("Goodbye!")
            if (isOpen) disconnect()
        }
        client = null
        this.connectionState = ConnectionState.DISCONNECTED
        onAllRefreshables { refreshConnectionState(ConnectionState.DISCONNECTED) }
        //updateConnectionState(ConnectionState.DISCONNECTED)
    }

    /** [refreshPlayerJoined] sends a refresh to the view layer */
    fun refreshPlayerJoined(sender: String){
        onAllRefreshables { refreshOnUserJoined(sender) }
    }

    /** [refreshPlayerLeft] sends a refresh to the view layer */
    fun refreshPlayerLeft(sender: String){
        onAllRefreshables { refreshOnUserLeft(sender) }
    }

    /** [startNewHostedGame] starts the Game as an online game. It takes the playerList from GUI
     */
    fun startNewHostedGame(players: List<Player>) {
        println(client!!.playerType)
        check(connectionState == ConnectionState.WAITING_FOR_GUESTS)
        { "currently not prepared to start a new hosted game." }
        val ntfPlayers = mutableListOf<edu.udo.cs.sopra.ntf.Player>()
        for (player in players) {
            val ntfColor:Color =when (player.color) {
                DiscColor.WHITE -> Color.WHITE
                DiscColor.BLACK -> Color.BLACK
                DiscColor.BROWN -> Color.BROWN
                else -> Color.GREY
            }
            ntfPlayers.add(Player(player.name, ntfColor))
        }


        if (client?.playerName == ntfPlayers[0].name) {
            this.connectionState = ConnectionState.PLAYING_MY_TURN
            onAllRefreshables { refreshConnectionState(ConnectionState.PLAYING_MY_TURN) }
            //updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            this.connectionState = ConnectionState.PLAYING_WAITING_FOR_OPPONENTS
            onAllRefreshables { refreshConnectionState(ConnectionState.PLAYING_WAITING_FOR_OPPONENTS) }
            //updateConnectionState(ConnectionState.PLAYING_WAITING_FOR_OPPONENTS)
        }
        rootService.gameService.startGame(players)

        val tiles = (rootService.currentGame!!.offerDisplay + rootService.currentGame!!.drawStack).toMutableList()
        val message = GameInitMessage(ntfPlayers, tiles.map { it.id })

        client?.sendGameActionMessage(message)
        onAllRefreshables { refreshAfterStartGame() }
    }

    /** [startNewJoinedGame] starts a new game when the correct messages received.
     * @param message: [GameInitMessage] that contains all needed parameters like [GameInitMessage.players] and
     * [GameInitMessage.drawPile]
     */
    fun startNewJoinedGame(message: GameInitMessage) {
        check(connectionState == ConnectionState.WAITING_FOR_INIT) { "not waiting for game init message. " }
        val players = mutableListOf<Player>()

        for (player in message.players) {
            val color:DiscColor = when (player.color) {
                Color.WHITE -> DiscColor.WHITE
                Color.BLACK -> DiscColor.BLACK
                Color.BROWN -> DiscColor.BROWN
                else -> DiscColor.GREY
            }
            players.add(
                Player(0, 0, player.name, 24, 0, mutableListOf(),
                    color, PlayerType.HUMAN
                )
            )
            println()
            println(player)
            println()

        }
        players.reversed()
        rootService.gameService.startGame(players)

        rootService.currentGame!!.players.first { it.name == client!!.playerName }.playerType = client!!.playerType!!

        val game = rootService.currentGame!!

        val tiles = (rootService.currentGame!!.offerDisplay + rootService.currentGame!!.drawStack).toMutableList()
        val newDrawPile = mutableListOf<Tile>()
        for (id in message.drawPile) {
            newDrawPile.add(tiles.first { it.id == id })
        }

        game.offerDisplay = newDrawPile.subList(0, 5).toMutableList()
        game.drawStack = newDrawPile.subList(5, 72).toMutableList()
        if (client?.playerName == message.players[0].name) {
            this.connectionState = ConnectionState.PLAYING_MY_TURN
            onAllRefreshables { refreshConnectionState(ConnectionState.PLAYING_MY_TURN) }
            //updateConnectionState(ConnectionState.PLAYING_MY_TURN)
        } else {
            this.connectionState = ConnectionState.PLAYING_WAITING_FOR_OPPONENTS
            onAllRefreshables { refreshConnectionState(ConnectionState.PLAYING_WAITING_FOR_OPPONENTS) }
            //updateConnectionState(ConnectionState.PLAYING_WAITING_FOR_OPPONENTS)
        }
        onAllRefreshables { refreshAfterStartGame() }
    }

    /** [sendTurnMessage] is triggered when we make a move in an online game. creates [TurnMessage] and [TurnChecksum],
     * [TilePlacement] and [MoveType].
     * @param displayType: where we took the tile from; offerDsiplay, intermezzoDisplay or drawPile
     * @param intermezzoBegins: if we triggered a intermezzo, but we no intermezzo move was made
     * @param initiatedLastRound: if we triggered the beginning of the last round by some criteria
     */
    fun sendTurnMessage(displayType: String, intermezzoBegins: Boolean, initiatedLastRound: Boolean) {
        // executed makeMove required, this function will be called by makeMove between the if-else block
        // where hendriks proof from which stack the tile is
        if (rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].name==
            client!!.playerName){
            connectionState = ConnectionState.PLAYING_MY_TURN
            onAllRefreshables { refreshConnectionState(ConnectionState.PLAYING_MY_TURN) }
        }
        require(connectionState == ConnectionState.PLAYING_MY_TURN) { "not your turn" }
        var type = MoveType.SKIP
        when (displayType) {
            "OFFER" -> type = MoveType.OFFER_DISPLAY
            "INTERMEZZO" -> type = MoveType.INTERMEZZO
            "DRAW" -> type = MoveType.DRAW_PILE
        }
        val game = rootService.currentGame!!
        println(rootService.currentGame!!.playerIndex)
        val checkSum = TurnChecksum(
            game.players[game.playerIndex].points, game.players[game.playerIndex].discsLeftCount,
            intermezzoBegins, initiatedLastRound
        )
        val tile = game.players[game.playerIndex].placedTiles.last()
        val orientation: Orientation = when (tile.rotation) {
            0 -> Orientation.NORTH
            90 -> Orientation.EAST
            180 -> Orientation.SOUTH
            270 -> Orientation.WEST
            else -> throw IllegalArgumentException("Invalid orientation value: ${tile.rotation}")
        }
        println()
        println("POSX: "+tile.posX)
        println("POSY: "+tile.posX)
        println()
        val tilePlacement = TilePlacement(tile.id, tile.posX, tile.posY, orientation)
        val messageToSend = TurnMessage(type, tilePlacement, checkSum)
        client?.sendGameActionMessage(messageToSend)

    }

    /** [receivedTurnMessage] gets the message of a move that the current player made, converts the message into
     * a [Tile] and executes [PlayerService.makeMove] with parameters from [TurnMessage.tilePlacement] like
     * [TilePlacement.tileId], [TilePlacement.posX], [TilePlacement.posY] and [TilePlacement.orientation].
     * Also it proofs if the sended message was correct ([TurnChecksum])
     * @param message: information about the move
     */
    fun receivedTurnMessage(message: TurnMessage) {
        if (rootService.currentGame!!.players[rootService.currentGame!!.playerIndex].name!=
            client!!.playerName){
            connectionState = ConnectionState.PLAYING_WAITING_FOR_OPPONENTS
            onAllRefreshables { refreshConnectionState(ConnectionState.PLAYING_WAITING_FOR_OPPONENTS) }
        }
        require(connectionState == ConnectionState.PLAYING_WAITING_FOR_OPPONENTS) { "Not my turn" }
        val selectedTileID = message.tilePlacement!!.tileId
        val selectedTile: Tile? = when (message.type) {
            MoveType.OFFER_DISPLAY -> rootService.currentGame!!.offerDisplay.first { it.id == selectedTileID }
            MoveType.INTERMEZZO -> rootService.currentGame!!.intermezzoDisplay.first { it.id == selectedTileID }
            MoveType.SKIP -> null
            MoveType.DRAW_PILE -> rootService.currentGame!!.drawStack.first()
        }
        //var player = rootService.currentGame!!.players[rootService.currentGame!!.playerIndex]
        val rotation:Int = when (message.tilePlacement!!.orientation) {
            Orientation.NORTH -> 0
            Orientation.EAST -> 90
            Orientation.SOUTH -> 180
            Orientation.WEST -> 270
            else -> throw IllegalArgumentException("Invalid orientation value: ${message.tilePlacement!!.orientation}")
        }
        println(rootService.currentGame!!.playerIndex)
        println()
        println("POSX: "+message.tilePlacement!!.posX)
        println("POSY: "+message.tilePlacement!!.posX)
        println()
        if (message.type != MoveType.SKIP) {
            if (selectedTile != null) {
                rootService.playerService.makeMove(
                    selectedTile, message.tilePlacement!!.posX, message.tilePlacement!!.posY, rotation, "opponent")
            }
        }
        else rootService.playerService.passMoveIntermezzo()
        println(rootService.currentGame!!.playerIndex)
        val player =
            if (rootService.currentGame!!.playerIndex == 0 ){
                rootService.currentGame!!.players[rootService.currentGame!!.players.size-1]
            }
            else{
                rootService.currentGame!!.players[rootService.currentGame!!.playerIndex-1]
            }

        rootService.currentGame!!.players[rootService.currentGame!!.playerIndex]

        // if checks are failing -> disconnect
        check(message.checksum.score == player.points) {
            disconnect()
            println("check(message.checksum.score == player.points)")
        }
        check(message.checksum.availableDiscs == player.discsLeftCount ) {
            disconnect()
            println("check(message.checksum.availableDiscs == player.discsLeftCount )")
        }
        check(player.placedTiles.any { it.id == message.tilePlacement!!.tileId }) {
            disconnect()
            println("check(player.placedTiles.any { it.id == message.tilePlacement!!.tileId })")
        }

        onAllRefreshables { refreshAfterTurnEnds() }
    }
}