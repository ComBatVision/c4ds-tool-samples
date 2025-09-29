# C4DS Tool Samples
<img width="1000" alt="image" src="https://github.com/user-attachments/assets/f69f8821-5f4b-4f77-b45e-f037b66679b0" />

This repository contains a set of external ComBat Vision 4.0 tools to help you learn about the integration process. Each sample demonstrates different use cases, complexity levels and APIs.

## Requirements
To try out these sample tools, you need to have ComBat Vision 4.0 app.
It is recommended to use the same version of Kotlin as the host app to maintain binary compatibility.
```
kotlin = "2.2.20"
```

## Getting Started

The app divides the sceen into several UI areas (Tool Components) each of which can be accessed from your tool:
- **Overlay** (red area) - can be used to display any heads-up content over the main map.
- **Status** (blue area) - can be used to display small amount of non-critical information, Status can also be expandable.
- **Window** (green area) - can be used to build screens that will be displayed on the right (bottom) panel. It can have nested navigation. There is also a sub-variant of Window component named `MapWindowCompoent` which is designed to be used with the secondary map in the window.
- **Underlay** (whole area under the main map) - primarily used for AR tools.

The app also allows to declare custom buttons on the right side of the main map (yellow area). To do so, you should override `AbstractTool.endBarButtons` property.

<img width="1000" height="1600" alt="Group 2" src="https://github.com/user-attachments/assets/78bb8036-9e5a-496d-a27d-d7b96382662c" />

## Integration
1. Create a new class of your tool (e.g. `OverlayTool`). It should extend `AbstractTool` class.
2. Create a new class of your tool descriptor (e.g. `OverlayToolDescriptor`). It should provide tool's name and icon that will be displayed in the Tools list. It also overrides `createTool` that should create a new instance of your tool.
3. Create a new *.xml file (e.g. `combat_tools.xml`) that will list all your tool descriptors.
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <combat-tools>
       <tool-descriptor name="vision.combat.c4.ds.example.tool.overlay.OverlayToolDescriptor" />
   </combat-tools>
   ```
4. Declare the created *.xml file in your AndroidManifest.xml as meta-data under the name `vision.combat.c4.ds.sdk.DECLARED_TOOLS`.
   ```xml
   <meta-data
        android:name="vision.combat.c4.ds.sdk.DECLARED_TOOLS"
        android:resource="@xml/combat_tools" />
   ```

## Building
Your project does NOT need to have an Activity. To run the app simply select `Nothing` in **Launch options**

<img width="500" alt="Group 3" src="https://github.com/user-attachments/assets/a5cb4f21-db1a-478b-964a-6ffb81ecce88" />

Once your app is installed, ComBat Vision 4.0 app should automatically refresh the Tools list and display your tool.


