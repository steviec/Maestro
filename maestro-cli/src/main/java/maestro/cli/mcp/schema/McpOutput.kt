package maestro.cli.mcp.schema

import kotlinx.serialization.Serializable

/**
 * Base interface for all MCP tool outputs.
 * This allows tools to explicitly declare their output content type while maintaining type safety.
 */
sealed interface McpOutput

/**
 * Text-only output without structured content.
 * Use this for tools that return human-readable text like documentation, logs, or markdown.
 */
@Serializable
data class TextOutput(val text: String) : McpOutput

/**
 * Image output for tools that return visual content.
 * The data should be base64 encoded image data.
 */
@Serializable
data class ImageOutput(
    val data: String,
    val mimeType: String = "image/jpeg"
) : McpOutput

/**
 * Audio output for tools that return audio content.
 * The data should be base64 encoded audio data.
 */
@Serializable
data class AudioOutput(
    val data: String,
    val mimeType: String
) : McpOutput

/**
 * Marker interface for structured data outputs.
 * Any data class that implements this interface will have both text (JSON) and structuredContent in the response.
 * The text representation is automatically generated from the JSON serialization.
 * 
 * Example:
 * ```
 * @Serializable
 * data class LaunchAppOutput(
 *     val success: Boolean,
 *     val deviceId: String,
 *     val message: String
 * ) : StructuredOutput
 * ```
 */
interface StructuredOutput : McpOutput