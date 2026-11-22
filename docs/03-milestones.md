# milestone-plan.md

# Emberline Launcher — Milestone Plan

Rule: one milestone at a time. Each milestone, before merge:
1. Compiles (CI: `assembleDebug` + unit tests green).
2. Ships tests proportional to the surface it touches.
3. Documents known limitations in `docs/limitations/` .
4. Preserves all prior working functionality (CI runs full test suite).

## Status legend
`[done]` [planned] [experimental] — never label work "implemented" when it is experimental or planned.

## M0 — Repository, docs, CI, identity  `[done]`
- Project identity (name: Emberline, package `com.emberline`), module layout, Gradle version catalog.
- CI: GitHub Actions — JDK 17, Android SDK, `app:assembleDebug` + `core:test`; Release workflow builds signed/debug APK artifacts on tags.
- Docs: analysis, architecture, risks/licensing, benchmark plan.

## M1 — Launcher shell (this commit)  `[planned → CI will mark done when green]`
- Compose shell: bottom navigation (Home, Instances, Accounts, Java, Controls, Settings).
- Instance model + JSON persistence in `core` module (pure Kotlin, unit-tested).
- Control layout schema v0 + (de)serialization + Zalith-layout import adapter skeleton (interop only).
- Limitations: no game functionality yet; screens are functional shells over the data layer.

## M2 — Storage & version metadata  `[planned]`
- App-specific storage layout, SAF custom-root option.
- piston-meta manifest sync w/ ETag caching; version list UI backed by it.
- Tests: manifest parsing, path resolution, cache invalidation.

## M3 — Download engine  `[planned]`
- Concurrent, resumable, sha1-verified fetch of client jar, libraries, asset objects.
- Rule-based argument expansion (Mojang "rules" objects), per-instance dedup of shared assets.
- Tests: planner decisions (skip-if-hash-match, retry, checksum fail), rule evaluation.

## M4 — Java runtime management  `[plans experimental]`
- Import/scan runtime packages, per-instance `javaVersion` mapping, RAM allocation UI.
- Runtime distribution channel(s) documented with licenses; game-boot not yet wired.

## M5 — Microsoft authentication  `[planned]`
- Device-code flow, refresh tokens in Keystore-backed storage, entitlement+profile fetch.
- Offline-mode accounts (clearly labeled).
- Tests: response models, refresh logic, token redaction in logs.

## M6 — Game process bridge  `[planned, experimental]`
- Native bootstrap (`JNI_CreateJavaVM` on pthread, env isolation per instance), surface handoff, stdout/stderr capture UI.
- Ships only with user-provided runtime binaries; never enabled by default.

## M7 — Touch control system v1  `[planned]`
- Overlay: joystick, buttons, multi-touch, per-profile position/size/opacity,Profiles import/export (own schema) + Zalith-layout import.
- Editor: drag & drop, snap, live preview.

## M8 — Renderer integration  `[planned, experimental]`
- Renderer abstraction + selection UI; gl4es-class translation layer first, ANGLE/Zink next.
- Graphics settings (resolution scale, fps cap, smooth lighting bridged to game options).

## M9 — Mod loaders  `[planned]`
- Fabric install per instance; then NeoForge; Forge evaluated with go/no-go.
- Mod & resource pack manager (install, enable/disable, dedupe).

## M10 — Performance engineering  `[planned]`
- Frame pacing (Choreographer + present fences), allocation audits, GC tuning surface,
  input-latency path audit, thermal-aware logging.
- Repeatable benchmark harness + first published numbers on a declared device matrix
  (none until then — no FPS promises).

## M11 — Release hardening  `[planned]`
- R8 rules, crash reporting choice, migration paths, final docs + signed release builds.
