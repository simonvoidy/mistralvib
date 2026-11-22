# benchmark-plan.md

# Emberline Launcher — Performance Benchmark Plan

Rule: **no FPS or latency numbers are published until this harness exists and has run on
a declared device matrix.** No promises of improvements without repeatable measurements.

## 1. Method (repeatable)
- Device matrix: 3 tiers declared here before any M10 results: flagship
  (Snapdragon 8-gen-class), mainstream (Snapdragon 7xx / Dimensity 8xxx), entry
  (6xx / Helio G-series) — each published with exact model, OS build, GPU driver version.
- Fixed scene: dedicated benchmark world (fixed seed), scripted camera path with a
  deterministic input replay.
- Configuration parity: same instance, render distance, resolution scale, renderer,
  vsync setting per comparison.
- Sessions: 5 × 20-minute runs per configuration after a 10-minute thermal soak;
  first run discarded.
- Metrics: mean / 1% / 0.1% frame time (ms), sustained FPS, input-to-photon latency
  (touch timestamp → next presented frame timestamp), GC pauses/min, allocated MB/min,
  end-of-run device skin temperature, mAh consumed.
- Reporting: raw CSV committed under `docs/benchmarks/`, summaries generated only from
  those CSVs; every published claim links to its dataset.

## 2. Instrumentation
- Frame pacing: `Choreographer` frame timestamps + `EGL_ANDROID_present_fence`.
- Allocations: profiler heap dumps + `Debug.getMemoryInfo` sampling.
- Input latency: monotonic timestamp of injected touch vs presentation timestamp of the
  frame containing the resulting game-state change.
- Thermal: battery temperature + sustained-FPS degradation curve.

## 3. Optimization checklist (measure first, then change)
Swap/present strategy; texture upload churn (avoids re-uploads per frame);
launcher-side per-frame allocations (target: near-zero steady state on the input path);
JVM GC option surface per runtime; background work paused while foreground play is active;
game-attach cold start time.

## 4. Anti-promises
- No emulator-originated device numbers.
- No cross-launcher comparisons without running this harness on both, on the same device.
