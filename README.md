# C4DS Tool Samples

This repository contains a set of external ComBat 4 Dismounted Soldier tools to help you learn about the integration process. Each sample demonstrates different use cases, complexity levels, and APIs.

<img width="1000" alt="Intro" src="https://github.com/user-attachments/assets/06c47964-0ebf-4cf3-80dc-6ac2a0639202" />

## Disclaimer
This is an early version and may contain bugs or incomplete features.  
Feedback and contributions are welcome!  
If you encounter any issues, please [open an issue](https://github.com/ComBatVision/c4ds-tool-samples/issues).

## Requirements
To try out these sample tools, you need to: 
- have the [ComBat 4 Dismounted Soldier](https://play.google.com/store/apps/details?id=vision.combat.c4.ds) app installed on your device.
- have access to the Maven private repository. You may request access by writing to [support@combat.vision](mailto:support@combat.vision").
- use latest available version of the C4DS SDK. Check the latest version on [Nexus Repository](https://nexus.combat.vision/#browse/browse:maven-sdk:vision%2Fcombat%2Fc4ds-sdk).
- use the same versions of core frameworks and libraries as the host app to maintain binary compatibility:
  
- ```
  kotlin = "2.2.20"
  compose = "1.8.3"
  ```

## Setup
- Add your Nexus credentials to the `gradle.properties` file, either in the `~/.gradle/` directory of your user or in the root of your project:

  ```
  c4ds_sdk_username=<username>
  c4ds_sdk_password=<password>
  ```
- Configure your project to use the Nexus private repository. To do so, add the following code to your root `build.gradle.kts` file:
  ```Kotlin
  subprojects {
      repositories {
          google()
          mavenCentral()
          maven {
              url = uri("https://nexus.combat.vision/repository/maven-sdk/")
              credentials {
                  username = System.getProperty("c4ds_sdk_username") ?: rootProject.properties["c4ds_sdk_username"].toString()
                  password = System.getProperty("c4ds_sdk_password") ?: rootProject.properties["c4ds_sdk_password"].toString()
              }
          }
      }
  }
  ```
- Add dependencies to your `build.gradle` file:
  ```Kotlin
  dependencies {
      compileOnly(libs.combat.ds.sdk)
      runtimeOnly(libs.combat.ds.sdk.runtine)
  }
  ```

## Getting Started

The app divides the screen into several UI areas (Tool Components), each of which can be accessed from your tool:
- **Overlay** (red area) - can be used to display any heads-up content over the main map.
- **Status** (blue area) - can be used to display a small amount of non-critical information. The Status area can also be expandable.
- **Window** (green area) - can be used to build screens that will be displayed on the right (or bottom) panel. It can have nested navigation. There is also a sub-variant of the Window component named `MapWindowComponent`, which is designed to be used with the secondary map in the window.
- **Underlay** (whole area under the main map) - primarily used for AR tools.

The app also allows you to declare custom buttons on the right side of the main map (yellow area). To do so, you should override the `AbstractTool.endBarButtons` property.

<img width="1000" alt="Screen layout" src="https://github.com/user-attachments/assets/109fb109-a89b-4a34-ac54-ea7ab66966dc" />

## Integration
1. Create a new class for your tool (e.g. [OverlayTool](https://github.com/ComBatVision/c4ds-tool-samples/blob/main/overlay/src/main/kotlin/vision/combat/c4/ds/example/tool/overlay/OverlayTool.kt)) that extends `AbstractTool` class.
2. Create a new class for your tool descriptor (e.g., [OverlayToolDescriptor](https://github.com/ComBatVision/c4ds-tool-samples/blob/main/overlay/src/main/kotlin/vision/combat/c4/ds/example/tool/overlay/OverlayToolDescriptor.kt)) that extends `ToolDescriptor` class. It's responsible for providing the tool's name and icon that will be displayed in the **Tools** list as well as creating a new instance of your tool via overridden `createTool` method.
3. Create a new *.xml file (e.g., [combat_tools.xml](https://github.com/ComBatVision/c4ds-tool-samples/blob/main/overlay/src/main/res/xml/combat_tools.xml)) that will list all your tool descriptors.

   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <combat-tools>
       <tool-descriptor name="vision.combat.c4.ds.example.tool.overlay.OverlayToolDescriptor" />
   </combat-tools>
   ```
4. Declare the created `*.xml` file in your `AndroidManifest.xml` as meta-data under the name `vision.combat.c4.ds.sdk.DECLARED_TOOLS`.

   ```xml
   <meta-data
        android:name="vision.combat.c4.ds.sdk.DECLARED_TOOLS"
        android:resource="@xml/combat_tools" />
   ```

## Building
Your project does NOT need to have an Activity. To run the app, simply select `Nothing` in **Launch options**.

<img width="500" alt="Run configuration" src="https://github.com/user-attachments/assets/35dd1cd5-1e49-4419-a4f2-87476c5d7847" />

Once your app is installed, the ComBat 4 Dismounted Soldier app should automatically refresh the **Tools** list and display your tool. The host app usually applies your changes immediately after your tool re-activation; however, in some cases, you may need to kill and restart the app.

<img width="1000" alt="Installed tools" src="https://github.com/user-attachments/assets/019a24eb-0fc9-46aa-b943-7139fb7857e2" />

## Demo
Click to watch

[![Demo video. Click to watch](https://github.com/user-attachments/assets/e077a04b-35c4-4d77-bd7b-672d552a4f26)](https://www.youtube.com/watch?v=V-FoCHOEejs)
