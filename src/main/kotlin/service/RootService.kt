package service

import entity.Sagani
import view.Refreshable

/**
 * RootService can access all the other services. It additionally contains the current Sagani game
 * as well as methods to add Refreshables.
 */
class RootService {

    var gameService = GameService(this)
    var playerService = PlayerService(this)
    var aiService = AIService(this)
    var fileService = FileService(this)
    var networkService = NetworkService(this)

    var currentGame: Sagani? = null

    /**
     * Adds a [newRefreshable] to all services.
     *
     * @param newRefreshable: new Refreshable
     */
    fun addRefreshable(newRefreshable: Refreshable) {
        gameService.addRefreshable(newRefreshable)
        playerService.addRefreshable(newRefreshable)
        aiService.addRefreshable(newRefreshable)
        fileService.addRefreshable(newRefreshable)
        networkService.addRefreshable(newRefreshable)
    }

    /**
     * The function addRefreshable is called for every [newRefreshables].
     *
     * @param newRefreshables: multiple new Refreshables
     */
    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}