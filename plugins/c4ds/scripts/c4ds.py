#!/usr/bin/env python3
"""C4DS plugin-development helper used by the c4ds Claude Code plugin skills.

Commands:
  setup     Create a Gradle project for C4DS tools in an empty directory.
  new-tool  Add a tool: a new plugin APK module, or another tool in an existing module.
  sync      Refresh the vendored AGENTS.md + docs/, and optionally the version pins.
  doctor    Check JDK, Android SDK, adb, device, host app, Nexus credentials and access.

The build setup, version pins and docs are copied from the public samples repo
(https://github.com/ComBatVision/c4ds-tool-samples) at a git ref, so a new project always
matches a published, buildable state of the samples.

Run `c4ds.py <command> --help` for options.
"""

from __future__ import annotations

import argparse
import base64
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import urllib.error
import urllib.request
from pathlib import Path

PLUGIN = Path(__file__).resolve().parents[1]
TEMPLATES = PLUGIN / "templates"
SAMPLES_REPO = "https://github.com/ComBatVision/c4ds-tool-samples.git"
SAMPLES_WEB = "https://github.com/ComBatVision/c4ds-tool-samples"
NEXUS_SDK = "https://nexus.combat.vision/repository/maven-sdk"
CONVENTIONS_MARKER = "vision/combat/c4/ds/vision.combat.c4.ds.gradle.plugin/maven-metadata.xml"
HOST_PACKAGES = ("vision.combat.c4.ds", "vision.combat.c4.ds.mdm")
CREDENTIAL_KEYS = ("c4ds_sdk_username", "c4ds_sdk_password")
SOURCE_FILE = ".c4ds-source.json"

# What `setup` and `sync` copy from the samples repo. Docs are the generic, tool-author ones
# that AGENTS.md links to; the samples' own guides are left out.
DOC_PATHS = ("AGENTS.md", "CLAUDE.md", "docs/architecture", "docs/reference", "docs/testing",
             "docs/review-rules.md")
BUILD_PATHS = ("gradlew", "gradlew.bat", "gradle/wrapper", "gradle/libs.versions.toml",
               "gradle.properties", ".gitignore", "build.gradle.kts")
PINNED_VERSIONS = ("combat-ds-sdk", "agp", "kotlin")


def fail(message: str) -> None:
    sys.exit(f"error: {message}")


def rel(path: Path, root: Path) -> str:
    try:
        return str(path.relative_to(root))
    except ValueError:
        return str(path)


# ---------------------------------------------------------------------------------------------
# Samples repo

class Samples:
    """A sparse, shallow checkout of the samples repo at one ref."""

    def __init__(self, ref: str, source: str | None):
        self.ref = ref
        self._tmp = None
        if source:
            self.root = Path(source).resolve()
            self.commit = self._git("rev-parse", "HEAD", cwd=self.root)
            return
        self._tmp = tempfile.TemporaryDirectory(prefix="c4ds-samples-")
        self.root = Path(self._tmp.name) / "samples"
        print(f"Fetching {SAMPLES_WEB} @ {ref} ...")
        self._git("clone", "--quiet", "--depth", "1", "--branch", ref, "--filter=blob:none",
                  "--sparse", SAMPLES_REPO, str(self.root))
        self._git("sparse-checkout", "set", "docs", "gradle", cwd=self.root)
        self.commit = self._git("rev-parse", "HEAD", cwd=self.root)

    @staticmethod
    def _git(*args: str, cwd: Path | None = None) -> str:
        result = subprocess.run(["git", *args], cwd=cwd, capture_output=True, text=True)
        if result.returncode != 0:
            fail(f"git {' '.join(args[:2])} failed: {result.stderr.strip()}")
        return result.stdout.strip()

    def copy(self, path: str, target_root: Path) -> None:
        src = self.root / path
        dst = target_root / path
        if not src.exists():
            fail(f"{path} not found in samples @ {self.ref}")
        if src.is_dir():
            if dst.exists():
                shutil.rmtree(dst)
            shutil.copytree(src, dst)
        else:
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, dst)
        print(f"  + {path}")

    def link_outside(self, target_root: Path) -> None:
        """Point links to files that were not copied (guides, sample code) at GitHub."""
        link = re.compile(r"\]\((?!https?:|mailto:|#)([^)\s#]+)(#[^)\s]*)?\)")
        for doc in DOC_PATHS:
            files = [target_root / doc] if doc.endswith(".md") else (target_root / doc).rglob("*.md")
            for md in files:
                here = md.parent.relative_to(target_root)

                def fix(match: re.Match) -> str:
                    target = os.path.normpath(here / match.group(1))
                    if (target_root / target).exists() or target.startswith(".."):
                        return match.group(0)
                    kind = "tree" if (self.root / target).is_dir() else "blob"
                    url = f"{SAMPLES_WEB}/{kind}/{self.commit}/{target}"
                    return f"]({url}{match.group(2) or ''})"

                md.write_text(link.sub(fix, md.read_text()))

    def versions(self) -> dict:
        return read_versions(self.root / "gradle/libs.versions.toml")

    def close(self) -> None:
        if self._tmp:
            self._tmp.cleanup()


