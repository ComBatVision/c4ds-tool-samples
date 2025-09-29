package vision.combat.c4.ds.example.tool.overlay

import org.kodein.di.DI
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams
import vision.combat.c4.ds.tool.sample.overlay.R

class OverlayToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {

    override val iconResId: Int = R.drawable.ic_tool
    override val nameResId: Int = R.string.overlay_tool_name

    override fun createTool(toolContext: ToolContext, di: DI, params: ToolParams?): AbstractTool {
        return OverlayTool(toolContext, this, di, params)
    }
}
