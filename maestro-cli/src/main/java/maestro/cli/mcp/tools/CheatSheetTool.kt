package maestro.cli.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import maestro.cli.mcp.MaestroTool
import maestro.cli.mcp.schema.McpToolInput
import maestro.cli.mcp.schema.TextOutput

// Schema definitions for this tool
@Serializable
data class CheatSheetInput(
    // No fields needed for empty input
    val dummy: String? = null
) : McpToolInput

object CheatSheetTool {
    fun create(): RegisteredTool {
        return MaestroTool.create<CheatSheetInput, TextOutput>(
            name = "cheat_sheet",
            description = """Get the Maestro cheat sheet with common commands and syntax examples. 
                Returns comprehensive documentation on Maestro flow syntax, commands, and best practices."""
        ) { input ->
            try {
                // Load cheat sheet from resources
                val resourceStream = CheatSheetTool::class.java.getResourceAsStream("/maestro-cheat-sheet.yaml")
                    ?: throw Exception("Cheat sheet resource not found")
                
                val cheatSheetContent = resourceStream.bufferedReader().use { it.readText() }
                
                // Return text-only content without structured data
                TextOutput(cheatSheetContent)
            } catch (e: Exception) {
                throw Exception("Failed to load cheat sheet: ${e.message}")
            }
        }
    }
}