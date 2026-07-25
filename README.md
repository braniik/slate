# Slate

A FOSS minimal Android launcher. Clean by default, yours visually.

⚠️ IN DEVELOPMENT ⚠️

## The idea

Most launchers are too bloated and basically the same thing over and over, so I decided I'll try my take on a launcher :D. Slate does the opposite, you start with nothing and add what you want, where you want it. There will be no widget or news feeds, and definetly not AI. **You just launch apps.**

Freescreen mode is the centerpiece: your home screen is a blank **slate** where icons go anywhere at any coordinate. Guide lines let you impose structure when you want it without forcing a grid. List mode exists for people who prefer ordered rows. Both are fully customizable per-app.

## Screenshots

<p align="center">
    <img width="40%" height="1036" alt="Screenshot_20260704_085217" src="https://github.com/user-attachments/assets/119c57e3-af9a-4915-8b28-9b872272f736" />
    <img width="40%" height="1040" alt="Screenshot_20260704_091834" src="https://github.com/user-attachments/assets/0139dff8-d370-4f90-9ebb-262412a336b2" />
</p>
<p align="center">
    <img width="40%" height="1033" alt="Screenshot_20260704_084615" src="https://github.com/user-attachments/assets/07593843-1059-4d08-973b-3f3d90afbba0" />
    <img width="40%" height="1036" alt="Screenshot_20260704_092240" src="https://github.com/user-attachments/assets/7bb6c226-3d9f-427f-a393-b5141d11aa60" />
</p>

## Features

- **Freescreen mode:** Drag icons to any position on screen.
- **List mode:** Vertical or horizontal scrolling list. Drag-to-reorder in edit mode.
- **Guide lines:** In edit mode, swipe from screen edges to create guide lines. Icons snap to guides and slide along them.
- **Wallpapers:** Solid color, gradient (8 directions), or image. Auto-contrast text adapts to whatever you set.
- **System bar contrast:** Status bar and navigation bar icons auto-adapt to the light/dark wallpapers, it is the same logic as toolbar.
- **Toolbar position:** Snap the toolbar to any screen edge.
- **Custom title:** Set your own toolbar title or leave it blank to hide it.
- **Per-app customization:** Icon size, label visibility, text size (list mode), icon shape, rotation, all configurable per individual app.
- **Icon shapes:** Round, square, squircle, hexagon, octagon. Pick per icon or blanket-set all at once.
- **Icon rotation:** Rotate any icon from -360° to 360°. Shape rotates with the icon. Per-app or blanket-set.
- **Icon packs:** Supports all major pack formats (ADW, Nova, Apex, GO, Tesla)
- **Work profiles:** Apps from managed profiles (Shelter, Island) appear alongside personal apps, badged with the system work indicator, and launch into the right profile.
- **Blanket-set:** Apply icon size, shape, rotation, or label settings to every app at once.
- **You choose what shows up.** Tap + to add apps, tap the trash to remove. Nothing appears unless you put it there.

## Toolbar modes

| Icon | Mode | What it does |
|------|------|-------------|
| Plus | Adding | Browse installed apps and add them to your home screen |
| Pen | Editing | Tap an app to customize it, drag to reposition (freescreen) or reorder (list). Create and manage guide lines in freescreen |
| Bin | Deleting | Tap an app to remove it from the home screen |
| Cog | Settings | Switch layout mode, change list orientation, set toolbar position, customize wallpaper, select icon pack |
| Tune | Blanket-set | Appears in edit mode. Set icon size/shape/rotation/labels for all apps at once |

## Installation

Slate is not on F-Droid yet. For now, build from source.

### Build from source

1. Clone the repo
```bash
git clone https://github.com/braniik/slate.git
```

2. Open in Android Studio

3. Build and run on your device or emulator (min SDK 29 / Android 10)

## Stack

- Kotlin + Jetpack Compose (Material 3)
- DataStore (Preferences) for all persistence, models serialized as JSON
- `androidx.palette` for wallpaper color extraction
- Min SDK 29 (Android 10), target SDK 36
- No third-party deps beyond AndroidX/Compose

## Project structure