def read_versions(toml: Path) -> dict:
    versions = {}
    in_versions = False
    for line in toml.read_text().splitlines():
        stripped = line.strip()
        if stripped.startswith("["):
            in_versions = stripped == "[versions]"
            continue
        match = re.match(r'([\w-]+)\s*=\s*"([^"]+)"', stripped)
        if in_versions and match:
            versions[match.group(1)] = match.group(2)
    return versions


def write_source(project: Path, samples: Samples) -> None:
    data = {"repo": SAMPLES_WEB, "ref": samples.ref, "commit": samples.commit}
    (project / SOURCE_FILE).write_text(json.dumps(data, indent=2) + "\n")
    print(f"  + {SOURCE_FILE}")


def find_project(start: Path) -> Path:
    for path in [start, *start.parents]:
        if (path / "settings.gradle.kts").exists():
            return path
    fail(f"No settings.gradle.kts in {start} or its parents. Run `setup` first.")


# ---------------------------------------------------------------------------------------------
# setup

def cmd_setup(args: argparse.Namespace) -> None:
    project = Path(args.dir).resolve()
    project.mkdir(parents=True, exist_ok=True)
    if (project / "settings.gradle.kts").exists() or (project / "settings.gradle").exists():
        fail(f"{project} already has a Gradle settings file. Merge the setup by hand; see "
             "the setup-project skill, section 'Existing project'.")
    name = args.name or project.name
    if not re.fullmatch(r"[A-Za-z0-9._-]+", name):
        fail(f"Invalid project name {name!r}")

    samples = Samples(args.ref, args.samples)
    try:
        print(f"Creating C4DS tool project in {project}")
        for path in BUILD_PATHS + DOC_PATHS:
            samples.copy(path, project)
        samples.link_outside(project)
        (project / "gradlew").chmod(0o755)

        settings = (samples.root / "settings.gradle.kts").read_text()
        settings = re.sub(r'rootProject\.name\s*=\s*"[^"]*"', f'rootProject.name = "{name}"', settings)
        settings = re.sub(r'^include\(.*\)\n', "", settings, flags=re.MULTILINE).rstrip() + "\n"
        (project / "settings.gradle.kts").write_text(settings)
        print("  + settings.gradle.kts")

        write_local_properties(project)
        write_source(project, samples)
        versions = samples.versions()
    finally:
        samples.close()

    if not (project / ".git").exists():
        subprocess.run(["git", "init", "--quiet"], cwd=project, check=True)
        print("  + git init")

    print("\nPinned: " + ", ".join(f"{k} {versions.get(k, '?')}" for k in PINNED_VERSIONS))
    if args.module:
        print()
        scaffold(project, args.module, args.tool, args.package, args.label, args.minimal)


