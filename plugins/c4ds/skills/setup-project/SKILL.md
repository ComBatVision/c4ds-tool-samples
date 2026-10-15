---
name: setup-project
description: Set up a standalone repository for developing ComBat 4 DS (C4DS) external tools - Gradle wrapper, Nexus SDK repository, version pins, AGENTS.md and docs for agents - then scaffold the first tool and build it. Use when the user wants to start C4DS plugin or tool development in a new or empty repo, or asks how to set up the C4DS development environment.
argument-hint: [--module name --package com.example.x]
allowed-tools: Bash(python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py *)
---

# Set up a C4DS tool project

The build setup, version pins and docs are copied from the public samples repo
(https://github.com/ComBatVision/c4ds-tool-samples) at one git ref, so the new project starts
from a state that is known to build. The ref and commit are recorded in `.c4ds-source.json`;
the `update-sdk` skill uses it later.

## 1. Check the machine

Run the `doctor` checks first:

```bash
python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py doctor
```

Fix failures before setup. The usual ones:

- **Nexus credentials missing.** The developer requests them at support@combat.vision, then
  adds them to `~/.gradle/gradle.properties`:

  ```properties
  c4ds_sdk_username=<username>
  c4ds_sdk_password=<password>
  ```

  Never ask for the values, never write them yourself, and never put them in the project.
- **JDK** older than 17, or **Android SDK** not found: point the developer to Android Studio,
  which installs both.

A missing device or host app is only a warning: it matters for running the tool, not building.

## 2. Decide the inputs

- **Directory:** the current repo root, unless the user names another one. It must not have
  a Gradle settings file yet; for that case see "Existing project" below.
- **First tool:** module name and base package (`com.example.weather`). Ask for the package
  if the user did not give one; it becomes the APK's `applicationId`.
- **Ref:** `main` by default. Pass `--ref <branch-or-tag>` only when the user needs an older
  SDK line.

## 3. Run setup

```bash
python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py setup --module <module> --package <package> [--tool Name] [--minimal]
```

Options: `--dir <path>` (default: current directory), `--name <rootProject.name>` (default:
directory name). It creates:

| Path                                        | From                                          |
|---------------------------------------------|-----------------------------------------------|
| `gradlew`, `gradlew.bat`, `gradle/wrapper/` | samples repo                                  |
| `gradle/libs.versions.toml`                 | samples repo (SDK, AGP, Kotlin pins)          |
| `settings.gradle.kts`                       | samples repo, renamed, sample modules removed |
| `build.gradle.kts`, `gradle.properties`     | samples repo                                  |
| `.gitignore`                                | samples repo                                  |
| `AGENTS.md`, `CLAUDE.md`, `docs/`           | samples repo (agent guidance)                 |
| `local.properties`                          | `sdk.dir`, when the Android SDK is found      |
| `.c4ds-source.json`                         | ref + commit the files came from              |
| `<module>/`                                 | the `new-tool` template                       |

It runs `git init` when the directory is not a repository yet.

## 4. Build

```bash
./gradlew :<module>:assembleDebug
```

The first build downloads Gradle, the SDK and the conventions plugin, so it takes a few
minutes. A Nexus 401 here means wrong credentials; run `doctor`.

## 5. Hand over

Tell the developer:

- `AGENTS.md` and `docs/` are the rules for this project; agents read them first.
- `/c4ds:new-tool` adds tools, `/c4ds:doctor` checks the environment, `/c4ds:update-sdk`
  moves to a newer SDK and refreshes the docs.
- Nothing is committed. Suggest a first commit of the generated files; `local.properties` is
  ignored.

## Existing project

When the repo already has a Gradle build, do not run `setup`. Instead:

1. Run `python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py sync --pins` from the project root.
   It copies `AGENTS.md` + `docs/` and adds the `combat-ds-sdk`, `agp` and `kotlin` versions
   to `gradle/libs.versions.toml`. Review `git diff` afterwards: `AGENTS.md` and `CLAUDE.md`
   are overwritten, so merge back any project-specific content they had.
2. Compare the project's `settings.gradle.kts` and catalog `[plugins]` with the samples
   repo's and add what is missing: the Nexus `maven-sdk` repository in `pluginManagement`,
   and the `android-application` and `combat-plugin` plugin aliases the `new-tool` template
   uses.
3. Continue with the `new-tool` skill.
