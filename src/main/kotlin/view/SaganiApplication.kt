package view

import entity.Tile
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.dialog.ExtensionFilter
import tools.aqua.bgw.dialog.FileDialog
import tools.aqua.bgw.dialog.FileDialogMode
import tools.aqua.bgw.event.KeyCode


/** [SaganiApplication] manages the whole game, here you can mostly find "apply" additions to objects
 */
class SaganiApplication : BoardGameApplication("Sagani"), Refreshable {
    // Central service from which all others are created/accessed
    // also holds the currently active game
    private val rootService = RootService()

    private val compManager = ComponentManager(rootService)

    // Scenes
    // This is where the actual game takes place
    private val gameScene : GameScene = GameScene(rootService, compManager).apply {

        // open PauseMenu:
        onKeyPressed = { event ->
            if (event.keyCode == KeyCode.ESCAPE) {
                hideMenuScene()
                showMenuScene(pauseMenuScene)
            }
        }
        menuButton.onMouseClicked = {
            hideMenuScene()
            showMenuScene(pauseMenuScene)
        }

        // open placeTileMenu:
        allTilesList.forEachIndexed { index, cardView ->
            cardView.onMouseClicked = {
                println(rootService.currentGame!!.intermezzoMoveCounter)
                placeTileGameScene.newCall()
                when (index) {
                    in 0..8 -> placeTileGameScene.setActiveTile(index)
                    else -> placeTileGameScene.setActiveTile(9)
                }
                if(index == 9){
                    allTilesList[4].isDisabled = true
                    offer5View.isDisabled = true
                    placeTileGameScene.offer5View.isDisabled = true
                    }
                showGameScene(placeTileGameScene)
            }
        }
    }

    private val localGameMenuScene : MenuScene = LocalGameMenuScene(rootService).apply {
        backButton.onMouseClicked = {
            hideMenuScene()
            showMenuScene(mainMenuScene)
        }
    }

    private val loadGameMenuScene : MenuScene = LoadGameMenuScene().apply {
        backButton.onMouseClicked = {
            hideMenuScene()
            showMenuScene(mainMenuScene)
        }
        loadButton.onMouseClicked = {
            showFileDialog(
                FileDialog(
                    mode = FileDialogMode.OPEN_FILE,
                    title = "Open file",
                    //extensionFilters = listOf(ExtensionFilter("textfile",".txt"))
                ))
                .ifPresent{l->
                    rootService.fileService.loadGame(l[0])
                    hideMenuScene()
                    showGameScene(gameScene)
                }
        }
    }

    private val placeTileGameScene : PlaceTileGameScene = PlaceTileGameScene(rootService, compManager,
        ).apply {
        backButton.onMouseClicked = {
            showGameScene(gameScene)
            if(offer5View.isDisabled){
                gameScene.offer5View.isDisabled = true
            }
        }

        passMoveIntermezzoButton.onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game) { "There is no ongoing game!" }
            rootService.playerService.passMoveIntermezzo()
            showGameScene(gameScene)
        }

        confirmButton.onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game) { "There is no ongoing game!" }
            offer5View.isDisabled = false
            gameScene.offer5View.isDisabled = false
            try {
            val chosenTile: Tile? = when (chosenTileInd) {
                in 0 .. 4 -> game.offerDisplay[chosenTileInd - 5+game.offerDisplay.size]
                in 5 .. 8 -> game.intermezzoDisplay[chosenTileInd - 9+game.intermezzoDisplay.size]
                9 -> game.drawStack.first()
                else -> null
            }
            checkNotNull(chosenTile) {"No Tile has been selected!"}

                rootService.playerService.makeMove(
                    posX = chosenX-playerGrid.originX, posY = -(chosenY-playerGrid.originY),
                    selectedTile = chosenTile,
                    rotation = chosenRotation,
                    online = "localPlayer"
                )
                showGameScene(gameScene)
            }
            catch (e: Exception){
                e.message?.let { warning("Choose a tile") }
            }
        }
    }

    private val pauseMenuScene = PauseMenuScene(rootService).apply {
        resumeButton.onMouseClicked = {
            hideMenuScene()
        }
        quitGameButton.onMouseClicked = {

            hideMenuScene()
            showMenuScene(mainMenuScene)
            rootService.currentGame = null
        }
        saveGameButton.onMouseClicked = {
                showFileDialog(
                    FileDialog(
                        mode = FileDialogMode.SAVE_FILE,
                        title = "Save file",
                        extensionFilters = listOf(ExtensionFilter("textfile",".txt"))
                    ))
                    .ifPresent{l->
                       rootService.fileService.saveGame(l[0])
                    }
            }
    }

    // This menu scene is shown after each finished game (i.e. too few cards in the draw pile)
    private val endScene = EndScene(rootService).apply {
        quitButton.onMouseClicked = {
            hideMenuScene()
            this@SaganiApplication.showMenuScene(mainMenuScene)
            rootService.currentGame = null
        }
    }



    // This menu scene is shown after application start and if the "quit" button
    // is clicked in the EndScene
    private val mainMenuScene : MenuScene = MainMenuScene().apply {
        exitButton.onMouseClicked = {
            exit()
        }
        joinOnlineGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(joinGameMenuScene)
        }
        hostOnlineGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(hostGameMenuScene)
        }
        localGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(localGameMenuScene)
            localGameMenuScene.addComponents()
        }
        loadGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(loadGameMenuScene)
        }
    }
    private val joinGameMenuScene  = JoinGameMenuScene(rootService).apply {
        backButton.onMouseClicked = {
            hideMenuScene()
            showMenuScene(mainMenuScene)
        }
    }


    private val hostGameMenuScene = HostGameMenuScene(rootService).apply {
        backButton.onMouseClicked = {
            hideMenuScene()
            this@SaganiApplication.showMenuScene(mainMenuScene)
        }
    }

    override fun refreshAfterStartGame() {
        this.hideMenuScene()
        this.showGameScene(gameScene)

    }

    override fun refreshAfterGameEnds() {
        this.showMenuScene(endScene)
    }

    init {
        rootService.addRefreshables(
            this,
            gameScene,
            endScene,
            placeTileGameScene,
            hostGameMenuScene,
            joinGameMenuScene,
            pauseMenuScene
        )

        this.showMenuScene(mainMenuScene)
        //this.showGameScene(gameScene)
    }

}