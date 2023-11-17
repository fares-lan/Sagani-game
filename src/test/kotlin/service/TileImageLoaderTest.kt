package service

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.test.*

/**
 * Test cases for the [TileImageLoader]. All code is based on CardImageLoaderTest.kt and has been altered to work with
 * tiles of the Sagani board game.
 */
class TileImageLoaderTest {
    /**
     * The [TileImageLoader] that is tested with this test class
     */
    private val imageLoader: TileImageLoader = TileImageLoader()

    /**
     * A test image of the front of the first blue tile (id = 37) that is used to be compared to images
     * loaded with [imageLoader].
     */
    private val tileFront37 = ImageIO.read(TileImageLoaderTest::class.java.getResource("/tileFront_37.png"))

    /**
     * Loads the images for front and backside of every tile and checks whether the resulting [BufferedImage]
     * has the correct dimensions of 610x610 px.
     */
    @Test
    fun testLoadAll() {
        val allImages = mutableListOf<BufferedImage>()
        for (i in 1..72) {
            allImages += imageLoader.frontImageFor(i)
            allImages += imageLoader.backImageFor(i)
        }
        allImages.forEach {
            assertEquals(610, it.width)
            assertEquals(610, it.height)
        }
        assertFailsWith<IllegalArgumentException> {imageLoader.frontImageFor(0)}
        assertFailsWith<IllegalArgumentException> {imageLoader.frontImageFor(73)}
        assertFailsWith<IllegalArgumentException> {imageLoader.backImageFor(0)}
        assertFailsWith<IllegalArgumentException> {imageLoader.backImageFor(73)}
    }

    /**
     * Loads the tile with id 37 from the [imageLoader] and tests equality to [tileFront37]
     */
    @Test
    fun testCardEqual() {
        val testImage = imageLoader.frontImageFor(37)
        assertTrue (testImage sameAs99 tileFront37)
    }

    /**
     * Loads the tile with id 24 from the [imageLoader] and tests inequality [tileFront37]
     */
    @Test
    fun testCardUnequal() {
        val testImage = imageLoader.frontImageFor(24)
        assertFalse(testImage sameAs99 tileFront37)
    }

}

/**
 * Tests equality of two [BufferedImage]s by first checking if they have the same dimensions
 * and then comparing every pixels' RGB value. The function is satisfied with a successrate of
 * at least 99% over all pixels
 */
private infix fun BufferedImage.sameAs99(other: Any?): Boolean {

    // if the other is not even a BufferedImage, we are done already
    if (other !is BufferedImage) {
        return false
    }

    // check dimensions
    if (this.width != other.width || this.height != other.height) {
        return false
    }

    var numOfMatchingPixels = 0
    // compare every pixel
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (this.getRGB(x, y) == other.getRGB(x, y))
                numOfMatchingPixels++
        }
    }

    if (numOfMatchingPixels < this.height * this.width * 0.99) {
        return false
    }

    // if we reach this point, dimensions and pixels match
    return true
}
