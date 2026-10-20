# Fusion Orbit Key

Android app for OPPO Find X9 / ColorOS 16 and Windows App / Fusion 360.

## v0.5.0 (Gboard-compatible diagnostic)

- Detect Volume Down DOWN/UP through AccessibilityService even while Windows App is foreground.
- Record foreground app changes and held state.
- **Does not change the default keyboard.**
- **Does not inject Shift yet.** The Android Accessibility API cannot inject arbitrary keyboard key-down/key-up events into another app. A Shizuku-backed privileged input transport requires separate implementation and validation; `input keyevent --longpress` is not equivalent to holding Shift across mouse drag.
- Does not consume volume keys until an actual Shift transport is proven.

The old experimental `OrbitIme.kt` source is retained for reference but its service is no longer registered in AndroidManifest.xml. Do not select it as the keyboard.

## Test

Enable Fusion Orbit Key Accessibility service. Leave Gboard selected. Open Windows App and hold/release Volume Down. Return to Fusion Orbit Key and copy the event log.

## Build

GitHub Actions -> Android debug APK -> Artifacts -> `FusionOrbitKey-v0.5.0-debug-apk`.
