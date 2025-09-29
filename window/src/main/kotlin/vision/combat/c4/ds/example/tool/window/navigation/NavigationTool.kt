package vision.combat.c4.ds.example.tool.window.navigation

import org.kodein.di.DI
import org.kodein.di.subDI
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolComponent
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams
import vision.combat.c4.ds.sdk.tool.requiredComponent
import vision.combat.c4.ds.example.tool.window.navigation.di.navigationToolModule
import vision.combat.c4.ds.example.tool.window.navigation.ui.NavigationToolWindow

internal class NavigationTool(
    toolContext: ToolContext,
    toolDescriptor: ToolDescriptor,
    parentDI: DI,
    params: ToolParams?,
) : AbstractTool(toolContext, toolDescriptor, parentDI, params) {

    override val di: DI = subDI(super.di) {
        import(navigationToolModule)
    }

    override val window: ToolComponent.Window by requiredComponent {
        NavigationToolWindow()
    }
}