```
├── MainActivity.kt                — activity, edge-to-edge, wallpaper background, system bar appearance, home-intent reset signal
├── data/
│   ├── Contrast.kt                — WCAG relative luminance, the single light/dark foreground decision
│   ├── GuideLine.kt               — guide line model, JSON serialization, DataStore persistence
│   ├── HomeAppsStore.kt           — runtime owner of the home app list: edits apply in memory synchronously, DataStore trails as a write-behind mirror
│   ├── IconPackManager.kt         — icon pack discovery, appfilter.xml parsing, icon resolution
│   ├── LauncherPreferences.kt     — DataStore keys, HomeScreenApp model (package + profile identity), settings flows
│   ├── LauncherRole.kt            — HOME role check and request intent (RoleManager)
│   ├── PackageChanges.kt          — flow of app/profile changes via LauncherApps.Callback
│   ├── SystemWallpaperApplier.kt  — renders wallpaper config to the system wallpaper
│   ├── WallpaperConfig.kt         — wallpaper mode/colors/gradient, per-edge text color for solid/gradient
│   ├── WallpaperImageStore.kt     — image save/load/compress, palette extraction
│   └── WallpaperSampler.kt        — image wallpaper edge-strip sampling behind the toolbar and system bars
└── ui/
    ├── SystemBars.kt               — status/navigation bar icon appearance (light vs dark)
    ├── drawer/
    │   ├── AddAppsOverlay.kt       — scrollable picker for adding apps to home
    │   ├── AppActions.kt           — acting on apps: launch, open the system App Info sheet
    │   ├── AppDrawerScreen.kt      — wires modes, dialogs, back handling, home reset, every home list edit goes through HomeAppsStore
    │   ├── AppLoader.kt            — LauncherApps query across profiles, badged work icons, unmasked rasterization
    │   ├── HomeMode.kt             — NORMAL, ADDING, EDITING, DELETING enum
    │   ├── HomeUiState.kt          — ephemeral mode/overlay state, its two transitions: back (peel topmost) and home (reset)
    │   ├── Toolbar.kt              — position-aware toolbar
    │   ├── common/
    │   │   ├── BlanketSetDialog.kt — bulk-set icon size/shape/rotation/labels for all apps
    │   │   ├── EditDialogShell.kt  — reusable dialog frame with save/close
    │   │   └── IconShape.kt        — shape definitions and picker
    │   ├── freescreen/
    │   │   ├── FreeScreenIcon.kt       — draggable icon with per-axis guide line snapping
    │   │   ├── FreescreenEditDialog.kt — per-icon size, shape, rotation, and label toggle
    │   │   ├── GuideLineLayer.kt       — renders, creates, drags, and deletes guide lines
    │   │   └── HomeFreescreen.kt       — freescreen canvas, layers guides behind icons
    │   ├── list/
    │   │   ├── HomeList.kt         — vertical/horizontal list with drag-to-reorder
    │   │   └── ListEditDialog.kt   — per-item text size, icon size, shape, rotation, icon toggle
    │   └── settings/
    │       ├── SlateSettingsSheet.kt — layout mode switch, list orientation, toolbar position, title, wallpaper access, icon pack selection
    │       └── WallpaperPicker.kt    — solid/gradient/image wallpaper configuration
    ├── setup/
    │   └── SetupScreen.kt          — first-launch layout picker
    └── theme/
        ├── Color.kt                — color palette (#080808 base)
        ├── Theme.kt                — Material 3 dark theme
        └── Type.kt                 — typography
```

## Roadmap

- [x] 0.1 — Proof of concept, basic launcher
- [x] 0.2 — Freescreen mode, curated app list, toolbar
- [x] 0.3 — List mode customization, horizontal/vertical, per-app icon size
- [x] 0.3.1 — In-app settings, blanket-set
- [x] 0.3.2 — Drag-to-reorder in list mode
- [x] 0.4 — Wallpapers (solid, gradient, auto-contrast)
- [x] 0.4.1 — Image wallpapers, palette extraction
- [x] 0.5 — Guide lines for freescreen
- [x] 0.5.1 — Toolbar snapping (top, bottom, left, right)
- [x] 0.5.2 — Icon shapes (round, square, squircle, hexagon, octagon)
- [x] 0.6 — Icon pack support
- [x] 0.6.1 — Icon rotation
- [x] 0.7 — Polish and refinement
- [x] 0.8 — Production readiness
- [ ] 0.9 — Pre-release 
- [ ] 1.0 — F-Droid release (and other stores, if Android stays open)

