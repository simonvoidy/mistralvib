# risks-and-licensing.md

# Emberline Launcher — Risk & Licensing Register

## 1. Legal / licensing

| Risk | Level | Assessment | Mitigation |
|---|---|---|---|
| Zalith code reuse | High | Zalith Launcher 2 derives from the PojavLauncher lineage and is GPL-licensed; reusing its code/assets would make Emberline a derivative work and violate the project's own constraints | Zero source or asset reuse; functional behavior studied from public docs and observed behavior only |
| Trademark | Medium | "Minecraft" is a Mojang trademark; usage rules for third-party products are published by Mojang | Independent name (Emberline), clearly unofficial, never styled as a Mojang/Microsoft product |
| LWJGL-class natives | High | The game links LWJGL3; Android needs a compatible port of the GLFW/GL/OpenAL layer | Track upstream license (BSD-class) for any imported component; document origin + license per native component in `docs/licenses/`; prefer building from source with attribution |
| Android JVM builds | High | OpenJDK is GPLv2+Classpath-exception; Android-adapted builds come from third parties | Only redistribute if the provider's terms allow; otherwise user-imported ("bring your own runtime") with checksum display |
| gl4es / ANGLE / Zink | Medium | MIT / BSD-class licenses; compatible with distribution under attribution | Keep license notices; show them in-app (About screen, M8) |
| Microsoft auth client ID | Medium | Public docs warn against using another launcher's Azure client ID (and custom client IDs can trigger child-account verification errors) | Own Azure registration documented in the M5 setup guide; device-code flow, `XboxLive.signin offline_access` scopes only |
| Control-layout interop | Low | JSON file formats are not code; importing user files is standard interop | Original parser for the Zalith layout format; never vendor their editor/parsing code |
| Google Play policies | Medium | `MANAGE_EXTERNAL_STORAGE` and "download executables" are review-sensitive | Default to scoped storage; documented review posture; capability opt-in |

## 2. Technical risks

| Risk | Level | Trigger | Mitigation |
|---|---|---|---|
| JVM refuses `JNI_CreateJavaVM` under bionic (TLS layout, signal handling) | High | M6 | Support multiple JVM build lines; keep bootstrap thin and JVM-version-aware; experimental label |
| LWJGL-class port limitations (GL 4.x features over GLES translation) | High | M6+ | Per-instance renderer allow-list; deny unsupported versions with a clear reason |
| Vulkan driver variance on mid-range SoCs | Medium | M8 | Fallback renderer path; benchmark-driven, no FPS promises |
| Forge binary patching on-device | Medium | M9 | Go/no-go milestone; fallback = documented external installer step |
| Microsoft auth UX on phones (device-code copy/paste) | Low | M5 | Custom Tabs fallback flow already identified |
| Download integrity / partial files | Medium | M3 | Hash-verify everything; atomic temp-file moves; dedup cache |
| Config breakage across milestones | Medium | ongoing | Schema-versioned JSON + migration tests in `core` |

## 3. Explicit non-goals (anti-scope)
- No bundled copies of Mojang game files.
- No anti-cheat evasion or patched Minecraft binaries.
- No desktop UI, no iOS shell.
