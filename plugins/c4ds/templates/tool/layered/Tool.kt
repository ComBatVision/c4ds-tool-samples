package __PACKAGE__

import org.kodein.di.DI
import org.kodein.di.subDI
import __PACKAGE__.di.__TOOL_CAMEL__Module
import __PACKAGE__.ui.__TOOL__Window
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolComponent
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams
import vision.combat.c4.ds.sdk.tool.requiredComponent

internal class __TOOL__Tool(
    toolContext: ToolContext,
    toolDescriptor: ToolDescriptor,
    parentDI: DI,
    params: ToolParams?,
) : AbstractTool(toolContext, toolDescriptor, parentDI, params) {

    override val di: DI = subDI(super.di) { import(__TOOL_CAMEL__Module) }

    override val window: ToolComponent.Window by requiredComponent {
        __TOOL__Window()
    }
}
