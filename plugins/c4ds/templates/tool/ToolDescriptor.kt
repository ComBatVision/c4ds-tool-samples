package __PACKAGE__

import org.kodein.di.DI
import __NAMESPACE__.R
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams

class __TOOL__ToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {
    override val nameResId: Int = R.string.__TOOL_SNAKE___tool_name
    override val iconResId: Int = R.drawable.ic___TOOL_SNAKE__
    override val categories: List<String> = emptyList()

    override fun createTool(toolContext: ToolContext, di: DI, params: ToolParams?): AbstractTool =
        __TOOL__Tool(toolContext, this, di, params)
}
