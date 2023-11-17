package view

import entity.DiscColor
import entity.Player
import entity.PlayerType
import service.ConnectionState
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.schedule

/** [HostGameMenuScene] shows you the possible options you as a host have. Here you can edit the lobby name, the
 * player name and the playerColor
 * @param rootService
 */
class HostGameMenuScene (private val rootService: RootService) : MenuScene(1920, 1080), Refreshable  {

    private val players = mutableListOf<String>()

    //headlines:
    private val nameLabel = Label(
        width = 300, height = 35, posX = 285, posY = 70,
        visual = TextVisual("Player names:", Font(
            size = 22,
            color = Color.WHITE
        ))
    )
    private val typeLabel = Label(
        width = 200, height = 35, posX = 685, posY = 70,
        visual = TextVisual("Player types:", Font(
            size = 22,
            color = Color.WHITE
        ))
    )
    private val colourLabel = Label(
        width = 200, height = 35, posX = 985, posY = 70,
        visual = TextVisual("Player colours:", Font(
            size = 22,
            color = Color.WHITE
        ))
    )
    private val orderLabel = Label(
        width = 200, height = 35, posX = 1215, posY = 70,
        visual = TextVisual("Player order:", Font(
            size = 22,
            color = Color.WHITE
        ))
    )

    private val p1Name: TextField = TextField(
        width = 300, height = 35, posX = 285, posY = 170, text = "playername1", font = Font(22)
    )
    private val p2Name: Label = Label(
        width = 300, height = 35, posX = 285, posY = 370,
        visual = TextVisual("", Font(
            size = 22,
            color = Color.WHITE
        )),
        alignment = Alignment.CENTER_LEFT)

    private val p3Name: Label = Label(
        width = 300, height = 35, posX = 285, posY = 570,
        visual = TextVisual("", Font(
            size = 22,
            color = Color.WHITE
        )),
        alignment = Alignment.CENTER_LEFT)

    private val p4Name: Label = Label(
        width = 300, height = 35, posX = 285, posY = 770,
        visual = TextVisual("", Font(
            size = 22,
            color = Color.WHITE
        )),
        alignment = Alignment.CENTER_LEFT)

    private val playerLabels = mutableListOf<Label>(p2Name,p3Name,p4Name)

    private val secret: TextField = TextField(
        width = 300, height = 35, posX = 1520, posY = 370, text = "23_b_tbd", font = Font(22)
    )
    private val sessionId: TextField = TextField(
        width = 300, height = 35, posX = 1520, posY = 170, text = "sessionId", font = Font(22)
    )

