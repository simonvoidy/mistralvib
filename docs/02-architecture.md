# architecture.md

# Emberline Launcher — Architecture

## 1. Design principles

1. **Android-first**: touch-first Compose UI, lifecycle-aware services, no desktop shell.
2. **Original implementation**: every subsystem implemented from public interface contracts.
3. **Layered, testable core**: all logic that does not need Android lives in a pure-Kotlin
   module (`core`) that builds and unit-tests on the JVM — fast CI, no emulator needed for logic.
4. **Strict feature status labels**: implemented / experimental / planned in README and docs.
5. **Interop without copying**: third-party file formats (control layouts, installer JSON)
   are supported via original parsers over public formats, not by vendoring code.

## 2. Module map

```
emberline/
├── app/        Android app (Compose UI, lifecycle, services) — the only Android module
├── core/       Pure Kotlin JVM library: models, storage, download planning, auth logic,
│               control-layout schema + parsers, rule evaluation
└── future      M6+: core-native/ (JNI bootstrap), game-bridge/ (window + input into game)
```

Rationale: `core` has zero Android dependencies, so every data transform
(manifest parsing, download planning, control-profile editing, token refresh)
is unit-testable in CI in seconds. `app` contains only UI, Android glue, and IO.

## 3. Core layers (implemented or planned per milestone)

### 3.1 Storage (M2)
```
files rooted at app-specific external storage (scoped-storage safe):
$
 <root>/
 ├── runtime/                  Java runtimes (exact layout documented in M4)
 ├── accounts/                 tokens (Keystore-encrypted), cached profiles
 ├── libraries/                shared dependency cache (hash-addressed)
 ├── assets/indexes|objects/   shared across instances
 └── instances/
      └── <id>/
          ├── instance.json    EmberlineInstanceConfig (schema-versioned)
          ├── .minecraft/      game data dir (saves, options, mods, resourcepacks)
          └── logs/
```
- Optional user-selected custom root via SAF (tree URI persisted in settings).
- `MANAGE_EXTERNAL_STORAGE` opt-in toggle in Settings (M2), default off.

### 3.2 Version metadata & download engine (M2–M3)
- `VersionManifestSource`: fetches `version_manifest_v2`, ETag-based caching.
- `DownloadPlanner`: input = version JSON + instance config; output = incremental,
  checksum-verified fetch plan (skip if hash matches, retry, dedupe by sha1).
- Rule engine: evaluates Mojang `rules` (os/features) to produce JVM/game argument lists.
- OkHttp-class client through `core` via `java.net.http`/OkHttp abstraction, allowlisted hosts only.

### 3.3 Accounts (M5)
- MS device-code flow (sensible for touch devices; the authorization-code flow needs a
  redirect target that is awkward on Android without Custom Tabs — Custom Tabs variant can follow).
- Chain: MSA token → XBL (`user.auth.xboxlive.com`) → XSTS (`xsts.auth.xboxlive.com`,
  RelyingParty `rp://api.minecraftservices.com/`) → `login_with_xbox` → entitlements → profile.
- Tokens stored via Android Keystore (app side), keys never present in `core`.

### 3.4 Game bridge (M6, experimental)
- Native `lib/gameboot.so`: `dlopen` the runtime's `libjvm.so`, `JNI_CreateJavaVM` on a
  dedicated pthread, `AttachCurrentThread` for the render thread, then invoke `net.minecraft.client.main.Main`.
- Window: `SurfaceView` + EGL (surface preserved across config changes).
- Output capture: pipes for stdout/stderr shown in a console screen.
- All behind a feature flag; disabled unless a valid runtime + LWJGL-compatible layer
  are installed by the user.

### 3.5 Touch control system (M7)
- Schema v0 (own format, `emberline-controls-v0` JSON):
  elements: `joystick`, `button`, `dpad`, `toggle`, `slider`;
  props: anchor, pixels/percent coordinates, size, opacity, key/mouse mapping, pointer capture mode.
- Profiles: named, per-instance default, hot-swappable in-game.
- Editor: Compose overlay with drag & drop, per-element property editor, opacity/size sliders.
- Import: Zalith control-layout JSON (public file format) → mapped to schema v0 by an
  original adapter; unknown elements are skipped and reported. Export: schema v0.

### 3.6 Performance engineering (M10)
- Renderer abstraction: `Renderer(id, supports: glVersionRange, backend: gles|vulkan)`
  with per-instance selection; first backends gl4es-class (GLES) and ANGLE (Vulkan).
- Frame pacing: Choreographer-driven swap + `EGL_ANDROID_present_fence`; fps cap
  implemented at swap granularity.
- Budgets: measure, don't promise — see `docs/05-benchmark-plan.md`.

## 4. Key flows

- **Install flow**: pick version → resolve JSON → plan download → execute (progress UI)
  → apply loader (Fabric first) → instance ready.
- **Launch flow**: instance → validate files → resolve account token (refresh if stale)
  → spawn game bridge with per-instance env → hand surface → stream logs.

## 5. Failure & telemetry posture

- No third-party telemetry. Crash logs stay on-device and are user-exported.
- All network traffic restricted to allowlisted official hosts (their names in `core`).
