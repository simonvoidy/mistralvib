# Emberline Launcher

**Emberline** is an original, Android-only launcher for Minecraft: Java Edition.
Touch-first, designed for phones and tablets. **Unofficial** — not affiliated with Mojang
or Microsoft. Zalith Launcher 2 v2.8.2 was used strictly as a *functional* reference
(observed public behavior); no code, assets, or packaged files from it are included.

## Status (do not trust marketing, trust this table)

### Implemented (M0–M1)
- Original project scaffold: `app` (Android, Compose) + `core` (pure-Kotlin, unit-tested).
- Launcher shell: bottom navigation — Home, Instances, Accounts, Java, Controls, Settings.
- Instance data model with validation, schema-versioned JSON persistence, atomic writes.
- Touch control layout schema v0 (joystick/button/dpad/toggle; anchor, size, opacity, mappings)
  with unit-tested serialization.
- Interop, experimental: import adapter for Zalith-style control layout JSON files
  (original parser, unknown fields skipped and reported).
- CI (GitHub Actions): unit tests + debug APK build on every push; release workflow for tags.

### Experimental
- Zalith-layout import: calibrated against the widely documented Pojav-lineage layout JSON
  shape (percent coordinates, `key`/`name`/`size`/`opacity` fields). Real-world layouts may
  include fields this adapter ignores; it never crashes on unknown data.

### Planned (see `docs/03-milestones.md`)
M2 storage & manifest sync · M3 download engine · M4 Java runtime management ·
M5 Microsoft authentication · M6 game process bridge (first game boot) ·
M7 touch control system v1 (editor, profiles, joystick actually usable) ·
M8 renderer selection · M9 mod loaders · M10 performance & benchmarks · M11 release hardening.

**The launcher cannot yet boot the game.** Game boot (M6) is the largest technical milestone;
it requires Android-ported JVM/LWJGL-class native components (see `docs/04-risks-and-licensing.md`).

## Build

Requirements: JDK 17, Android SDK (API 35), internet access; or Android Studio (latest).

```bash
gradle wrapper --gradle-version 8.10.2   # one-time (preinstalled Gradle or IDE)
./gradlew :core:test                     # unit tests (pure JVM, seconds)
./gradlew :app:assembleDebug             # debug APK
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.
CI produces a debug APK artifact on every push and release APKs on `v*` tags.

## Architecture

See `docs/02-architecture.md`. Summary:

- `core` — pure Kotlin: models, JSON persistence, download planning (M3), auth logic (M5),
  control-layout schema and parsers. Fast JVM unit tests, no Android dependency.
- `app` — Android glue: Compose UI, lifecycle, services, storage access.

## Licensing

Emberline code: MIT (see `LICENSE`). Components added in later milestones (JVM builds,
translation layers, renderer natives) keep their upstream licenses and are documented in
`docs/licenses/` when introduced. Minecraft is a trademark of Mojang Synergies AB.
