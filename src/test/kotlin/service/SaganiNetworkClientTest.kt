package service

import edu.udo.cs.sopra.ntf.*
import entity.*
import org.junit.jupiter.api.BeforeEach
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.notification.PlayerLeftNotification
import tools.aqua.bgw.net.common.response.*
import kotlin.test.*

/** [SaganiNetworkClientTest] tests the [SaganiNetworkClient] */
class SaganiNetworkClientTest {

/*
    private lateinit var rootService: RootService
    private lateinit var networkService: NetworkService
    @BeforeEach
    fun setUp() {
        rootService = RootService()
        networkService = NetworkService(rootService)
    }

    /**
     * [testOnCreateGameResponse] tests if a [CreateGameResponse] message from the server is working likes wished.
     * for that we created an own message, so we do not need a active connection.
     */
    @Test
    fun testOnCreateGameResponse(){
        networkService.client = SaganiNetworkClient(
                playerName = "join",
                host = NetworkService.SERVER_ADDRESS,
                secret = "23_b_tbd",
                networkService = this.networkService
            )
        val createGameResponseSuccess = CreateGameResponse(CreateGameResponseStatus.SUCCESS, "testClient")
        networkService.connectionState = ConnectionState.WAITING_FOR_HOST_CONFIRMATION
        networkService.client!!.onCreateGameResponse(createGameResponseSuccess)

        assertEquals(networkService.client!!.sessionID, createGameResponseSuccess.sessionID)
        assertEquals(networkService.connectionState, ConnectionState.WAITING_FOR_GUESTS)
    }


    /**
     * [testOnJoinGameResponse] tests if a [JoinGameResponse] message from the server is working likes wished.
     * for that we created an own message, so we do not need a active connection.
     */
    @Test
    fun testOnJoinGameResponse(){
        networkService.client = SaganiNetworkClient(
            playerName = "join",
            host = NetworkService.SERVER_ADDRESS,
            secret = "23_b_tbd",
            networkService = this.networkService
        )
        val joinGameResponse = JoinGameResponse(
            JoinGameResponseStatus.SUCCESS, "testClient", listOf(), "test")
        networkService.connectionState = ConnectionState.WAITING_FOR_JOIN_CONFIRMATION
        networkService.client!!.onJoinGameResponse(joinGameResponse)

        assertEquals(networkService.connectionState, ConnectionState.WAITING_FOR_INIT)
        assertEquals(networkService.client!!.sessionID, joinGameResponse.sessionID)

    }

    /**
     * [testOnPlayerJoined] tests if a [PlayerJoinedNotification] message from the server is working likes wished.
     * for that we created an own message, so we do not need a active connection.
     */
    @Test
    fun testOnPlayerJoined(){
        networkService.client = SaganiNetworkClient(
            playerName = "join",
            host = NetworkService.SERVER_ADDRESS,
            secret = "23_b_tbd",
            networkService = this.networkService
        )
        val playerJoinedNotification = PlayerJoinedNotification("test", "otherPlayer")
        networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
        networkService.client!!.onPlayerJoined(playerJoinedNotification)
        println(networkService.client!!.otherPlayerNames)
        assertTrue { networkService.client!!.otherPlayerNames!!.any { it == playerJoinedNotification.sender } }
    }

    /**
     * [testOnPlayerLeft] tests if a [PlayerLeftNotification] message from the server is working likes wished.
     * for that we created an own message, so we do not need a active connection.
     */
    @Test
    fun testOnPlayerLeft(){
        networkService.client = SaganiNetworkClient(
            playerName = "join",
            host = NetworkService.SERVER_ADDRESS,
            secret = "23_b_tbd",
            networkService = this.networkService
        )
        val playerLeftNotification = PlayerLeftNotification("test", "otherPlayer")
        networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
        networkService.client!!.otherPlayerNames = mutableListOf("otherPlayer")
        networkService.client!!.onPlayerLeft(playerLeftNotification)
        println(networkService.client!!.otherPlayerNames)
        assertFalse { networkService.client!!.otherPlayerNames!!.any { it == playerLeftNotification.sender } }
    }

    /**
     * [testOnJoinedGameInitReceived] tests if a [GameInitMessage] message from the server is working likes wished.
     * for that we created an own message, so we do not need a active connection.
     */
    @Test
    fun testOnJoinedGameInitReceived(){
        val secret = "23_b_tbd"
        val name = "Player 2"
        val sessionID = "session123"
        networkService.joinGame(secret, name, sessionID)

        val players = listOf(
            edu.udo.cs.sopra.ntf.Player("Player 1", Color.WHITE),
            edu.udo.cs.sopra.ntf.Player("Player 2", Color.BLACK),
            edu.udo.cs.sopra.ntf.Player("Player 3", Color.BROWN)
        )

        val fileName = "/tiles_colornames_v2.csv"
        val inputStream = NetworkServiceTest::class.java.getResourceAsStream(fileName)

        val tiles = rootService.fileService.readCSVcreateTiles(inputStream)
        //val message = GameInitMessage(players, tiles.map { tiles -> tiles.id })

        //val drawPile = mutableListOf<Int>()
        //for (i in 0 until 72) drawPile.add(i)
        val message = GameInitMessage(players, (tiles.map { it.id }).toList())
        print(message.drawPile.toString())
        networkService.updateConnectionState(ConnectionState.WAITING_FOR_INIT)
        networkService.client!!.onJoinedGameInitReceived(message, "Player 2")
        assertNotNull(rootService.currentGame)
        assertEquals(rootService.currentGame!!.offerDisplay.size, 5)
        assertEquals(rootService.currentGame!!.players.size, players.size)
        assertEquals(ConnectionState.PLAYING_WAITING_FOR_OPPONENTS, networkService.connectionState)
    }

    /**
     * [testOnTurnMessageRecieved] tests if a [TurnMessage] message from the server is working likes wished.
     * for that we created an own message, so we do not need a active connection.
     */
    @Test
    fun testOnTurnMessageRecieved(){
        val secret = "23_b_tbd"
        val name = "Player 1"
        val sessionID: String? = null
        networkService.hostGame(secret, name, sessionID)
        // creating a players list
        val playerList = mutableListOf(
            entity.Player(0,0, "Player1", 24 ,0, mutableListOf(),
                DiscColor.BLACK, PlayerType.HUMAN),
            entity.Player(0,0, "Bob", 24 ,0, mutableListOf(),
                DiscColor.BLACK, PlayerType.HUMAN)
        )
        //setting network
        networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
        networkService.startNewHostedGame(playerList)
        networkService.connectionState = ConnectionState.PLAYING_WAITING_FOR_OPPONENTS
        //
        val game = rootService.currentGame!!
        val testTile = game.offerDisplay[0]
        // creating a TurnMessage
        val offerMessage = TurnMessage(
            MoveType.OFFER_DISPLAY,
            TilePlacement(testTile.id, 0 , 0, Orientation.NORTH),
            TurnChecksum(playerList[0].points, playerList[0].discsLeftCount-testTile.arrows.size,
                startedIntermezzo = false, initiatedLastRound = false
            )
        )
        val testTileID = testTile.id
        val testedPlayer = game.players[0]
        networkService.client!!.onTurnMessageReceived(offerMessage, "Player 1")
        assertEquals(testTileID, testedPlayer.placedTiles[0].id)
    }

 */

}