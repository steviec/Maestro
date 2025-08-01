package maestro.cli.mcp.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.max

class ImageUtilsTest {

    @Test
    fun `calculateScaledDimensions should not scale when image is smaller than max dimension`() {
        val (newWidth, newHeight) = ImageUtils.calculateScaledDimensions(800, 600, 1000)
        
        assertEquals(800, newWidth)
        assertEquals(600, newHeight)
    }
    
    @Test
    fun `calculateScaledDimensions should scale when width is larger than max dimension`() {
        val (newWidth, newHeight) = ImageUtils.calculateScaledDimensions(1200, 800, 1000)
        
        assertEquals(1000, newWidth)
        assertEquals(666, newHeight) // (800 * 1000) / 1200 = 666.67 -> 666
    }
    
    @Test
    fun `calculateScaledDimensions should scale when height is larger than max dimension`() {
        val (newWidth, newHeight) = ImageUtils.calculateScaledDimensions(600, 1500, 1000)
        
        assertEquals(400, newWidth) // (600 * 1000) / 1500 = 400
        assertEquals(1000, newHeight)
    }
    
    @Test
    fun `calculateScaledDimensions should maintain aspect ratio`() {
        val originalWidth = 1920
        val originalHeight = 1080
        val maxDimension = 800
        
        val (newWidth, newHeight) = ImageUtils.calculateScaledDimensions(originalWidth, originalHeight, maxDimension)
        
        val originalRatio = originalWidth.toDouble() / originalHeight
        val newRatio = newWidth.toDouble() / newHeight
        
        assertEquals(originalRatio, newRatio, 0.01) // Allow small floating point difference
        assertEquals(800, max(newWidth, newHeight)) // Longest side should be 800
    }
    
    @Test
    fun `downscaleImageToMaxDimension should return same image when no scaling needed`() {
        val originalImage = createTestImage(500, 300, Color.RED)
        
        val result = ImageUtils.downscaleImageToMaxDimension(originalImage, 1000)
        
        assertSame(originalImage, result)
        assertEquals(500, result.width)
        assertEquals(300, result.height)
    }
    
    @Test
    fun `downscaleImageToMaxDimension should scale image when larger than max dimension`() {
        val originalImage = createTestImage(1200, 800, Color.BLUE)
        
        val result = ImageUtils.downscaleImageToMaxDimension(originalImage, 1000)
        
        assertNotSame(originalImage, result)
        assertEquals(1000, result.width)
        assertEquals(666, result.height)
    }
    
    @Test
    fun `resizeImage should create new image with correct dimensions`() {
        val originalImage = createTestImage(1000, 500, Color.GREEN)
        
        val result = ImageUtils.resizeImage(originalImage, 400, 200)
        
        assertEquals(400, result.width)
        assertEquals(200, result.height)
        assertEquals(BufferedImage.TYPE_INT_RGB, result.type)
    }
    
    @Test
    fun `convertBufferedImageToJpegBytes should return non-empty byte array`() {
        val image = createTestImage(100, 100, Color.YELLOW)
        
        val result = ImageUtils.convertBufferedImageToJpegBytes(image)
        
        assertTrue(result.isNotEmpty())
        // JPEG files typically start with FF D8 bytes
        assertEquals(0xFF.toByte(), result[0])
        assertEquals(0xD8.toByte(), result[1])
    }
    
    private fun createTestImage(width: Int, height: Int, color: Color): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        graphics.color = color
        graphics.fillRect(0, 0, width, height)
        graphics.dispose()
        return image
    }
}