package maestro.cli.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import maestro.cli.mcp.MaestroTool
import maestro.cli.mcp.schema.McpToolInput
import maestro.cli.mcp.schema.StructuredOutput
import maestro.orchestra.yaml.YamlCommandReader

// Schema definitions for this tool
@Serializable
data class CheckFlowSyntaxInput(
    val flowYaml: String
) : McpToolInput

@Serializable
data class CheckFlowSyntaxOutput(
    val valid: Boolean,
    val message: String,
    val error: String? = null
) : StructuredOutput

object CheckFlowSyntaxTool {
    fun create(): RegisteredTool {
        return MaestroTool.create<CheckFlowSyntaxInput, CheckFlowSyntaxOutput>(
            name = "check_flow_syntax",
            description = "Check your YAML flow syntax to verify correctness. Use this before running flows to ensure they are well-formed and free of syntax errors. ",
        ) { input ->
            try {
                YamlCommandReader.checkSyntax(input.flowYaml)
                CheckFlowSyntaxOutput(
                    valid = true,
                    message = "Flow syntax is valid"
                )
            } catch (e: Exception) {
                CheckFlowSyntaxOutput(
                    valid = false,
                    message = "Syntax check failed",
                    error = e.message ?: "Unknown parsing error"
                )
            }
        }
    }
}