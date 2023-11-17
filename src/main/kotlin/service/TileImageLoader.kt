package service

import entity.Tile
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

private const val BLUE_FRONT = "/pictures/blue_front.png"
private const val BLUE_BACK = "/pictures/blue_back.png"
private const val GREEN_FRONT = "/pictures/green_front.png"
private const val GREEN_BACK = "/pictures/green_back.png"
private const val RED_FRONT = "/pictures/red_front.png"
private const val RED_BACK = "/pictures/red_back.png"
private const val WHITE_FRONT = "/pictures/white_front.png"
private const val WHITE_BACK = "/pictures/white_back.png"

private const val IMG_HEIGHT = 610
private const val IMG_WIDTH = 610

/**
 * Provides access to the files in src/main/resources/picture/ which in total contain all tile images
 * in a raster. The returned [BufferedImage] objects of [frontImageFor] and [backImageFor] are 610x610 pixels.
 * All code is based on CardImageLoader.kt and has been altered to work with tiles of the Sagani board game.
 */
class TileImageLoader {

    /**
     * The full raster images each containing front or backside images for all 18 tiles of one spirit respectively
     */
    private val redFrontRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(RED_FRONT))
    private val greenFrontRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(GREEN_FRONT))
    private val blueFrontRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(BLUE_FRONT))
    private val whiteFrontRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(WHITE_FRONT))
    private val frontRasters = mutableListOf(redFrontRaster, greenFrontRaster, blueFrontRaster, whiteFrontRaster)

    private val redBackRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(RED_BACK))
    private val greenBackRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(GREEN_BACK))
    private val blueBackRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(BLUE_BACK))
    private val whiteBackRaster : BufferedImage = ImageIO.read(TileImageLoader::class.java.getResource(WHITE_BACK))
    private val backRasters = mutableListOf(redBackRaster, greenBackRaster, blueBackRaster, whiteBackRaster)

    /**
     * Provides front image for the given id of a [Tile].
     * id-1/18 determines which spirit and thus raster image the tile has and by dividing and reduce mod the remainder
     * of the first division with 10 you get the correct coordinates for the subimage.
     *
     * @param id the given id of a [Tile]
     * @return the requested image
     */
    fun frontImageFor(id: Int) : BufferedImage {
        require(id in 1..72) {"invalid Tile-ID!"}
        return getImageByCoordinates(frontRasters[(id-1)/18], ((id-1)%18)%10, ((id-1)%18)/10)
    }

    /**
     * Provides the backside image for the given id of a [Tile].
     * id-1/18 determines which spirit and thus raster image the tile has and by dividing and reduce mod the remainder
     * of the first division with 10 you get the correct coordinates for the subimage.
     *
     * @param id the given id of a [Tile]
     * @return the requested image
     */
    fun backImageFor(id: Int) : BufferedImage {
        require(id in 1..72) {"invalid Tile-ID!"}
        return getImageByCoordinates(backRasters[(id-1)/18], ((id-1)%18)%10, ((id-1)%18)/10)
    }

    /**
     * retrieves from the full raster image [image] the corresponding sub-image
     * for the given column [x] and row [y]
     *
     * @param image the raster image
     * @param x column in the raster image, starting at 0
     * @param y row in the raster image, starting at 0
     */
    private fun getImageByCoordinates (image: BufferedImage, x: Int, y: Int) : BufferedImage =
        image.getSubimage(
            x * IMG_WIDTH,
            y * IMG_HEIGHT,
            IMG_WIDTH,
            IMG_HEIGHT
        )
}
