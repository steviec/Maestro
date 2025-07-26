package maestro.cli.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import maestro.cli.mcp.MaestroTool
import maestro.cli.mcp.schema.McpToolInput
import maestro.cli.mcp.schema.ImageOutput
import maestro.cli.session.MaestroSessionManager
import okio.Buffer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

// Schema definitions for this tool
@Serializable
data class DeviceIdInput(
    val deviceId: String
) : McpToolInput

object TakeScreenshotTool {
    fun create(sessionManager: MaestroSessionManager): RegisteredTool {
        return MaestroTool.create<DeviceIdInput, ImageOutput>(
            name = "take_screenshot",
            description = "Take a screenshot of the current device screen"
        ) { input ->
            val imageData = sessionManager.newSession(
                host = null,
                port = null,
                driverHostPort = null,
                deviceId = input.deviceId,
                platform = null
            ) { session ->
                val buffer = Buffer()
                session.maestro.takeScreenshot(buffer, true)
                val pngBytes = buffer.readByteArray()

                // Convert PNG to JPEG
                val pngImage = ImageIO.read(ByteArrayInputStream(pngBytes))
                val jpegOutput = ByteArrayOutputStream()
                ImageIO.write(pngImage, "JPEG", jpegOutput)
                val jpegBytes = jpegOutput.toByteArray()

                Base64.getEncoder().encodeToString(jpegBytes)
            }

            // Return image output
            ImageOutput(
                data = imageData,
                mimeType = "image/jpeg"
            )
        }
    }
}