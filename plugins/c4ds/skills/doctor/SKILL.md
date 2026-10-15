---
name: doctor
description: Check the ComBat 4 DS (C4DS) tool development environment - JDK, Android SDK, adb and device, host app installed, Nexus SDK credentials and access, SDK pin vs latest, docs source. Use when a C4DS tool build cannot resolve the SDK, fails with 401/403, the tool does not show up in the host, or the user asks to check their setup.
allowed-tools: Bash(python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py *)
---

# C4DS environment check

```bash
python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py doctor
```

Add `--offline` to skip the request to Nexus. The script reads credentials only to call Nexus
and never prints them. Do not read `gradle.properties` files yourself to check them.

| Check              | Fails when                                      | Fix                                                                                    |
|--------------------|-------------------------------------------------|----------------------------------------------------------------------------------------|
| JDK                | `java` missing or older than 17                 | Install JDK 17+, or use the one bundled with Android Studio (`JAVA_HOME`)              |
| Android SDK        | no `sdk.dir`, `ANDROID_HOME` or default path    | Install with Android Studio; set `sdk.dir` in `local.properties`                       |
| Device (warn)      | no device in `adb devices`                      | Start an emulator or connect a device; needed only to run the tool                     |
| Host app (warn)    | ComBat 4 DS not installed on a device           | Install it from Google Play (`vision.combat.c4.ds`)                                    |
| Nexus credentials  | `c4ds_sdk_username` / `c4ds_sdk_password` unset | Developer adds them to `~/.gradle/gradle.properties`; request at support@combat.vision |
| Nexus access       | 401 wrong credentials, 403 no repo access       | Re-check the values; ask support for `maven-sdk` access                                |
| SDK pin (warn)     | project pin is older than the latest on Nexus   | Use the `update-sdk` skill when the user wants to move                                 |
| Docs source (warn) | no `.c4ds-source.json`                          | Run `c4ds.py sync` to copy `AGENTS.md` + `docs/`                                       |

Report the result as the list of failures and warnings with their fix. Never ask the user to
paste credentials into the chat.

## Tool does not show up in the host

`doctor` passing does not prove the host found the tool. Check, in order:

1. The APK is installed: `adb shell pm list packages <applicationId>`.
2. `AndroidManifest.xml` has the `vision.combat.c4.ds.sdk.DECLARED_TOOLS` meta-data pointing
   to `@xml/combat_tools`.
3. `res/xml/combat_tools.xml` lists the descriptor's fully qualified class name.
4. The host was restarted after the install.
5. `adb logcat` while opening the Tools list, for class-loading errors from the host.

`docs/reference/tool-lifecycle-and-setup.md` explains discovery in detail.
