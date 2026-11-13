
package com.pedidos.api_pedidos.config;

import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class McpDiagnostics implements ApplicationRunner {

    private final SyncMcpToolCallbackProvider mcpTools;

    public McpDiagnostics(
            SyncMcpToolCallbackProvider mcpTools) {
        this.mcpTools = mcpTools;
    }

    @Override
    public void run(ApplicationArguments args) {

        var tools = mcpTools.getToolCallbacks();

        System.out.println("=== MCP DIAGNOSTICS ===");
        System.out.println("Tools encontradas: " + tools.length);

        for (var tool : tools) {
            System.out.println(
                "Tool: " + tool.getToolDefinition().name()
            );
        }
    }
}
