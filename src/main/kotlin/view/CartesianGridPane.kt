package view

import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.core.DEFAULT_GRID_SPACING
import tools.aqua.bgw.visual.Visual

/**
 * Extends the [GridPane]-class to also work with cartesian coordinates relative to an origin as input.
 */
class CartesianGridPane<T : ComponentView>(
    posX: Number = 0,
    posY: Number = 0,
    columns: Int,
    rows: Int,
    spacing: Number = DEFAULT_GRID_SPACING,
    visual: Visual = Visual.EMPTY
) :
    GridPane<T>(
        posX = posX, posY = posY,
        rows = rows, columns = columns,
        spacing = spacing, layoutFromCenter = true,
        visual = visual
    ) {

    var originX: Int = (rows-1)/2
    var originY: Int = (columns-1)/2

    /**
     * Returns [ComponentView] in with cartesian coordinates specified cell. Returns null if there was no component.
     * @param posX - x-coordinate
     * @param posY - y-coordinate
     * @return T? - the requested content
     */
    fun getCartesian(posX: Int, posY: Int): T? = this[posX+originX, originY-posY]

    /**
     * Sets content of desired grid cell specified with cartesian coordinates.
     * Overrides existing component in this cell. Pass null to remove a component.
     * @param posX - x-coordinate
     * @param posY - y-coordinate
     * @param content - which shall be inserted
     */
    fun setCartesian(posX: Int, posY: Int, content: T) {
        this[posX+originX, posY+originY] = content
    }

    /**
     * super.grow() but also adjusts origin-coordinates
     * @param left - Column count to be added to the left.
     * @param right - Column count to be added to the right.
     * @param top - Row count to be added on the top.
     * @param bottom - Row count to be added on the bottom.
     */
    fun growCartesian(left: Int, right: Int, top: Int, bottom: Int) {
        originX += left
        originY += top
        super.grow(left, right, top, bottom)
    }
}