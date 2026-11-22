# requirements-analysis.md

# Emberline Launcher — Technical Requirements Analysis

Status: Phase 1 (analysis). Reference for functional behavior only: Zalith Launcher 2 v2.8.2
(https://github.com/ZalithLauncher/ZalithLauncher2/releases/tag/2.8.2). No source, assets,
or packaged files from that project are used. All behavior below was derived from public
documentation (Minecraft wiki / wiki.vg), public endpoint behavior, and Android platform docs.

## 1. What a Minecraft: Java Edition launcher on Android must actually do

| Capability | Description | Public interface it depends on |
|---|---|---|
| Version metadata | Enumerate Minecraft releases/snapshots, resolve per-version JSON | `https://piston-meta.mojang.com/mc/game/version_manifest_v2.json` |
| Game install | Fetch client jar, libraries, asset index + objects, mappings | `piston-meta.mojang.com`, `piston-data.mojang.com`, `libraries.minecraft.net`, `resources.download.minecraft.net` |
| Java runtime management | Provide/locate JVM 8/17/21 aarch64 runtimes, per-instance selection | Mojang manifest declares `javaVersion` component; the binaries themselves cannot be Mojang's glibc builds on Android — Android-native JVM builds are required |
| Microsoft auth | OAuth 2.0 → Xbox Live → XSTS → Minecraft services token | `login.microsoftonline.com/consumers/oauth2/v2.0`, `user.auth.xboxlive.com/user/authenticate`, `xsts.auth.xboxlive.com/xsts/authorize` (RelyingParty `rp://api.minecraftservices.com/`), `api.minecraftservices.com/authentication/login_with_xbox`, `/entitlements/mcstore`, `/minecraft/profile` |
| Mod loaders | Install Fabric / NeoForge / Forge profiles per instance | Fabric: `meta.fabricmc.net` (installer/launcher-meta API). Forge/NeoForge: official maven InstallerJSON/profile formats |
| Process hosting | Run the actual game on-device | JNI/bionic-hosted JVM (`JNI_CreateJavaVM` on a native thread), EGL surface, LWJGL-compatible native layer |
| Rendering | Select renderer per instance | OpenGL ES over translation layers (e.g. gl4es-class), ANGLE/Zink (Vulkan-backed GL) — per-instance choice |
| Input | Touch overlay, gamepad, keyboard | Virtual overlay injecting into game window; Android input sources |
| Instances | Isolated game dirs, isolated configs | Per-instance `.minecraft`, `options.txt`, saves, mods, config |

## 2. Hard Android constraints (from platform docs/behavior)

1. **Scoped storage.** Android 11+ restricts broad file access. Default layout in
   app-specific external storage (`getExternalFilesDir`) — no permission needed; optional
   custom root via SAF tree URI. `MANAGE_EXTERNAL_STORAGE` is feasible but Play-policy
   restricted (requires justification) — must be a user-facing toggle, off by default.
2. **Process model.** The game runs on a native thread inside our process; `Activity`
   recreation on rotation/config change must not kill the game → `android:configChanges`
   for the game activity, SurfaceView bound to an EGL context that survives re-layout.
   Backgrounding must pause the game loop without losing the JVM.
3. **64-bit only practically.** arm64-v8a primary. armeabi-v7a is not a target: modern
   versions need >3 GB address space and aarch64 JVM builds.
4. **Memory.** Devices ship 6–16 GB but `heap` limits for the *launcher* process apply;
   the game heap is the JVM's (native + JVM heap), so Android's largeHeap flag is largely
   irrelevant to the game itself; the sizing UI maps free RAM → `-Xmx`.
5. **Thermals.** Sustained game load is thermally limited; the benchmark harness must
   measure sustained (≥20 min) frames, not burst.

## 3. Feature-vs-effort reality matrix

| Feature | Feasibility | Notes |
|---|---|---|
| Version list, install, asset/library downloads, instance isolation | High | Pure network + storage work, no native code |
| Microsoft auth (device-code flow) | High | Own Azure app registration required (public doc explicitly warns against reusing another app's client ID; child-account errors clash with custom IDs) |
| Fabric | High | Well-documented installer JSON + launcher meta API |
| NeoForge | Medium | Similar installer data; more moving parts |
| Forge (legacy) | Medium/low | Binary-diff patching + huge classpath; evaluate in dedicated milestone |
| Running the game (JVM boot, LWJGL bridge, window) | Hard | Needs Android-ported JVM + LWJGL-class natives; see risk register |
| Renderer selection (gl4es-class / ANGLE / Zink) | Hard | Native components must be built or imported under their own licenses |
| Touch control editor, profiles, import/export | High (post game-boot) | Pure app-layer work; high effort but low risk |
| Gamepad/keyboard | High | Standard Android input sources |
