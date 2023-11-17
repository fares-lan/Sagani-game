package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import javax.imageio.ImageIO

/**[EndScene] shows we winner of the game, also here you can find the [refreshAfterGameEnds]
 * @param rootService
 */
class EndScene (private val rootService: RootService) : MenuScene(1920, 1080), Refreshable  {

    val quitButton = Button(
        width = 280,
        height = 70,
        posX = 1555,
        posY = 900,
        text = "Exit",
        font = Font(22, fontWeight = Font.FontWeight.BOLD),
        visual = ColorVisual.TRANSPARENT
    ).apply {
        backgroundStyle = "-fx-background-color: rgba(83,215,216, 0.8); " +
                "-fx-background-radius: ${10}px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 2.0, 2.0);"
        componentStyle = "-fx-background-radius: ${10}px;"
    }
    private val gameResult = Label(
        width = 700,
        height = 50,
        posX = 610,
        posY = 300,
        font = Font(30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER
    )
    private val gameResult2 = Label(
        width = 700,
        height = 50,
        posX = 610,
        posY = 400,
        font = Font(30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER
    )
    private val gameResult3 = Label(
        width = 700,
        height = 50,
        posX = 610,
        posY = 500,
        font = Font(30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER
    )
    private val gameResult4 = Label(
        width = 700,
        height = 50,
        posX = 610,
        posY = 600,
        font = Font(30, fontWeight = Font.FontWeight.BOLD),
        alignment = Alignment.CENTER
    )

    init {
        background = ImageVisual(ImageIO.read(GameScene::class.java.getResource("/pictures/Air.png")))
        addComponents(quitButton, gameResult, gameResult2, gameResult3, gameResult4)
    }

    override fun refreshAfterGameEnds() {
        val game = rootService.currentGame
        checkNotNull(game) {"No game running"}
        val pointsList = mutableListOf<Triple<String, Int, Int>>()
        game.players.forEach {
           pointsList.add(Triple(it.name,it.points,it.pointsOrder))

        }
        pointsList.sortWith(compareBy<Triple<String, Int, Int>>( { it.second },{it.third }).reversed())
        gameResult.text = "${pointsList[0].first} has won the game with ${pointsList[0].second} points!"
        gameResult2.text = "${pointsList[1].first} reached ${pointsList[1].second} points"
        if (game.players.size >= 3){
            gameResult3.text = "${pointsList[2].first} reached ${pointsList[2].second} points"
        }
        if (game.players.size >= 4){
            gameResult4.text = "${pointsList[3].first} reached ${pointsList[3].second} points"
        }
    }
    }