    private val p1to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 170, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
    }
    private val p2to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 370, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
    }
    private val p3to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 570, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
    }
    private val p4to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 770, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
    }

    //player colours:
    private val p1Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 170,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val p2Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 370,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val p3Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 570,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val p4Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 770,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(83,215,216)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }

    //player type:
    private val pt1 = ComboBox<PlayerType>(
        width = 220, height = 35, posX = 685, posY = 170,
        items = listOf(PlayerType.HUMAN, PlayerType.SMART_AI, PlayerType.RANDOM_AI), font = Font(22),
        prompt = "Select an option!"
    ).apply {
        visual = ColorVisual(83,215,216)
    }

    private val warningLabel = Label(
        width = 588, height = 60,
        posX = 666, posY = 120,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )


    val backButton : Button = Button(
        width = 280,
        height = 70,
        posX = 120,
        posY = 905,
        text = "Back",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(83,215,216,100)
        backgroundStyle = "-fx-background-color: rgba(83,215,216, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    private val createGameButton: Button = Button(
        width = 280,
        height = 70,
        posX = 920,
        posY = 905,
        text = "Create Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(83,215,216,100)
        backgroundStyle = "-fx-background-color: rgba(83,215,216, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
        onMouseClicked = {
            rootService.networkService.hostGame(secret.text,p1Name.text,sessionId.text)
            rootService.networkService.client!!.playerType = pt1.selectedItem!!
            //rootService.currentGame!!.players.first{it.name == p1Name.text}.playerType = pt1.selectedItem!!
    }
        }

    private val cancelButton : Button = Button(
        width = 280,
        height = 70,
        posX = 520,
        posY = 905,
        text = "Cancel",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(83,215,216,100)
        isVisible = false
        backgroundStyle = "-fx-background-color: rgba(83,215,216, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
        onMouseClicked = {
            rootService.networkService.disconnect()
        }
    }

    private val startButton: Button = Button(
        width = 280,
        height = 70,
        posX = 1520,
        posY = 905,
        text = "Start",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(83,215,216,100)
        backgroundStyle = "-fx-background-color: rgba(83,215,216, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"

        onMouseEntered = {
            checkColors()
        }

        onMouseClicked = {
            val playerInfo = mutableListOf<Triple<String, DiscColor?, Int?>>()
            if (p1Name.text != "") {
                playerInfo.add(Triple(p1Name.text, p1Color.selectedItem, p1to.selectedItem))
            }
            if (p2Name.text != "") {
                playerInfo.add(Triple(p2Name.text, p2Color.selectedItem, p2to.selectedItem))
            }
            if (p3Name.text != "") {
                playerInfo.add(Triple(p3Name.text, p3Color.selectedItem, p3to.selectedItem))
            }
            if (p4Name.text != "") {
                playerInfo.add(Triple(p4Name.text, p4Color.selectedItem, p4to.selectedItem))
            }
            try {
                rootService.networkService.startNewHostedGame(createPlayers(orderingPlayers(colorToPlayer(playerInfo))))
                rootService.currentGame!!.players.first{it.name == p1Name.text}.playerType = pt1.selectedItem!!
            }
            catch (e: IllegalStateException) {
                e.message?.let { warning(it) }
            }
        }
    }

    /**
     * displays warning message in warningLabel for 5 seconds.
     * @param text String of message
     */

    private fun warning(text: String) {
        warningLabel.isVisible = true
        warningLabel.text = text
        Timer().schedule(5000) {
            warningLabel.isVisible = false
        }
    }

    /**
     * adds selected player color to the player, if none given adds a random remaining one
     */
    private fun colorToPlayer(players: List<Triple<String, DiscColor?, Int?>>): List<Triple<String, DiscColor, Int?>> {
        val ctp = mutableListOf<Triple<String, DiscColor, Int?>>()
        val colors = mutableListOf(DiscColor.BROWN, DiscColor.BLACK, DiscColor.GREY, DiscColor.WHITE)
        val colorOptions = mutableListOf(DiscColor.BROWN, DiscColor.BLACK, DiscColor.GREY, DiscColor.WHITE)
        for (i in players) {
            for (j in colors) {
                if (i.second == j) {
                    ctp.add(Triple(i.first, j, i.third))
                    colorOptions.remove(j)
                }
            }
        }
        colors.shuffle()
        for (i in players) {
            if (i.second == null) {
                ctp.add(Triple(i.first, colorOptions[0], i.third))
                colorOptions.removeFirst()
            }
        }


        return ctp
    }

    /**
     * orders the player, prioritises selected position first
     * @param players Triple of name, DiscColor and potential turn order
     * @return returns the given list, without the nullable option for turn order
     */

    private fun orderingPlayers(players: List<Triple<String, DiscColor, Int?>>): List<Triple<String, DiscColor, Int>> {
        val op = mutableListOf<Triple<String, DiscColor, Int>>()
        val int = mutableListOf<Int>(1,2,3,4)
        val intOptions = mutableListOf<Int>(1,2,3,4)
        for (i in players) {
            for (j in int){
            if (i.third == j) {
                op.add(Triple(i.first, i.second, j))
                intOptions.remove(j)
            }}
        }
        op.shuffle()
        for (i in players){
            if (i.third == null){
                op.add(Triple(i.first,i.second,int[0]))
                intOptions.removeFirst()
            }
        }
        op.sortBy { it.third }
        return op
    }

    /**
     * checks if multiple players have the same color, if so disables the start button
     */

    private fun checkColors() {
        val colorsList = mutableListOf<DiscColor?>()
        if (p1Name.text != "" && p1Color.selectedItem != null) {
            colorsList.add(p1Color.selectedItem)
        }
        if (p2Name.text != "" && p2Color.selectedItem != null) {
            colorsList.add(p2Color.selectedItem)
        }

        if (p3Name.text != "" && p3Color.selectedItem != null) {
            colorsList.add(p3Color.selectedItem)
        }

        if (p4Name.text != "" && p4Color.selectedItem != null) {
            colorsList.add(p4Color.selectedItem)
        }
        startButton.isDisabled = false

        for (i in 0 until colorsList.size - 1) {
            for (j in i + 1 until colorsList.size) {
                if (colorsList[i] == colorsList[j]) {
                    startButton.isDisabled = true
                }
            }
        }
    }

    /**
     * creates players to start the game with adds the [PlayerType]
     * @param players consists of the name, discColor and turnorder
     * @return returns the finished List of Players to start the game with
     */

    private fun createPlayers(players: List<Triple<String, DiscColor, Int>>): List<Player> {
        checkNotNull(pt1.selectedItem)
        var order = 0
        val playerList = mutableListOf<Player>()
        for (i in players) {
            if (i.first == p1Name.text) {
                playerList.add(Player(0,order++,i.first,24,0, mutableListOf(),
                    i.second,pt1.selectedItem as PlayerType))
            }
            if (i.first == p2Name.text) {
                playerList.add(Player(0,order++,i.first,24,0, mutableListOf(),
                    i.second,PlayerType.HUMAN))
            }
            if (i.first == p3Name.text) {
                playerList.add(Player(0,order++,i.first,24,0, mutableListOf(),
                    i.second,PlayerType.HUMAN))
            }
            if (i.first == p4Name.text) {
                playerList.add(Player(0,order++,i.first,24,0, mutableListOf(),
                    i.second,PlayerType.HUMAN))
            }
        }
        return playerList
    }

    /**
     * updates labels when a player joins or leaves the lobby
     */
    private fun updateLabels() {
        playerLabels.forEachIndexed { index, _ ->
            if (players.size > index) {
                //playerLabels[index].text = players[index]
                playerLabels[index].apply {
                    text = players[index]
                    font = Font(
                        size = 22,
                        color = Color.WHITE)
                }
            } else {
                playerLabels[index].text =  ""
            }
        }
    }






    init {

        background = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/Water.png")))
        startButton.isVisible = false

        addComponents(
            nameLabel,orderLabel,typeLabel,colourLabel,
            p1Name,p2Name,p3Name,p4Name,p1to,p2to,p3to,p4to,p1Color,p2Color,p3Color,p4Color,pt1,backButton,
            cancelButton,startButton,secret,sessionId,createGameButton
        )
    }
    override fun refreshConnectionState(state: ConnectionState) {

        val disconnected = state == ConnectionState.DISCONNECTED
        cancelButton.isVisible = !disconnected
        startButton.isVisible = !disconnected
        backButton.isDisabled = !disconnected
        createGameButton.isVisible = disconnected
    }

    override fun refreshOnUserJoined(sender: String) {
        players.add(sender)
        updateLabels()

    }

    override fun refreshOnUserLeft(sender: String) {
        players.remove(sender)
        updateLabels()

    }




    /**
     * kleine nachricht vom network team :)
     * beim startGame button (der bestimmt noch kommen wird) bitte nicht startGame in gameService ausführen
     * sondern startNewHostedGame in NetworkService, welche Service genau werden wir noch besprechen :)
     */
}