def write_local_properties(project: Path) -> None:
    target = project / "local.properties"
    if target.exists():
        return
    sdk = android_sdk(None)
    if sdk:
        target.write_text(f"sdk.dir={sdk}\n")
        print("  + local.properties (sdk.dir, git-ignored)")


def android_sdk(project: Path | None) -> Path | None:
    if project and (project / "local.properties").exists():
        for line in (project / "local.properties").read_text().splitlines():
            if line.startswith("sdk.dir="):
                return Path(line.split("=", 1)[1].strip())
    for var in ("ANDROID_HOME", "ANDROID_SDK_ROOT"):
        if os.environ.get(var):
            return Path(os.environ[var])
    for guess in (Path.home() / "Library/Android/sdk", Path.home() / "Android/Sdk"):
        if guess.exists():
            return guess
    return None


# ---------------------------------------------------------------------------------------------
# new-tool

def snake(name: str) -> str:
    return re.sub(r"(?<!^)(?=[A-Z])", "_", name).lower()


def render(template: str, tokens: dict) -> str:
    text = (TEMPLATES / template).read_text()
    for key, value in tokens.items():
        text = text.replace(f"__{key}__", value)
    leftover = re.findall(r"__[A-Z_]+__", text)
    if leftover:
        fail(f"Unresolved tokens in {template}: {sorted(set(leftover))}")
    return text


def cmd_new_tool(args: argparse.Namespace) -> None:
    scaffold(find_project(Path.cwd()), args.module, args.tool, args.package, args.label,
             args.minimal)


def scaffold(project: Path, module: str, tool: str | None, package: str | None,
             label: str | None, minimal: bool) -> None:
    def write(path: Path, content: str) -> None:
        if path.exists():
            fail(f"Refusing to overwrite {rel(path, project)}")
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content)
        print(f"  + {rel(path, project)}")

    def insert_before(path: Path, marker: str, block: str) -> None:
        text = path.read_text()
        if marker not in text:
            fail(f"Marker {marker!r} not found in {rel(path, project)}")
        idx = text.rindex(marker)
        path.write_text(text[:idx] + block + text[idx:])
        print(f"  ~ {rel(path, project)}")

    if not re.fullmatch(r"[a-z][a-z0-9-]*", module):
        fail("Module must be lowercase, e.g. weather or weather-radar")
    tool = (tool or module.replace("-", " ").title().replace(" ", "")).removesuffix("Tool")
    if not re.fullmatch(r"[A-Z][A-Za-z0-9]*", tool):
        fail("ToolName must be PascalCase, e.g. Weather")

    module_dir = project / module
    new_module = not module_dir.exists()
    if new_module:
        if not package:
            fail("A new module needs --package, e.g. com.example.weather")
        namespace = package
    else:
        build = (module_dir / "build.gradle.kts").read_text()
        match = re.search(r'namespace\s*=\s*"([^"]+)"', build)
        if not match:
            fail(f"No namespace in {module}/build.gradle.kts")
        namespace = match.group(1)
    if not re.fullmatch(r"[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+", namespace):
        fail(f"Invalid package {namespace!r}")

    # A module named after its only tool keeps the tool in the base package.
    last = namespace.rsplit(".", 1)[-1]
    tool_pkg = namespace if new_module and last == tool.lower() else f"{namespace}.{tool.lower()}"
    tokens = {
        "NAMESPACE": namespace,
        "PACKAGE": tool_pkg,
        "TOOL": tool,
        "TOOL_CAMEL": tool[0].lower() + tool[1:],
        "TOOL_SNAKE": snake(tool),
        "MODULE": module,
        "LABEL": label or f"ComBat {tool}",
    }
    src = module_dir / "src/main"
    res = src / "res"
    pkg_dir = src / "kotlin" / Path(*tool_pkg.split("."))
    if pkg_dir.exists():
        fail(f"Package {tool_pkg} already exists in {module}")

    print(f"{'Creating module' if new_module else 'Adding tool to'} :{module} ({tool_pkg})")
    if new_module:
        write(module_dir / "build.gradle.kts", render("module/build.gradle.kts", tokens))
        write(module_dir / ".gitignore", render("module/gitignore", tokens))
        write(module_dir / "proguard-rules.pro", render("module/proguard-rules.pro", tokens))
        write(src / "AndroidManifest.xml", render("module/AndroidManifest.xml", tokens))
        write(res / "drawable/ic_launcher.xml", render("module/ic_launcher.xml", tokens))
        write(res / "values/strings.xml", render("module/strings.xml", tokens))
        write(res / "xml/combat_tools.xml", render("module/combat_tools.xml", tokens))
        settings = project / "settings.gradle.kts"
        text = settings.read_text()
        line = f'include(":{module}")\n'
        if line not in text:
            settings.write_text(text.rstrip("\n") + "\n" + line)
            print(f"  ~ {rel(settings, project)}")

    variant = "minimal" if minimal else "layered"
    files = {
        f"{tool}ToolDescriptor.kt": "tool/ToolDescriptor.kt",
        f"{tool}Tool.kt": f"tool/{variant}/Tool.kt",
        f"ui/{tool}Window.kt": "tool/Window.kt",
        f"ui/{tool}ViewModel.kt": f"tool/{variant}/ViewModel.kt",
    }
    if not minimal:
        files.update({
            f"di/{tool}Module.kt": "tool/layered/Module.kt",
            f"domain/{tool}Interactor.kt": "tool/layered/Interactor.kt",
            f"domain/repository/{tool}Repository.kt": "tool/layered/Repository.kt",
            f"data/{tool}RepositoryImpl.kt": "tool/layered/RepositoryImpl.kt",
        })
    for target, template in files.items():
        write(pkg_dir / target, render(template, tokens))

    write(res / f"drawable/ic_{snake(tool)}.xml", render("tool/ic_tool.xml", tokens))
    insert_before(res / "values/strings.xml", "</resources>", render("tool/strings.xml", tokens))
    insert_before(res / "xml/combat_tools.xml", "</combat-tools>",
                  f'    <tool-descriptor name="{tool_pkg}.{tool}ToolDescriptor" />\n')
    print(f"\nNext: ./gradlew :{module}:assembleDebug")


