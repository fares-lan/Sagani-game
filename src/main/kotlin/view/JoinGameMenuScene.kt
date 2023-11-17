package view

import entity.PlayerType
import service.ConnectionState
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
import javax.imageio.ImageIO

/** [JoinGameMenuScene] shows you the possible options you have as a player who joins. Here you can edit your name.
 * @param rootService
 */
class JoinGameMenuScene (private val rootService: RootService) : MenuScene(1920, 1080), Refreshable  {
    private val p1Name: TextField = TextField(
        width = 400, height = 35, posX = 785, posY = 270, text = "playername1", font = Font(22)
    )
    private val pt1 = ComboBox<PlayerType>(
        width = 400, height = 35, posX = 785, posY = 370,
        items = listOf(PlayerType.HUMAN, PlayerType.SMART_AI, PlayerType.RANDOM_AI) , font = Font(22)
    ).apply {
        visual = ColorVisual(255,255,255)
        onMouseClicked = {
            joinButton.isDisabled = false
        }
    }
    private val sessionId: TextField = TextField(
        width = 400, height = 35, posX = 785, posY = 570, text = "lobbyname", font = Font(22)
    )
    private val secretName: TextField = TextField(
        width = 400, height = 35, posX = 785, posY = 670, text = "23_b_tbd", font = Font(22)
    )

    private val textLabel = Label(
        width = 600, height = 35, posX = 685, posY = 200,
        visual = TextVisual("Please enter your Name and choose your player type", Font(
            size = 22,
            color = Color.WHITE
        ))
    )

    private val lobbyLabel = Label(
        width = 600, height = 35, posX = 685, posY = 500,
        visual = TextVisual("Please enter your Lobby name and the secret code", Font(
            size = 22,
            color = Color.WHITE
        ))
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
        backgroundStyle = "-fx-background-color: rgba(244,86,53, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    private val cancelButton : Button = Button(
        width = 280,
        height = 70,
        posX = 920,
        posY = 905,
        text = "Cancel",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(244,86,53, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
        isVisible = false
        onMouseClicked = {
            rootService.networkService.disconnect()
        }
    }

    private val joinButton : Button = Button(
        width = 280,
        height = 70,
        posX = 1520,
        posY = 905,
        text = "Join Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(244,86,53, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
        onMouseEntered = {
            if(pt1.selectedItem == null){
                this.isDisabled = true
            }
        }

        onMouseClicked = {
            rootService.networkService.joinGame(secret = secretName.text,name = p1Name.text, sessionID = sessionId.text)
            rootService.networkService.client!!.playerType = pt1.selectedItem!!
        }
    }



    init {
        background = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/Fire.png")))

        addComponents(
            p1Name,pt1,sessionId,secretName,backButton,joinButton,textLabel,lobbyLabel,cancelButton
        )
    }
    override fun refreshConnectionState(state: ConnectionState) {

        val disconnected = state == ConnectionState.DISCONNECTED
        cancelButton.isVisible = !disconnected
        backButton.isDisabled = !disconnected
    }
}