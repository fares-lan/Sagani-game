package view

import service.RootService
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*
import java.awt.Color
import java.util.*
import kotlin.concurrent.schedule

/** [PlaceTileGameScene] shows where the player can lay down their tiles
 * @param rootService
 */
class PlaceTileGameScene (private val rootService: RootService, private val compManager: ComponentManager)
    : BoardGameScene(1920,1080), Refreshable {

    var chosenTileInd = -1
    var chosenRotation = 0
    var chosenX = -1
    var chosenY = -1


    // CardViews:
    private val offer1View = CardView(width = 110, height = 110, posX = 680, posY = 50, front = Visual.EMPTY)

    private val offer2View = CardView(width = 110, height = 110, posX = 791.5, posY = 50, front = Visual.EMPTY)

    private val offer3View = CardView(width = 110, height = 110, posX = 903, posY = 50, front = Visual.EMPTY)

    private val offer4View = CardView(width = 110, height = 110, posX = 1014.5, posY = 50, front = Visual.EMPTY)

    val offer5View = CardView(width = 110, height = 110, posX = 1126, posY = 50, front = Visual.EMPTY)

    val offerViewList = mutableListOf(offer1View, offer2View, offer3View, offer4View, offer5View)

    private val mezzoOffer1View = CardView(width = 110, height = 110, posX = 736.5, posY = 50, front = Visual.EMPTY)
        .apply { isVisible = false }

    private val mezzoOffer2View = CardView(width = 110, height = 110, posX = 848, posY = 50, front = Visual.EMPTY)
        .apply { isVisible = false }

    private val mezzoOffer3View = CardView(width = 110, height = 110, posX = 959.5, posY = 50, front = Visual.EMPTY)
        .apply { isVisible = false }

    private val mezzoOffer4View = CardView(width = 110, height = 110, posX = 1071, posY = 50, front = Visual.EMPTY)
        .apply { isVisible = false }

    private val mezzoViewList = mutableListOf(mezzoOffer1View, mezzoOffer2View, mezzoOffer3View, mezzoOffer4View)

    private val drawPileView = CardView(width = 110, height = 110, posX = 1280, posY = 50, front = Visual.EMPTY)

    private val allTilesList = offerViewList + mezzoViewList + drawPileView

    private var chosenTileView = CardView(width = CELL_SIZE, height = CELL_SIZE, front = Visual.EMPTY).apply {
        isDisabled = true
    }

    private val chosenTileLabel = Label(width = 110, height = 20, posX = 0, posY = 0, text = "chosen")

    private val warningLabel = Label(
        width = 800, height = 40,
        posX = 560, posY = 160,
        font = Font(family = "Times New Roman" , size = 28, fontWeight = Font.FontWeight.BOLD, color = Color.RED),
    )

    var playerGrid = CartesianGridPane<CardView>(
        posX = 960- CELL_SIZE, posY = 540- CELL_SIZE,
        rows = 1, columns = 1,
        spacing = 0,
        visual = ColorVisual.WHITE
    )

    val backButton: Button = Button(
        width = 280,
        height = 70,
        posX = 140,
        posY = 900,
        text = "Back",
        font = Font(22, fontWeight = Font.FontWeight.BOLD)
    )

    val confirmButton: Button = Button(
        width = 280,
        height = 70,
        posX = 1500,
        posY = 900,
        text = "Confirm",
        font = Font(22, fontWeight = Font.FontWeight.BOLD)
    )

    val passMoveIntermezzoButton: Button = Button(
        width = 280,
        height = 70,
        posX = 1500,
        posY = 700,
        text = "Pass",
        font = Font(22, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        isVisible = false
    }

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

    /**
     * function to move the chosenTileLabel to indicate which tile has been chosen.
     * @param tileIndex is the selected index corresponding to an CardView-object from [GameScene]
     */
    fun setActiveTile(tileIndex: Int) {
        require(tileIndex in 0..9) {"tileIndex has to be in range of 0 to 9!"}
        chosenTileInd = tileIndex
        chosenTileLabel.posX = allTilesList[chosenTileInd].posX
        chosenTileLabel.posY = allTilesList[chosenTileInd].posY-20
    }

    private fun updateChosenTilePreview() {
        chosenTileView.isVisible = true
        try {
            chosenTileView.backVisual = (allTilesList[chosenTileInd].frontVisual as ImageVisual).copy().apply {
                transparency = 0.6
            }
            chosenTileView.posX = playerGrid.posX - playerGrid.columns.toDouble() / 2 * CELL_SIZE + CELL_SIZE * chosenX
            chosenTileView.posY = playerGrid.posY - playerGrid.rows.toDouble() / 2 * CELL_SIZE + CELL_SIZE * chosenY
            chosenTileView.rotation = chosenRotation.toDouble()
        }
        catch (e: ClassCastException){
            e.message?.let { warning("Choose a tile") }
        }

    }

    /**
     * function that should be called every time this [PlaceTileGameScene] will be shown.
     * It adjusts all components to match their current state.
     */
    fun newCall() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        val currentPlayer = game.players[game.playerIndex]

        enableButtons()

        // reset chosenTile variables:
        chosenRotation = 0
        chosenX = -1
        chosenY = -1
        chosenTileView.isVisible = false

        // update playerGrid:
        compManager.updatePlayerGrid(playerGrid, currentPlayer)
        playerGrid.apply {
            for (i in 0 until columns) {
                for (j in 0 until rows) {
                    this[i, j]?.onMouseClicked = {
                        if (i == chosenX && j == chosenY) {
                            chosenRotation += 90
                        }
                        else {
                            chosenX = i
                            chosenY = j
                            chosenRotation = 0
                        }
                        updateChosenTilePreview()
                    }
                }
            }
        }
        updateDisplays()

    }

    private fun updateDisplays(){
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }
        // update offerDisplay:
        for (i in 0 until 5-game.offerDisplay.size) {
            offerViewList[i].frontVisual = Visual.EMPTY
        }
        game.offerDisplay.forEachIndexed { index, tile ->
            compManager.changeCardView(offerViewList[5-game.offerDisplay.size + index], tile)
        }

        // update IntermezzoDisplay:
        for (i in 0 until 4-game.intermezzoDisplay.size) {
            mezzoViewList[i].frontVisual = Visual.EMPTY
        }
        game.intermezzoDisplay.forEachIndexed { index, tile ->
            compManager.changeCardView(mezzoViewList[4-game.intermezzoDisplay.size + index], tile)
        }

        // update drawPiles:
        compManager.changeCardView(drawPileView, game.drawStack.first(), false)
    }

    /**
     * enables/disables buttons
     */
    private fun enableButtons() {
        val game = rootService.currentGame
        checkNotNull(game) { "There is no ongoing game!" }

        // active/deactivate drawpile depending on size of offerdisplay
        if (game.offerDisplay.size == 1){
            drawPileView.isDisabled = false
        }
        if(game.offerDisplay.size != 1){
            drawPileView.isDisabled = true
        }

        //show intermezzo cards and hide offer display in intermezzo
        if (game.intermezzoMoveCounter != -1){
            offer1View.isVisible = false
            offer2View.isVisible = false
            offer3View.isVisible = false
            offer4View.isVisible = false
            offer5View.isVisible = false
            mezzoOffer1View.isVisible = true
            mezzoOffer2View.isVisible = true
            mezzoOffer3View.isVisible = true
            mezzoOffer4View.isVisible = true
            drawPileView.isVisible = false
            passMoveIntermezzoButton.isVisible = true
        }
        if (game.intermezzoMoveCounter == -1){
            offer1View.isVisible = true
            offer2View.isVisible = true
            offer3View.isVisible = true
            offer4View.isVisible = true
            offer5View.isVisible = true
            mezzoOffer1View.isVisible = false
            mezzoOffer2View.isVisible = false
            mezzoOffer3View.isVisible = false
            mezzoOffer4View.isVisible = false
            drawPileView.isVisible = true
            passMoveIntermezzoButton.isVisible = false
        }
    }

    init {
        background = ColorVisual(108, 168, 59)
        allTilesList.forEachIndexed { index, cardView ->
            cardView.onMouseClicked = {
                if(index == 9){
                    chosenRotation = 0
                    setActiveTile(index)
                    offer5View.isDisabled = true
                }
                else{
                chosenRotation = 0
                setActiveTile(index)
            }}
        }

        addComponents(
            offer1View, offer2View, offer3View, offer4View, offer5View,
            mezzoOffer1View, mezzoOffer2View, mezzoOffer3View, mezzoOffer4View, drawPileView,
            playerGrid, chosenTileView, chosenTileLabel, warningLabel,
            backButton, confirmButton, passMoveIntermezzoButton
        )
    }



}