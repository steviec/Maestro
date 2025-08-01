package maestro.cli.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import kotlinx.serialization.json.*
import maestro.cli.session.MaestroSessionManager
import okio.Buffer
import java.util.Base64
import maestro.cli.mcp.utils.ImageUtils
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

object TakeScreenshotTool {
    fun create(sessionManager: MaestroSessionManager): RegisteredTool {
        return RegisteredTool(
            Tool(
                name = "take_screenshot",
                description = "Take a screenshot of the current device screen",
                inputSchema = Tool.Input(
                    properties = buildJsonObject {
                        putJsonObject("device_id") {
                            put("type", "string")
                            put("description", "The ID of the device to take a screenshot from")
                        }
                    },
                    required = listOf("device_id")
                )
            )
        ) { request ->
            try {
                val deviceId = request.arguments["device_id"]?.jsonPrimitive?.content
                
                if (deviceId == null) {
                    return@RegisteredTool CallToolResult(
                        content = listOf(TextContent("device_id is required")),
                        isError = true
                    )
                }
                
                val result = sessionManager.newSession(
                    host = null,
                    port = null,
                    driverHostPort = null,
                    deviceId = deviceId,
                    platform = null
                ) { session ->
                    val buffer = Buffer()
                    session.maestro.takeScreenshot(buffer, true)
                    val pngBytes = buffer.readByteArray()
                    
                    val pngImage = ImageIO.read(ByteArrayInputStream(pngBytes))
                    val scaledImage = ImageUtils.downscaleImageToMaxDimension(pngImage, 1000)
                    val scaledJpegBytes = ImageUtils.convertBufferedImageToJpegBytes(scaledImage)
                    encodeToBase64(scaledJpegBytes)
                }
                
                val imageContent = ImageContent(
                    data = result,
                    mimeType = "image/jpeg"
                )
                
                CallToolResult(content = listOf(imageContent))
            } catch (e: Exception) {
                CallToolResult(
                    content = listOf(TextContent("Failed to take screenshot: ${e.message}")),
                    isError = true
                )
            }
        }
    }



    private fun encodeToBase64(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }
}