package maestro.cli.mcp.utils

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.max

object ImageUtils {
    
    fun downscaleImageToMaxDimension(image: BufferedImage, maxDimension: Int): BufferedImage {
        val (newWidth, newHeight) = calculateScaledDimensions(image.width, image.height, maxDimension)
        
        if (newWidth == image.width && newHeight == image.height) {
            return image
        }
        
        return resizeImage(image, newWidth, newHeight)
    }

    fun calculateScaledDimensions(originalWidth: Int, originalHeight: Int, maxDimension: Int): Pair<Int, Int> {
        val currentMaxDimension = max(originalWidth, originalHeight)
        
        return if (currentMaxDimension > maxDimension) {
            val scale = maxDimension.toDouble() / currentMaxDimension
            Pair((originalWidth * scale).toInt(), (originalHeight * scale).toInt())
        } else {
            Pair(originalWidth, originalHeight)
        }
    }

    fun resizeImage(originalImage: BufferedImage, newWidth: Int, newHeight: Int): BufferedImage {
        val scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
        val bufferedScaledImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedScaledImage.createGraphics()
        graphics.drawImage(scaledImage, 0, 0, null)
        graphics.dispose()
        return bufferedScaledImage
    }

    fun convertBufferedImageToJpegBytes(image: BufferedImage): ByteArray {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "JPEG", outputStream)
        return outputStream.toByteArray()
    }
}