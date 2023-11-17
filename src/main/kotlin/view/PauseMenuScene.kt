package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/** [PauseMenuScene] manages the pause menu and shows the option the player has
 */
class PauseMenuScene(val rootService: RootService) : MenuScene(1920, 1080), Refreshable  {



    val quitGameButton = Button(
        width = 280,
        height = 70,
        posX = 820,
        posY = 600,
        text = "Quit Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(248,194,119, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    val saveGameButton = Button(
        width = 280,
        height = 70,
        posX = 820,
        posY = 500,
        text = "Save Game",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(155,251,111, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
    }

    val resumeButton = Button(
        width = 280,
        height = 70,
        posX = 820,
        posY = 400,
        text = "Resume",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(83,215,216, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }

    init {

        background = ColorVisual(108, 168, 59)

        addComponents(
          quitGameButton,saveGameButton,resumeButton
        )
    }
    override fun refreshAfterStartGame() {
        saveGameButton.isDisabled = rootService.networkService.client != null
    }
}