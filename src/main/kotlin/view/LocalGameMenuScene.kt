package view

import entity.DiscColor
import entity.Player
import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color
import java.util.*
import javax.imageio.ImageIO
import kotlin.collections.LinkedHashMap
import kotlin.concurrent.schedule

/** [LocalGameMenuScene] shows you the options you have to configure the player names, colors, player type and order
 * @param rootService
 */
class LocalGameMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {
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


    //player names:
    private val p1Name: TextField = TextField(
        width = 300, height = 35, posX = 285, posY = 170, text = "player1", font = Font(22)
    )
    private val p2Name: TextField = TextField(
        width = 300, height = 35, posX = 285, posY = 370, text = "player2", font = Font(22)
    ).apply { onKeyTyped = {startButton.isDisabled = false }}
    private val p3Name: TextField = TextField(
        width = 300, height = 35, posX = 285, posY = 570, text = "", font = Font(22)
    ).apply { onKeyTyped = {startButton.isDisabled = false }}
    private val p4Name: TextField = TextField(
        width = 300, height = 35, posX = 285, posY = 770, text = "", font = Font(22)
    ).apply { onKeyTyped = {startButton.isDisabled = false }}

    //player order:
    private val p1to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 170, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
    }
    private val p2to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 370, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
    }
    private val p3to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 570, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
    }
    private val p4to = ComboBox<Int>(
        width = 100, height = 35, posX = 1265, posY = 770, items = listOf(1, 2, 3, 4), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
    }

    //player colours:
    private val p1Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 170,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val p2Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 370,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val p3Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 570,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val p4Color: ComboBox<DiscColor> = ComboBox<DiscColor>(
        width = 200, height = 35, posX = 985, posY = 770,
        items = listOf(DiscColor.GREY, DiscColor.BLACK, DiscColor.WHITE, DiscColor.BROWN), font = Font(22)
    ).apply {
        visual = ColorVisual(174,251,211)
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
        visual = ColorVisual(174,251,211)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val pt2 = ComboBox<PlayerType>(
        width = 220, height = 35, posX = 685, posY = 370,
        items = listOf(PlayerType.HUMAN, PlayerType.SMART_AI, PlayerType.RANDOM_AI), font = Font(22),
        prompt = "Select an option!"
    ).apply {
        visual = ColorVisual(174,251,211)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val pt3 = ComboBox<PlayerType>(
        width = 220, height = 35, posX = 685, posY = 570,
        items = listOf(PlayerType.HUMAN, PlayerType.SMART_AI, PlayerType.RANDOM_AI), font = Font(22),
        prompt = "Select an option!"
    ).apply {
        visual = ColorVisual(174,251,211)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }
    private val pt4 = ComboBox<PlayerType>(
        width = 220, height = 35, posX = 685, posY = 770,
        items = listOf(PlayerType.HUMAN, PlayerType.SMART_AI, PlayerType.RANDOM_AI), font = Font(22),
        prompt = "Select an option!"
    ).apply {
        visual = ColorVisual(174,251,211)
        onMouseClicked = {
            startButton.isDisabled = false
        }
    }

    private val warningLabel = Label(
        width = 588, height = 60,
        posX = 666, posY = 120,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )

    /**
     * displays warning message in warningLabel for 5 seconds.
     * @param text String of message
     */

    fun warning(text: String) {
        warningLabel.isVisible = true
        warningLabel.text = text
        Timer().schedule(5000) {
            warningLabel.isVisible = false
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
        backgroundStyle = "-fx-background-color: rgba(174,251,211, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
        onMouseEntered = {
            checkColorsAndTypes()
            checkNames()
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


            rootService.gameService.startGame(createPlayers(orderingPlayers(colorToPlayer(playerInfo))))}
            catch (e: IllegalStateException){
                e.message?.let { warning(it) }
            }
        }
    }
    val backButton: Button = Button(
        width = 280,
        height = 70,
        posX = 120,
        posY = 905,
        text = "Back",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(174,251,211, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
    }

    /**
     * adds the color to the player
     * @param players consists of a playername and potential options for [DiscColor] and turnOrder
     * @return returns the given list with a fixed [DiscColor]
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

    private fun orderingPlayers(players: List<Triple<String, DiscColor?, Int?>>): List<Triple<String, DiscColor?, Int?>> {
        val op = mutableListOf<Triple<String, DiscColor?, Int?>>()
        val int = mutableListOf<Int>(1,2,3,4)
        for (i in players) {
            if (i.third != null) {
                op.add(Triple(i.first, i.second, i.third))
                int.remove(i.third)
            }
        }
        op.shuffle()
        for (i in players){
            if (i.third == null){
                op.add(Triple(i.first,i.second,int[0]))
                int.removeFirst()
            }
        }
        op.sortBy { it.third }
        return op
    }

    /**
     * checks if multiple players have the same color or if no [PlayerType] is given
     * if either is true, disables the [startButton]
     */


    private fun checkColorsAndTypes() {
        val playerTypes = mutableListOf<PlayerType?>()
        val colorsList = mutableListOf<DiscColor?>()
        if (p1Name.text != "") {
            colorsList.add(p1Color.selectedItem)
            playerTypes.add(pt1.selectedItem)
        }
        if (p2Name.text != "") {
            colorsList.add(p2Color.selectedItem)
            playerTypes.add(pt2.selectedItem)
        }
        if (p3Name.text != "") {
            colorsList.add(p3Color.selectedItem)
            playerTypes.add(pt3.selectedItem)
        }
        if (p4Name.text != "") {
            colorsList.add(p4Color.selectedItem)
            playerTypes.add(pt4.selectedItem)
        }
        startButton.isDisabled = false

        for (i in 0 until colorsList.size - 1) {
            for (j in i + 1 until colorsList.size) {
                if (colorsList[i] != null && colorsList[i] == colorsList[j]) {
                    startButton.isDisabled = true

                }
            }
        }
        for (i in 0 until playerTypes.size){
            if(playerTypes[i] == null){
                startButton.isDisabled = true
            }
        }
    }

    /**
     * checks if at least two players are chosen, if not disables the [startButton]
     */


    private fun checkNames() {
        val playerNames = mutableListOf<String>()

        if (p1Name.text != "") {
            playerNames.add(p1Name.text)
        }
        if (p2Name.text != "") {
            playerNames.add(p2Name.text)
        }

        if (p3Name.text != "") {
            playerNames.add(p3Name.text)
        }

        if (p4Name.text != "") {
            playerNames.add(p4Name.text)
        }
        if(playerNames.size< 2){
            startButton.isDisabled = true
        }
    }

    /**
     * creates players to start the game with adds the [PlayerType]
     * @param players consists of the name, discColor and turnorder
     * @return returns the finished List of Players to start the game with
     */

    private fun createPlayers(players: List<Triple<String, DiscColor?, Int?>>): List<Player> {
        checkNotNull(pt1.selectedItem)
        val map = LinkedHashMap<Triple<String, DiscColor, Int?>, PlayerType>()
        @Suppress("UNCHECKED_CAST")
        for (i in players) {
            if (i.first == p1Name.text) {
                checkNotNull(i.second)
                map[i as Triple<String, DiscColor, Int?>] = pt1.selectedItem as PlayerType
            }
            if (i.first == p2Name.text) {
                checkNotNull(i.second)
                map[i as Triple<String, DiscColor, Int?>] = pt2.selectedItem as PlayerType
            }
            if (i.first == p3Name.text) {
                checkNotNull(i.second)
                map[i as Triple<String, DiscColor, Int?>] = pt3.selectedItem as PlayerType
            }
            if (i.first == p4Name.text) {
                checkNotNull(i.second)
                map[i as Triple<String, DiscColor, Int?>] = pt4.selectedItem as PlayerType
            }
        }
        val playerList = mutableListOf<Player>()
        var order = 0
        for (i in map) {
            playerList.add(Player(0, order++, i.key.first, 24, 0, mutableListOf(), i.key.second, i.value))
        }
        return playerList
    }

    init {
        background = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/Air.png")))

        addComponents(
            p1Name,
            p2Name,
            p3Name,
            p4Name,
            p1to,
            p2to,
            p3to,
            p4to,
            p1Color,
            p2Color,
            p3Color,
            p4Color,
            pt1,
            pt2,
            pt3,
            pt4,
            nameLabel,
            typeLabel,
            colourLabel,
            orderLabel,
            startButton,
            backButton,
            warningLabel

        )
    }
}