# Gamekt Pro — Dragon Core Interactive

## Overview
Gamekt Pro is an Android application (refactored from GameNative) that allows users to play games they own on Steam, Epic, and GOG directly on Android devices, with cloud saves. Branded with a red dragon identity — Dragon Core Interactive.

## Architecture
- **Type**: Android mobile application (APK)
- **App Name**: Gamekt Pro
- **Application ID**: app.gamektpro
- **Primary Language**: Kotlin (Jetpack Compose UI)
- **Native Code**: C/C++ via Android NDK (Wine, Box86/Box64, virglrenderer, proot)
- **Build System**: Gradle 8.12.1 with Kotlin DSL (`.gradle.kts`)
- **Package Manager**: Gradle with Version Catalogs (`gradle/libs.versions.toml`)
- **Target Device**: Moto G35 / Unisoc T760 chipset, Android 14
- **Target SDK**: 35

## Project Structure
```
app/                     - Main Android application module
  src/main/
    java/app/gamenative/ - Kotlin/Java source code
      managers/          - NEW: ThermalOptimizationManager, GraphicsOptimizer, TranslationManager
      ui/
        component/       - DragonBackgroundView, DragonBackground (new animated dragon layer)
        screen/
          admin/         - AdminPanelScreen (ADM button & admin dashboard)
          marketplace/   - MercadoLivreScreen (Mercado Livre WebView)
    cpp/                 - Native C/C++ code (Wine, Box86, virglrenderer)
    assets/              - Binary assets (box86_64, dxvk, fexcore, wine)
ubuntufs/                - Ubuntu rootfs module
gradle/                  - Gradle wrapper and version catalog
server/                  - Java HTTP info server (Replit preview — shows Gamekt Pro branding)
keyvalues/               - Game configuration key-value files
media/                   - Marketing assets and screenshots
```

## New Modules (All Added in Gamekt Pro Refactor)

### 🐉 Dragon Background (Ultra Priority Visual)
- `DragonBackgroundView.kt` — Android Canvas-based animated dragon
  - Breathing animation (ValueAnimator)
  - Periodic fire-spitting
  - Touch/eye tracking via `setTouchPosition(x, y)`
  - Random reactions: roar, blink, smoke, sparkles
- `DragonBackground.kt` — Composable wrapper (`AndroidView`)
- Integrated in `PluviaMain.kt` with `Modifier.pointerInput` for touch tracking

### 🔑 Admin Panel (Module 2)
- `AdminPanelScreen.kt` — Full admin panel with login/dashboard
- Fixed **ADM** circular button (bottom-right, zIndex=50) in main UI
- Access from anywhere in the app

### 🛒 Mercado Livre Store (Module 2)
- `MercadoLivreScreen.kt` — WebView loading https://meli.la/2GBs9LR
- New nav route: `PluviaScreen.MercadoLivre`
- Accessible from Admin Panel dashboard

### 🗣️ AI Translation + TTS (Module 3)
- `TranslationManager.kt` — Android TTS (TextToSpeech) with PT-BR locale
- Simple PT-BR translation dictionary overlay
- `TranslationOverlayButton` composable — floating button with TTS card overlay

### 🚀 Thermal Optimization + Graphics (Module 4)
- `ThermalOptimizationManager.kt` — Polls `/sys/class/thermal/` for temperature
  - 42°C = CRITICAL: throttles CPU, limits to 30 FPS
  - 38°C = HOT: medium throttle, 45 FPS limit
- `GraphicsOptimizer.kt` — Detects game type (RPG / Heavy 3D) and generates
  optimal resolution/FPS/DXVK config for Unisoc T760 / Moto G35

## Building the Android App
Requires Android Studio with:
- Android SDK (compileSdk 35)
- Android NDK version 22.1.7171670
- CMake (for native C/C++ compilation)

```bash
./gradlew assembleDebug
```

Optional `local.properties`:
```
STEAMGRIDDB_API_KEY=your_key_here
```

## Replit Environment
A lightweight Java HTTP server (`server/ProjectServer.java`) serves the Gamekt Pro info page at port 5000.
Workflow command: `cd server && java ProjectServer`

## Git Repository
- Original: https://github.com/utkarshdalal/GameNative
- Gamekt Pro fork: https://github.com/AndrewHayesGraves/GamektPro.git

## GitHub Push Commands (Module 0)
Run these in the Replit Shell terminal to push to GitHub:
```bash
git remote add gamektpro https://TOKEN@github.com/AndrewHayesGraves/GamektPro.git
git add .
git commit -m "Refactor: GameNative → Gamekt Pro — Dragon Core Interactive"
git push -u gamektpro main
```
(Replace TOKEN with your PAT before running)

## External Dependencies
- JavaSteam (Steam protocol implementation)
- Dagger Hilt (dependency injection)
- Jetpack Room (local database)
- Jetpack Compose (UI framework)
- PostHog (analytics)