# ---------------------------------------------------------------------------------------------
# sync

def cmd_sync(args: argparse.Namespace) -> None:
    project = find_project(Path.cwd())
    toml = project / "gradle/libs.versions.toml"
    samples = Samples(args.ref, args.samples)
    try:
        print("Docs:")
        for path in DOC_PATHS:
            samples.copy(path, project)
        samples.link_outside(project)
        if args.pins:
            print("Pins:")
            update_pins(project, toml, samples)
        write_source(project, samples)
    finally:
        samples.close()
    print("\nReview with `git diff`. Migration notes: "
          f"{SAMPLES_WEB}/blob/{args.ref}/docs/guides/migrating-to-the-gradle-plugin.md")


def update_pins(project: Path, toml: Path, samples: Samples) -> None:
    if not toml.exists():
        fail("No gradle/libs.versions.toml; create one with a [versions] table first")
    new = samples.versions()
    text = toml.read_text()
    if "[versions]\n" not in text:
        text = "[versions]\n\n" + text
    old = read_versions(toml)
    for key in PINNED_VERSIONS:
        if key not in new:
            continue
        if old.get(key) == new[key]:
            print(f"  = {key} {new[key]}")
            continue
        if key in old:
            text = re.sub(rf'^({re.escape(key)}\s*=\s*)"[^"]*"', rf'\g<1>"{new[key]}"', text,
                          flags=re.MULTILINE)
        else:
            text = text.replace("[versions]\n", f'[versions]\n{key} = "{new[key]}"\n', 1)
        print(f"  ~ {key} {old.get(key, '-')} -> {new[key]}")
    toml.write_text(text)

    wrapper = "gradle/wrapper/gradle-wrapper.properties"
    url = re.compile(r"^distributionUrl=.*$", re.MULTILINE)
    new_url = url.search((samples.root / wrapper).read_text()).group(0)
    current = (project / wrapper).read_text()
    if url.search(current).group(0) != new_url:
        (project / wrapper).write_text(url.sub(lambda _: new_url, current))
        print(f"  ~ {wrapper}: {new_url.rsplit('/', 1)[-1]}")


