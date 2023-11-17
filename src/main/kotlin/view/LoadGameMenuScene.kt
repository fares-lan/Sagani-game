package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color
import javax.imageio.ImageIO

/** [LoadGameMenuScene] shows you the option you have when you want to load a game and starts a new game with the file.
 * @param rootService
 */
class LoadGameMenuScene : MenuScene(1920, 1080), Refreshable  {

    private val loadLabel = Label(
        width = 500, height = 35, posX = 685, posY = 300,
        visual = TextVisual("Please choose the File you want to load", Font(
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
        backgroundStyle = "-fx-background-color: rgba(252,159,113, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    val loadButton : Button = Button(
        width = 280,
        height = 70,
        posX = 810,
        posY = 400,
        text = "Load",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(252,159,113, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    init {
        background = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/Earth.png")))
        addComponents(loadLabel,backButton,loadButton
        )
    }
}