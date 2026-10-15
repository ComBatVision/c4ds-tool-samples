---
name: update-sdk
description: Move a ComBat 4 DS (C4DS) tool project to a newer SDK - update the SDK, AGP, Kotlin and Gradle pins from the samples repo, refresh AGENTS.md and docs, apply migration notes, rebuild. Use when the user asks to update, bump or upgrade the C4DS SDK, or to refresh the C4DS docs in their project.
argument-hint: [--ref branch-or-tag]
allowed-tools: Bash(python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py *)
---

# Update the C4DS SDK

The samples repo (https://github.com/ComBatVision/c4ds-tool-samples) is the reference: its
`main` branch builds against the latest supported SDK, with matching AGP, Kotlin and Gradle
versions and docs. This skill copies those pins and docs into the project.

## 1. Check the starting point

- `git status` must be clean, so the update is one reviewable diff. If it is not, ask the user
  to commit or stash first.
- Read `.c4ds-source.json` for the ref and commit the project came from.
- Run `python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py doctor`; it shows the pinned SDK and the
  latest one on Nexus.

## 2. Sync

```bash
python3 ${CLAUDE_PLUGIN_ROOT}/scripts/c4ds.py sync --pins [--ref <branch-or-tag>]
```

Without `--pins` (the user asked only for docs) it refreshes `AGENTS.md`, `CLAUDE.md` and
`docs/` and leaves versions alone.

With `--pins` it also updates `combat-ds-sdk`, `agp` and `kotlin` in
`gradle/libs.versions.toml` and the Gradle `distributionUrl`. Other catalog entries are the
project's own; it does not touch them.

## 3. Review the diff

- `git diff AGENTS.md CLAUDE.md`: the files are overwritten. Restore any project-specific
  content the developer had added.
- `git diff docs/`: read the changed sections; they describe new rules and APIs.
- Read the migration guide the script prints, and the samples repo commits between the old and
  the new commit from `.c4ds-source.json`, for API changes that need code changes.
- If the project uses libraries next to the SDK, check
  `docs/reference/host-provided-libraries.md` for version changes the host now provides.

## 4. Rebuild and fix

```bash
./gradlew assembleDebug
```

Fix compile errors with the migration notes and the matching samples code. Then check the
tools against `docs/review-rules.md`. Running on a device needs a host app built with the new
SDK; tell the user if their installed host is older.

## 5. Finish

Summarize the version changes (old to new) and the code changes. Do not commit unless the user
asks.