# ---------------------------------------------------------------------------------------------
# doctor

class Report:
    def __init__(self):
        self.failures = 0

    def ok(self, label: str, detail: str = "") -> None:
        print(f"  ok    {label}{': ' + detail if detail else ''}")

    def warn(self, label: str, detail: str) -> None:
        print(f"  warn  {label}: {detail}")

    def fail(self, label: str, detail: str) -> None:
        self.failures += 1
        print(f"  FAIL  {label}: {detail}")


def run(*cmd: str) -> subprocess.CompletedProcess | None:
    try:
        return subprocess.run(cmd, capture_output=True, text=True, timeout=30)
    except (FileNotFoundError, subprocess.TimeoutExpired):
        return None


def gradle_properties(project: Path | None) -> dict:
    props = {}
    files = [Path(os.environ.get("GRADLE_USER_HOME", Path.home() / ".gradle")) / "gradle.properties"]
    if project:
        files.insert(0, project / "gradle.properties")
    for path in files:
        if not path.exists():
            continue
        for line in path.read_text().splitlines():
            if "=" in line and not line.lstrip().startswith("#"):
                key, value = line.split("=", 1)
                props.setdefault(key.strip(), value.strip())
    for key in CREDENTIAL_KEYS:
        env = os.environ.get(f"ORG_GRADLE_PROJECT_{key}")
        if env:
            props[key] = env
    return props


def cmd_doctor(args: argparse.Namespace) -> None:
    project = None
    for path in [Path.cwd(), *Path.cwd().parents]:
        if (path / "settings.gradle.kts").exists():
            project = path
            break
    report = Report()
    print(f"Project: {project or 'none found (checks the machine only)'}")

    java = run("java", "-version")
    if not java:
        report.fail("JDK", "java not on PATH; install JDK 17 or newer")
    else:
        match = re.search(r'version "(\d+)', java.stderr)
        major = int(match.group(1)) if match else 0
        if major >= 17:
            report.ok("JDK", f"{major}")
        else:
            report.fail("JDK", f"found {major or 'unknown'}, need 17 or newer")

    sdk = android_sdk(project)
    if sdk and sdk.exists():
        report.ok("Android SDK", str(sdk))
    else:
        report.fail("Android SDK", "set ANDROID_HOME or sdk.dir in local.properties")

    adb = shutil.which("adb") or (str(sdk / "platform-tools/adb") if sdk else None)
    devices = []
    if adb and Path(adb).exists():
        out = run(adb, "devices")
        devices = [line.split()[0] for line in (out.stdout if out else "").splitlines()[1:]
                   if line.strip().endswith("device")]
        if devices:
            report.ok("Device", ", ".join(devices))
        else:
            report.warn("Device", "none connected; needed only to run the tool")
    else:
        report.warn("adb", "not found; install Android SDK platform-tools")

    for serial in devices:
        out = run(adb, "-s", serial, "shell", "pm", "list", "packages", "vision.combat.c4.ds")
        packages = {line.removeprefix("package:").strip()
                    for line in (out.stdout if out else "").splitlines()}
        host = sorted(packages.intersection(HOST_PACKAGES))
        if host:
            report.ok(f"Host app on {serial}", ", ".join(host))
        else:
            report.warn(f"Host app on {serial}", "ComBat 4 DS not installed")

    props = gradle_properties(project)
    missing = [key for key in CREDENTIAL_KEYS if not props.get(key)]
    if missing:
        report.fail("Nexus credentials", f"missing {', '.join(missing)} in "
                    "~/.gradle/gradle.properties (request them at support@combat.vision)")
    elif args.offline:
        report.ok("Nexus credentials", "present")
    else:
        check_nexus(report, props, project / "gradle/libs.versions.toml" if project else None)

    # The samples repo is the docs source itself, so it has nothing vendored to check.
    if project and not (project / "plugins/c4ds").exists():
        source = project / SOURCE_FILE
        if source.exists():
            data = json.loads(source.read_text())
            report.ok("Docs source", f"{data.get('ref')} @ {data.get('commit', '')[:10]}")
        else:
            report.warn("Docs source", f"no {SOURCE_FILE}; run `sync` to vendor AGENTS.md + docs")

    print(f"\n{'All required checks passed.' if not report.failures else f'{report.failures} check(s) failed.'}")
    sys.exit(1 if report.failures else 0)


