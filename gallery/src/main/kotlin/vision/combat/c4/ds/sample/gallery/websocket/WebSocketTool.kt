package vision.combat.c4.ds.sample.gallery.websocket

import org.kodein.di.DI
import org.kodein.di.subDI
import vision.combat.c4.ds.sample.gallery.websocket.di.webSocketModule
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketWindow
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolComponent
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams
import vision.combat.c4.ds.sdk.tool.requiredComponent

/**
 * Minimal [AbstractTool] subclass for the live earthquake feed; imports [webSocketModule] (which
 * binds the tool's own OkHttp-engine `HttpClient`) and wires [WebSocketWindow] as the single
 * window component.
 */
internal class WebSocketTool(
    toolContext: ToolContext,
    toolDescriptor: ToolDescriptor,
    parentDI: DI,
    params: ToolParams?,
) : AbstractTool(toolContext, toolDescriptor, parentDI, params) {

    override val di: DI = subDI(super.di) { import(webSocketModule) }

    override val window: ToolComponent.Window by requiredComponent {
        WebSocketWindow()
    }
}
