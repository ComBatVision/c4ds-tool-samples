package vision.combat.c4.ds.example.tool.window.simple

import org.kodein.di.DI
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolId
import vision.combat.c4.ds.sdk.tool.ToolParams
import vision.combat.c4.ds.tool.sample.window.R

class SimpleToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {

    override val id: ToolId = ID
    override val iconResId: Int = R.drawable.ic_tool
    override val nameResId: Int = R.string.simple_tool_name

    override fun createTool(toolContext: ToolContext, di: DI, params: ToolParams?): AbstractTool {
        return SimpleTool(toolContext, this, di, params)
    }

    companion object {
        val ID = ToolId<SimpleToolDescriptor>()
    }
}