def check_nexus(report: Report, props: dict, toml: Path | None) -> None:
    token = base64.b64encode(
        f"{props[CREDENTIAL_KEYS[0]]}:{props[CREDENTIAL_KEYS[1]]}".encode()).decode()
    request = urllib.request.Request(f"{NEXUS_SDK}/{CONVENTIONS_MARKER}",
                                     # Nexus answers 403 to Python's default User-Agent.
                                     headers={"Authorization": f"Basic {token}",
                                              "User-Agent": "c4ds-doctor"})
    try:
        with urllib.request.urlopen(request, timeout=15) as response:
            body = response.read().decode(errors="replace")
        match = re.search(r"<release>([^<]+)</release>", body)
        latest = match.group(1) if match else "unknown"
        report.ok("Nexus access", f"latest SDK {latest}")
        pinned = read_versions(toml).get("combat-ds-sdk") if toml and toml.exists() else None
        if pinned and match and pinned != latest:
            report.warn("SDK pin", f"project pins {pinned}, Nexus has {latest}; see update-sdk")
    except urllib.error.HTTPError as error:
        reason = {401: "credentials rejected", 403: "no access to maven-sdk"}.get(
            error.code, f"HTTP {error.code}")
        report.fail("Nexus access", reason)
    except (urllib.error.URLError, TimeoutError) as error:
        report.fail("Nexus access", f"unreachable ({getattr(error, 'reason', error)})")


# ---------------------------------------------------------------------------------------------

def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)

    def samples_options(p: argparse.ArgumentParser) -> None:
        p.add_argument("--ref", default="main", help="samples repo branch or tag (default: main)")
        p.add_argument("--samples", help=argparse.SUPPRESS)  # local checkout, for testing

    def tool_options(p: argparse.ArgumentParser, module_required: bool) -> None:
        if module_required:
            p.add_argument("module", help="module dir; created if missing")
        else:
            p.add_argument("--module", help="also scaffold a first tool module")
        p.add_argument("--tool", help="PascalCase tool name (default: from module)")
        p.add_argument("--package", help="base package of a new module, e.g. com.example.weather")
        p.add_argument("--label", help="app label of a new module")
        p.add_argument("--minimal", action="store_true", help="window + ViewModel only")

    p = sub.add_parser("setup", help="create a C4DS tool project")
    p.add_argument("--dir", default=".", help="project dir (default: current)")
    p.add_argument("--name", help="rootProject.name (default: dir name)")
    samples_options(p)
    tool_options(p, module_required=False)
    p.set_defaults(func=cmd_setup)

    p = sub.add_parser("new-tool", help="add a tool to the project in the current dir")
    tool_options(p, module_required=True)
    p.set_defaults(func=cmd_new_tool)

    p = sub.add_parser("sync", help="refresh vendored docs; --pins also updates versions")
    p.add_argument("--pins", action="store_true", help="update SDK/AGP/Kotlin pins and wrapper")
    samples_options(p)
    p.set_defaults(func=cmd_sync)

    p = sub.add_parser("doctor", help="check the development environment")
    p.add_argument("--offline", action="store_true", help="skip the Nexus request")
    p.set_defaults(func=cmd_doctor)

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
