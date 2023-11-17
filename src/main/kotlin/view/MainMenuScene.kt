package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO

/**
 * [MenuScene] that appears on program start or when the user returns to it
 */
class MainMenuScene : MenuScene(1920, 1080), Refreshable {

    val exitButton :Button = Button(
        width = 80,
        height = 70,
        posX = 1800,
        posY = 50,
        text = "Exit",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(92,53,59,100)
        backgroundStyle = "-fx-background-color: rgba(92,53,59, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    val joinOnlineGameButton : Button = Button(
        width = 280,
        height = 70,
        posX = 1065,
        posY = 900,
        text = "Join Online Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(255,225,101,100)
        backgroundStyle = "-fx-background-color: rgba(255, 225, 101, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    val hostOnlineGameButton : Button = Button(
        width = 280,
        height = 70,
        posX = 575,
        posY = 900,
        text = "Host Online Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(83,210,211,100)
        backgroundStyle = "-fx-background-color: rgba(83,210,211, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    val localGameButton : Button = Button(
        width = 280,
        height = 70,
        posX = 95,
        posY = 900,
        text = "Local Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(139,237,183,100)
        backgroundStyle = "-fx-background-color: rgba(139,237,183, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    val loadGameButton : Button = Button(
        width = 280,
        height = 70,
        posX = 1555,
        posY = 900,
        text = "Load Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        //visual = ColorVisual(248,194,119,100)
        backgroundStyle = "-fx-background-color: rgba(248,194,119, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }



    init {
        // Element Background Picture
        background = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/Elemente.png")))

        addComponents(
            hostOnlineGameButton,localGameButton,exitButton,joinOnlineGameButton,loadGameButton
        )
    }
}