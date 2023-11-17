package service

import entity.*
import edu.udo.cs.sopra.ntf.*
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import edu.udo.cs.sopra.ntf.Player as NtfPlayer
import kotlin.test.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** [NetworkServiceTest] is the class we test the [NetworkService]*/
class NetworkServiceTest {

    private lateinit var rootService: RootService
    private lateinit var networkService: NetworkService

    /**
     * This function initializes [rootService] and [networkService] for later use.
     */
    @BeforeEach
    fun setUp() {
        rootService = RootService()
        networkService = NetworkService(rootService)
    }


    /** [testHostGame] test if we can call the server that we want to host a game*/
    @Test
    fun testHostGame() {
        val secret = "23_b_tbd"
        val name = "Player 1"
        val sessionID: String? = null

        networkService.hostGame(secret, name, sessionID)

        assertEquals(ConnectionState.WAITING_FOR_HOST_CONFIRMATION, networkService.connectionState)
    }

    /** [testJoinGame] test if it is possible to join ae game */
    @Test
    fun testJoinGame() {
        val secret = "23_b_tbd"
        val name = "Player 2"
        val sessionID = "session123"

        networkService.joinGame(secret, name, sessionID)

        assertEquals(ConnectionState.WAITING_FOR_JOIN_CONFIRMATION, networkService.connectionState)
    }

    /** [testDisconnect] tests if we can disconnect from the server
     *
     */
    @Test
    fun testDisconnect() {
        networkService.disconnect()

        assertEquals(ConnectionState.DISCONNECTED, networkService.connectionState)
    }


    /** [testConnect_Successful] test if the connection to a server was successful
     *
     */
    @Test
    fun testConnect_Successful() {
        val secret = "23_b_tbd"
        val name = "Player"
        networkService.connectionState = ConnectionState.DISCONNECTED
        val result = networkService.connect(secret, name)

        assertTrue(result)
        assertEquals(ConnectionState.CONNECTED, networkService.connectionState)
    }


    /** [testStartNewHostedGame] tests if its possible to start a game as a host
     */
    @Test
    fun testStartNewHostedGame() {
        val secret = "23_b_tbd"
        val name = "Player 1"
        val sessionID: String? = null

        networkService.hostGame(secret, name, sessionID)
        val players = listOf(
            Player(0,0,"Player 1", 24,0, mutableListOf(),
                DiscColor.GREY, PlayerType.HUMAN),
            Player(0,0,"Player 2", 24,0, mutableListOf(),
                DiscColor.WHITE, PlayerType.HUMAN),
            Player(0,0,"Player 3", 24,0, mutableListOf(),
                DiscColor.BLACK, PlayerType.HUMAN)
        )
        networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS

        networkService.startNewHostedGame(players)

        assertEquals(ConnectionState.PLAYING_MY_TURN, networkService.connectionState)
        // Additional assertions for the expected behavior of the method
    }


    /** [testStartNewJoinedGame] tests if its possible to start a game after receiving the [GameInitMessage]*/
    @Test
    fun testStartNewJoinedGame() {
        val secret = "23_b_tbd"
        val name = "Player 2"
        val sessionID = "session123"
        networkService.joinGame(secret, name, sessionID)

        val players = listOf(
            NtfPlayer("Player 1", Color.WHITE),
            NtfPlayer("Player 2", Color.BLACK),
            NtfPlayer("Player 3", Color.BROWN)
        )

        val fileName = "/tiles_colornames_v2.csv"
        val inputStream = NetworkServiceTest::class.java.getResourceAsStream(fileName)

        val tiles = rootService.fileService.readCSVcreateTiles(inputStream)
        //val message = GameInitMessage(players, tiles.map { tiles -> tiles.id })

        //val drawPile = mutableListOf<Int>()
        //for (i in 0 until 72) drawPile.add(i)
        val message = GameInitMessage(players, (tiles.map { it.id }).toList())
        print(message.drawPile.toString())
        networkService.client!!.playerType = PlayerType.HUMAN
        networkService.connectionState = ConnectionState.WAITING_FOR_INIT
        networkService.startNewJoinedGame(message)
        assertNotNull(rootService.currentGame)
        assertEquals(rootService.currentGame!!.offerDisplay.size, 5)
        assertEquals(rootService.currentGame!!.players.size, players.size)
        assertEquals(ConnectionState.PLAYING_WAITING_FOR_OPPONENTS, networkService.connectionState)
    }

    /** [testRecievedTurnMessage] tests if we can make a move by the [TurnMessage] we got
     */
    @Test
    fun testRecievedTurnMessage(){
        // first host a game to get a client
        val secret = "23_b_tbd"
        val name = "Player 1"
        val sessionID: String? = null
        networkService.hostGame(secret, name, sessionID)
        // creating a players list
        val playerList = mutableListOf(
            Player(0,0, "Player1", 24 ,0, mutableListOf(),
                DiscColor.BLACK, PlayerType.HUMAN),
            Player(0,0, "Bob", 24 ,0, mutableListOf(),
                DiscColor.BLACK, PlayerType.HUMAN)
        )
        //setting network
        networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
        networkService.startNewHostedGame(playerList)
        networkService.connectionState = ConnectionState.PLAYING_WAITING_FOR_OPPONENTS
        //
        var game = rootService.currentGame!!
        val testTile = game.offerDisplay.first()
        // creating a TurnMessage
        val offerMessage = TurnMessage(
            MoveType.OFFER_DISPLAY,
            TilePlacement(testTile.id, 0 , 0, Orientation.NORTH),
            TurnChecksum(playerList[game.playerIndex].points,
                playerList[game.playerIndex].discsLeftCount-testTile.arrows.size,
                startedIntermezzo = false, initiatedLastRound = false
            ))
        val testTileID = testTile.id
        //val testedPlayer = game.players[0]
        networkService.receivedTurnMessage(offerMessage)
        game = rootService.currentGame!!
        assertEquals(testTileID, game.players[0].placedTiles[0].id)
    }

    /** [testSendTurnMessage] is kinda useless, [NetworkService.sendTurnMessage] does not change anything in
     * [NetworkService], so no idea what to test. But it works, whooo
     */
    @Test
    fun testSendTurnMessage(){
        println("test")
        val secret = "23_b_tbd"
        val name = "Player 1"
        val sessionID = Random.nextInt().toString()
        networkService.hostGame(secret, name, sessionID)
        // creating a players list
        val playerList = mutableListOf(
            Player(0,0, "Player1", 24 ,0, mutableListOf(),
                DiscColor.BLACK, PlayerType.HUMAN),
            Player(0,0, "Bob", 24 ,0, mutableListOf(),
                DiscColor.BLACK, PlayerType.HUMAN)
        )
        //setting network
        networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
        networkService.startNewHostedGame(playerList)
        networkService.connectionState = ConnectionState.PLAYING_MY_TURN

        playerList[0].placedTiles.add(rootService.currentGame!!.offerDisplay[0])
        println(rootService.currentGame!!.playerIndex)
        networkService.sendTurnMessage("OFFER" ,false, false)
    }



}