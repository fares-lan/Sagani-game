package service

import view.Refreshable

/**
 * Abstract class for the Refreshables.
 */
abstract class AbstractRefreshingService {

    private val refreshables = mutableListOf<Refreshable>()

    /**
     * Adds a new Refreshable to the list [refreshables].
     *
     * @param newRefreshable: new Refreshable
     */
    fun addRefreshable(newRefreshable : Refreshable) {
        refreshables += newRefreshable
    }

    /**
     * This function calls a [method] on all Refreshables.
     */
    fun onAllRefreshables(method: Refreshable.() -> Unit) =
        refreshables.forEach { it.method() }
}