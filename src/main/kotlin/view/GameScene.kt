package view

import entity.DiscColor
import entity.PlayerType
import service.RootService
import tools.aqua.bgw.animation.DelayAnimation
import tools.aqua.bgw.components.container.Area
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*
import java.awt.Color
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.schedule

/** [GameScene] shows you the tiles of the player, the 3 stacks and also the game table.
 * [GameScene] also manages all the named visuals above.
 * @param rootService
 * @param compManager
 */
class GameScene (
    private val rootService: RootService,
    private val compManager: ComponentManager) : BoardGameScene(1920, 1080), Refreshable {


    private var animationSpeed = 4000
        // CardViews:
    private val offer1View = CardView(width = 110, height = 110, posX = 680, posY = 206, front = Visual.EMPTY)

    private val offer2View = CardView(width = 110, height = 110, posX = 791.5, posY = 206, front = Visual.EMPTY)

    private val offer3View = CardView(width = 110, height = 110, posX = 903, posY = 206, front = Visual.EMPTY)

    private val offer4View = CardView(width = 110, height = 110, posX = 1014.5, posY = 206, front = Visual.EMPTY)

    val offer5View = CardView(width = 110, height = 110, posX = 1126, posY = 206, front = Visual.EMPTY)

    private val offerViewList = mutableListOf(offer1View, offer2View, offer3View, offer4View, offer5View)

    private val mezzoOffer1View = CardView(width = 110, height = 110, posX = 736.5, posY = 330, front = Visual.EMPTY)
        .apply { isDisabled = true }

    private val mezzoOffer2View = CardView(width = 110, height = 110, posX = 848, posY = 330, front = Visual.EMPTY)
        .apply { isDisabled = true }

    private val mezzoOffer3View = CardView(width = 110, height = 110, posX = 959.5, posY = 330, front = Visual.EMPTY)
        .apply { isDisabled = true }

    private val mezzoOffer4View = CardView(width = 110, height = 110, posX = 1071, posY = 330, front = Visual.EMPTY)
        .apply { isDisabled = true }

    private val mezzoViewList = mutableListOf(mezzoOffer1View, mezzoOffer2View, mezzoOffer3View, mezzoOffer4View)

    private val drawPile1View = CardView(width = 110, height = 110, posX = 1280, posY = 330, front = Visual.EMPTY)
        .apply { isDisabled = true }

    private val drawPile2View = CardView(width = 110, height = 110, posX = 1280, posY = 450, front = Visual.EMPTY)
        .apply { isDisabled = true }

    private val drawPile3View = CardView(width = 110, height = 110, posX = 1280, posY = 570, front = Visual.EMPTY)
        .apply { isDisabled = true }

    val allTilesList = offerViewList + mezzoViewList + drawPile1View + drawPile2View + drawPile3View

    // PlayerGrids:
    private var playerGrid1 = compManager.playerGridList[0]

    private val playerGrid2 = compManager.playerGridList[1]

    private val playerGrid3 = compManager.playerGridList[2]

    private val playerGrid4 = compManager.playerGridList[3]

    // Labels, Tokens, other visual Components:

    private val currentPlayerLabel = Label(
        width = 588, height = 50,
        posX = 666, posY = 70,
        font = Font(family = "Times New Roman" , size = 40, fontWeight = Font.FontWeight.BOLD)
    )

    private val warningLabel = Label(
        width = 588, height = 60,
        posX = 666, posY = 120,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )

    private val player1Label = Label(
        width = 350, height = 50,
        posX = 10, posY = 400,
        font = Font(family = "Times New Roman" , size = 30),
        alignment = Alignment.CENTER_LEFT
    )

    private val player2Label = Label(
        width = 350, height = 50,
        posX = 1560, posY = 400,
        alignment = Alignment.CENTER_RIGHT
    )

    private val player3Label = Label(
        width = 350, height = 50,
        posX = 10, posY = 550,
        alignment = Alignment.CENTER_LEFT
    )

    private val player4Label = Label(
        width = 350, height = 50,
        posX = 1560, posY = 550,
        alignment = Alignment.CENTER_RIGHT
    )

    private val playerLabelList = mutableListOf(player1Label, player2Label, player3Label, player4Label)

    private val discsLeft1Label = Label(
        width = 130, height = 30,
        posX = 10, posY = 450,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT,
        text = "Discs left:"
    )
    private val discs1Label = Label(
        width = 30, height = 30,
        posX = 140, posY = 450,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
    )
    private val caco1Label = Label(
        width = 30, height = 30,
        posX = 170, posY = 450,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )

    private val discsLeft2Label = Label(
        width = 130, height = 30,
        posX = 1710, posY = 450,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT,
        text = "Discs left:"
    )
    private val discs2Label = Label(
        width = 30, height = 30,
        posX = 1850, posY = 450,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
    )
    private val caco2Label = Label(
        width = 30, height = 30,
        posX = 1880, posY = 450,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )

    private val discsLeft3Label = Label(
        width = 130, height = 30,
        posX = 10, posY = 600,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT,
        text = "Discs left:"
    )
    private val discs3Label = Label(
        width = 30, height = 30,
        posX = 140, posY = 600,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
    )
    private val caco3Label = Label(
        width = 30, height = 30,
        posX = 170, posY = 600,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )

    private val discsLeft4Label = Label(
        width = 130, height = 30,
        posX = 1710, posY = 600,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER_LEFT,
        text = "Discs left:"
    )
    private val discs4Label = Label(
        width = 30, height = 30,
        posX = 1850, posY = 600,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD),
    )
    private val caco4Label = Label(
        width = 30, height = 30,
        posX = 1880, posY = 600,
        font = Font(family = "Times New Roman" , size = 30, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )

    private val discsLeftLabelList = mutableListOf(discsLeft1Label, discsLeft2Label, discsLeft3Label, discsLeft4Label)
    private val discsLabelList = mutableListOf(discs1Label, discs2Label, discs3Label, discs4Label)
    private val cacoLabelList = mutableListOf(caco1Label, caco2Label, caco3Label, caco4Label)

    private val player1DiscArea = Area<TokenView>(
        posX = 5, posY = 400,
        width = 100, height = 100,
        visual = ColorVisual(144,238,144, 128)
    ).apply { isVisible = false }

    private val player1Marker = TokenView(
        width = 30, height = 30,
        visual = Visual.EMPTY
    ).apply { isVisible = false }

    private val player2Marker = TokenView(
        width = 30, height = 30,
        visual = Visual.EMPTY
    ).apply { isVisible = false }

    private val player3Marker = TokenView(
        width = 30, height = 30,
        visual = Visual.EMPTY
    ).apply { isVisible = false }

    private val player4Marker = TokenView(
        width = 30, height = 30,
        visual = Visual.EMPTY
    ).apply { isVisible = false }

    private val playerMarkerList = mutableListOf(player1Marker, player2Marker, player3Marker, player4Marker)

    private val gameBoardView = TokenView(
        width = 588, height = 702,
        posX = 666, posY = 190,
        visual = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/gameboard.png")))
    )

    // Buttons:
    private val undoButton = Button(
        width = 180,
        height = 50,
        posX = 740,
        posY = 900,
        text = "undo",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(59,112,128, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
        //visual = ColorVisual(59,112,128)
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game) { "There is no ongoing game!" }
            try {

                println()
                println("undo triggered in GameScene")
                println()
                rootService.playerService.undo()
            }
            catch (e: IllegalStateException) {
                e.message?.let { warning(it) }
            }
        }
    }

    private val redoButton = Button(
        width = 180,
        height = 50,
        posX = 1000,
        posY = 900,
        text = "redo",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(59,112,128)
        backgroundStyle = "-fx-background-color: rgba(59,112,128, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game) { "There is no ongoing game!" }
            try {
                rootService.playerService.redo()
            }
            catch (e: IllegalStateException) {
                e.message?.let { warning(it) }
            }
        }
    }

    val menuButton = Button(
        width = 180,
        height = 50,
        posX = 860,
        posY = 20,
        text = "Menu",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(59, 112, 128)
        backgroundStyle = "-fx-background-color: rgba(59,112,128, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    // simSpeedSlider:
    private val simSpeedSlider = Label(
        width = 15, height = 25, posX = 920, posY = 1020
    ).apply {
        visual = ColorVisual(100, 100, 255)
    }
    private val simSpeed = Label(
        width = 300, height = 25, posX = 820, posY = 1020
    ).apply {
        visual = ColorVisual(200, 200, 200)
        onMousePressed = {
            simSpeedNoSim.onMouseEntered = {
                simSpeedSlider.posX = 820.0
                animationSpeed = 10000
            }
            simSpeedFast.onMouseEntered = {
                simSpeedSlider.posX = 1110.0
                animationSpeed = 100
            }
            simSpeedVerySlow.onMouseEntered = {
                simSpeedSlider.posX = 920.0
                animationSpeed = 4000
            }
            simSpeedSlow.onMouseEntered = {
                simSpeedSlider.posX = 1020.0
                animationSpeed = 1000
            }
        }
    }

    /**
     * simSpeedBarriers, when the player leaves the area he cannot adjust the simSpeed again by just entering the slider
     */
    private val simSpeedBarrierLeft = Label(
        width = 1, height = 75, posX = 810, posY = 990
    ).apply {
        onMouseEntered = {
            simSpeedNoSim.onMouseEntered = {}
            simSpeedFast.onMouseEntered = {}
            simSpeedSlow.onMouseEntered = {}
            simSpeedVerySlow.onMouseEntered = {}
        }
    }
    private val simSpeedBarrierTop = Label(
        width = 320, height = 1, posX = 810, posY = 1000
    ).apply {
        onMouseEntered = {
            simSpeedNoSim.onMouseEntered = {}
            simSpeedFast.onMouseEntered = {}
            simSpeedSlow.onMouseEntered = {}
            simSpeedVerySlow.onMouseEntered = {}
        }
    }
    private val simSpeedBarrierRight = Label(
        width = 1, height = 75, posX = 1130, posY = 990
    ).apply {
        onMouseEntered = {
            simSpeedNoSim.onMouseEntered = {}
            simSpeedFast.onMouseEntered = {}
            simSpeedSlow.onMouseEntered = {}
            simSpeedVerySlow.onMouseEntered = {}
        }
    }
    private val simSpeedBarrierBottom = Label(
        width = 320, height = 1, posX = 810, posY = 1075
    ).apply {
        onMouseEntered = {
            simSpeedNoSim.onMouseEntered = {}
            simSpeedFast.onMouseEntered = {}
            simSpeedSlow.onMouseEntered = {}
            simSpeedVerySlow.onMouseEntered = {}
        }
    }
    private val simSpeedNoSim = Label(
        width = 15, height = 60, posX = 820, posY = 1020
    ).apply {
        onMousePressed = {
            simSpeedSlider.posX = 820.0
            animationSpeed = 10000
        }
    }
    private val simSpeedVerySlow = Label(
        width = 15, height = 60, posX = 920, posY = 1000
    ).apply {
        onMousePressed = {
            simSpeedSlider.posX = 920.0
            animationSpeed = 4000
        }
    }
    private val simSpeedSlow = Label(
        width = 15, height = 60, posX = 1020, posY = 1010
    ).apply {
        onMousePressed = {
            simSpeedSlider.posX = 1020.0
            animationSpeed = 1000
        }
    }
    private val simSpeedFast = Label(
        width = 15, height = 60, posX = 1110, posY = 990
    ).apply {
        onMousePressed = {
            simSpeedSlider.posX = 1110.0
            animationSpeed = 100
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
     * function to determine which tiles the player can select from
     */
    private fun enableButtons(){
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        //Enable/Disable Intermezzo/Offer display Tiles
        if (game.intermezzoMoveCounter != -1){
            mezzoOffer1View.isDisabled = false
            mezzoOffer2View.isDisabled = false
            mezzoOffer3View.isDisabled = false
            mezzoOffer4View.isDisabled = false
            offer1View.isVisible = false
            offer2View.isVisible = false
            offer3View.isVisible = false
            offer4View.isVisible = false
            offer5View.isVisible = false
            drawPile1View.isVisible = false
            drawPile2View.isVisible = false
            drawPile3View.isVisible = false

        }

        if (game.intermezzoMoveCounter == -1){
            mezzoOffer1View.isDisabled = true
            mezzoOffer2View.isDisabled = true
            mezzoOffer3View.isDisabled = true
            mezzoOffer4View.isDisabled = true
            offer1View.isVisible = true
            offer2View.isVisible = true
            offer3View.isVisible = true
            offer4View.isVisible = true
            offer5View.isVisible = true
            drawPile1View.isVisible = true
            drawPile2View.isVisible = true
            drawPile3View.isVisible = true
            drawPile1View.isDisabled = true
            drawPile2View.isDisabled = true
            drawPile3View.isDisabled = true
        }

        if (game.offerDisplay.size == 1){
            if (drawPile2View.visual == Visual.EMPTY){
                drawPile3View.isDisabled = false
            }
            if (drawPile1View.visual == Visual.EMPTY &&
                        drawPile2View.visual != Visual.EMPTY){
                drawPile2View.isDisabled = false
            }
            else{ drawPile1View.isDisabled = false}
        }
    }

    // disable buttons but allowing simspeed adjustment and option to open up the menu
    /*private fun disableButtons(){
        mezzoOffer1View.isDisabled = true
        mezzoOffer2View.isDisabled = true
        mezzoOffer3View.isDisabled = true
        mezzoOffer4View.isDisabled = true
        offer1View.isDisabled = true
        offer2View.isDisabled = true
        offer3View.isDisabled = true
        offer4View.isDisabled = true
        offer5View.isDisabled = true
        drawPile1View.isDisabled = true
        drawPile2View.isDisabled = true
        drawPile3View.isDisabled = true
    }

     */

    init {
        // Grass Background Picture
        background = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/grass2.jpg")))

        addComponents(
            gameBoardView,
            offer1View, offer2View, offer3View, offer4View, offer5View,
            mezzoOffer1View, mezzoOffer2View, mezzoOffer3View, mezzoOffer4View,
            drawPile1View, drawPile2View, drawPile3View,
            player1Label, player2Label, player3Label, player4Label, currentPlayerLabel, warningLabel,
            discsLeft1Label, discsLeft2Label, discsLeft3Label, discsLeft4Label,
            discs1Label, discs2Label, discs3Label, discs4Label,
            caco1Label, caco2Label, caco3Label, caco4Label,
            playerGrid1, playerGrid2, playerGrid3, playerGrid4,
            player1DiscArea,
            player1Marker, player2Marker, player3Marker, player4Marker,
            undoButton, redoButton, menuButton,

            simSpeed,
            simSpeedSlider,
            simSpeedNoSim,
            simSpeedVerySlow, simSpeedSlow, simSpeedFast,
            simSpeedBarrierLeft,
            simSpeedBarrierBottom,
            simSpeedBarrierRight, simSpeedBarrierTop

        )
    }

    override fun refreshAfterStartGame() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        compManager.initializeTileMaps()
        compManager.updateComponents()
        compManager.playerGridList.forEachIndexed { index, cardViews ->
            compManager.playerGridList[index].isVisible = index < game.players.size
        }
        // initialize all Player Tokens/Labels:
        playerLabelList.forEach { it.isVisible = false}
        discsLeftLabelList.forEach {it.isVisible = false}
        discsLabelList.forEach {it.isVisible = false}
        cacoLabelList.forEach {it.isVisible = false}
        warningLabel.isVisible = false
        compManager.updateComponents()
        game.players.forEachIndexed { index, player ->
            //name
            playerLabelList[index].text = player.name
            discsLabelList[index].text = "24"
            cacoLabelList[index].text = "0"
            playerLabelList[index].isVisible = true
            discsLeftLabelList[index].isVisible = true
            discsLabelList[index].isVisible = true
            cacoLabelList[index].isVisible = true
            playerLabelList[index].font = when (game.players[index].color) {
                DiscColor.GREY -> {
                    Font(family = "Times New Roman" , size = 40, fontWeight = Font.FontWeight.BOLD,
                        color = Color.DARK_GRAY) }
                DiscColor.WHITE -> {
                    Font(family = "Times New Roman" , size = 40, fontWeight = Font.FontWeight.BOLD, color = Color.WHITE) }
                DiscColor.BLACK -> {
                    Font(family = "Times New Roman" , size = 40, fontWeight = Font.FontWeight.BOLD, color = Color.BLACK) }
                else -> {
                    Font(family = "Times New Roman" , size = 40, fontWeight = Font.FontWeight.BOLD,
                        color = Color(160,82,45)) }
            }
            //marker
            playerMarkerList[index].visual = when (game.players[index].color) {
                DiscColor.GREY -> ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/playergrey.png")))
                DiscColor.WHITE -> ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/playerwhite.png")))
                DiscColor.BLACK -> ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/playerblack.png")))
                DiscColor.BROWN -> ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/playerbrown.png")))
                else -> Visual.EMPTY
            }
            playerMarkerList[index].posX = 780.0 - Random().nextInt(50)
            playerMarkerList[index].posY = 840.0 - Random().nextInt(50)
            playerMarkerList[index].isVisible = true }
        for (i in 0..game.players[0].discsLeftCount) {
            player1DiscArea.add(TokenView(
                posX = Random().nextInt(80), posY = Random().nextInt(80),
                width = 20, height = 20,
                visual = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/discgrey.png"))))) }
        // initialize all CardViews:
        game.offerDisplay.forEachIndexed { index, tile ->
            compManager.changeCardView(offerViewList[index], tile)
            offerViewList[index].isDisabled = false }
        // initialize drawPiles:
        compManager.changeCardView(drawPile1View, game.drawStack.first(), false)
        compManager.changeCardView(drawPile2View, game.drawStack[22], false)
        compManager.changeCardView(drawPile3View, game.drawStack[46], false)
        if(game.players[game.playerIndex].playerType != PlayerType.HUMAN) {
            if (rootService.networkService.client != null){
                this.playAnimation(DelayAnimation(duration = 4000).apply {
                    onFinished = {
                        if(game.players[game.playerIndex].playerType != PlayerType.HUMAN){
                            rootService.aiService.moveByAI()
                        }
                    }
                })
            }else rootService.aiService.moveByAI() }
        //visibility undo/redo Button:
        if (rootService.networkService.client != null){
            undoButton.isVisible = false
            redoButton.isVisible = false }
    }

    override fun refreshAfterMakeMove() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        // update buttons:
        enableButtons()
        println("playerIndex: " + game.playerIndex)

        // update player Tokens/Grids:
        currentPlayerLabel.text = "It's your turn ${game.players[game.playerIndex].name}!"
        compManager.updateComponents()
        game.players.forEachIndexed { index, player ->
            discsLabelList[index].text = (player.discsLeftCount-player.cacoCount).toString()
            cacoLabelList[index].text = player.cacoCount.toString()
            if (player.points > 79) {
                val cord = compManager.pointsPosMap.forward(79)
                playerMarkerList[index].posX = 815 + cord.xCoord
                playerMarkerList[index].posY = 835.5 + cord.yCoord
            }
            else if (player.points > 0) {
                val cord = compManager.pointsPosMap.forward(player.points)
                playerMarkerList[index].posX = 815 + cord.xCoord
                playerMarkerList[index].posY = 835.5 + cord.yCoord
            }
        }

        // update offerDisplay:
        for (i in 0 until 5-game.offerDisplay.size) {
            offerViewList[i].frontVisual = Visual.EMPTY
        }
        game.offerDisplay.forEachIndexed { index, tile ->
            compManager.changeCardView(offerViewList[5 - game.offerDisplay.size + index], tile)
        }

        // update IntermezzoDisplay:
        for (i in 0 until 4-game.intermezzoDisplay.size) {
            mezzoViewList[i].frontVisual = Visual.EMPTY
        }
        game.intermezzoDisplay.forEachIndexed { index, tile ->
            compManager.changeCardView(mezzoViewList[4 - game.intermezzoDisplay.size + index], tile)
        }

        // update drawPiles:
        when (val size = game.drawStack.size) {
            in 49..71 -> compManager.changeCardView(drawPile1View, game.drawStack.first(), false)
            in 25..48 -> {
                compManager.changeCardView(drawPile2View, game.drawStack.first(), false)
                drawPile1View.backVisual = TextVisual(text = "empty")
            }
            in 0..24 -> {
                compManager.changeCardView(drawPile3View, game.drawStack.first(), false)
                drawPile1View.backVisual = TextVisual(text = "empty", font = Font(fontWeight = Font.FontWeight.BOLD))
                drawPile2View.backVisual = TextVisual(text = "empty", font = Font(fontWeight = Font.FontWeight.BOLD))
            }
            else -> {
                drawPile3View.backVisual = TextVisual(text = "empty", font = Font(fontWeight = Font.FontWeight.BOLD))
            }
        }
    }



    override fun refreshForIntermezzo() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        // update buttons:
        enableButtons()

        for (i in 0 until 5-game.offerDisplay.size) {
            offerViewList[i].frontVisual = Visual.EMPTY
            offerViewList[i].isDisabled = true
        }

        game.offerDisplay.forEachIndexed { index, tile ->
            compManager.changeCardView(offerViewList[5 - game.offerDisplay.size + index], tile)
            offerViewList[5 - game.offerDisplay.size + index].isDisabled = false
        }

        // update IntermezzoDisplay:
        for (i in 0 until 4-game.intermezzoDisplay.size) {
            mezzoViewList[i].frontVisual = Visual.EMPTY
            mezzoViewList[i].isDisabled = true
        }

        game.intermezzoDisplay.forEachIndexed { index, tile ->
            compManager.changeCardView(mezzoViewList[index], tile)
        }
        compManager.updateComponents()

    }

    override fun refreshAfterTurnEnds() {
        val game = rootService.currentGame!!
        //.lock()
        this.playAnimation(DelayAnimation(duration = 1000).apply {
            onFinished = {
                //unlock()
                if(game.players[game.playerIndex].playerType != PlayerType.HUMAN){
                    //disableButtons()
                    rootService.aiService.moveByAI()
                }

            }
        })
    }

}