# Slate

A FOSS minimal Android launcher. Clean by default, yours visually.

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

- **Freescreen mode:** In edit mode, drag icons to any position on screen.
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
- **Multi-select:** In freescreen edit mode, drag a box across empty canvas to select several icons, then set their properties in one go. Tap icons to add or drop them from the selection.
- **Guide patterns:** In freescreen edit mode, tap a region and halve it. Symmetry without measuring by eye.
- **Search when adding:** Filter the app picker.
- **Home app switching:** Jump straight to the system screen that picks your home app.
- **You choose what shows up.** Tap + to add apps, tap the trash to remove. Nothing appears unless you put it there.

## Toolbar modes

| Icon | Mode | What it does |
|------|------|-------------|
| Plus | Adding | Browse installed apps and add them to your home screen |
| Pen | Editing | Tap an app to customize it, drag to reposition (freescreen) or reorder (list). Create and manage guide lines in freescreen. Drag across empty canvas to multi-select |
| Border | Guide patterns | Appears in freescreen edit mode. Tap a region to halve it vertically or horizontally |
| Bin | Deleting | Tap an app to remove it from the home screen |
| Cog | Settings | Switch layout mode, change list orientation, set toolbar position, customize wallpaper, select icon pack |
| Tune | Blanket-set | Appears in edit mode. Set icon size/shape/rotation/labels for every app at once, or for just the current multiselection |

## Installation

## Installation

> **Xiaomi / HyperOS users:** setting any third-party launcher, including Slate, as default forces your phone back to three-button navigation. See [Device quirks](#device-quirks).

### F-Droid

You can download it from [here](https://f-droid.org/en/packages/com.braniik.slate/)

### Github Release

Grab the signed apk from the [latest release](https://github.com/braniik/slate/releases/latest)

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
│   ├── GuidePattern.kt            — the grid guide lines carve out of the screen, and halving a region of it
│   ├── HomeAppsStore.kt           — runtime owner of the home app list: edits apply in memory synchronously, DataStore trails as a write-behind mirror
│   ├── IconPackManager.kt         — icon pack discovery, appfilter.xml parsing, icon resolution
│   ├── LauncherPreferences.kt     — DataStore keys, HomeScreenApp model (package + profile identity), settings flows
│   ├── LauncherRole.kt            — the HOME role: who holds it, requesting it, and the system screen that reassigns it
│   ├── PackageChanges.kt          — flow of app/profile changes via LauncherApps.Callback
│   ├── SystemWallpaperApplier.kt  — renders wallpaper config to the system wallpaper
│   ├── WallpaperConfig.kt         — wallpaper mode/colors/gradient, per-edge text color for solid/gradient
│   ├── WallpaperImageStore.kt     — image save (unique file per pick)/load/compress/prune, palette extraction
│   └── WallpaperSampler.kt        — image wallpaper edge-strip sampling behind the toolbar and system bars
└── ui/
    ├── SystemBars.kt               — status/navigation bar icon appearance (light vs dark)
    ├── drawer/
    │   ├── AddAppsOverlay.kt       — searchable picker for adding apps to home
    │   ├── AppActions.kt           — acting on apps: launch, open the system App Info sheet
    │   ├── AppSearch.kt            — match ranking for the picker's search bar
    │   ├── AppIcon.kt              — rememberAppIcon: an app's icon rasterized once per size, reused across recompositions
    │   ├── AppDrawerScreen.kt      — wires modes, dialogs, back handling, home reset, every home list edit goes through HomeAppsStore
    │   ├── AppLoader.kt            — LauncherApps query across profiles; AppInfo holds the Drawable and renders badged, unmasked bitmaps on demand
    │   ├── HomeMode.kt             — NORMAL, ADDING, EDITING, DELETING enum
    │   ├── HomeUiState.kt          — ephemeral mode/overlay/selection state, its two transitions: back (peel topmost) and home (reset)
    │   ├── Toolbar.kt              — position-aware toolbar
    │   ├── common/
    │   │   ├── BlanketSetDialog.kt — bulk-set icon size/shape/rotation/labels for all apps
    │   │   ├── EditDialogShell.kt  — reusable dialog frame with save/close
    │   │   ├── IconShape.kt        — shape definitions and picker
    │   │   └── SearchField.kt      — single-line input tinted by the wallpaper's own foreground
    │   ├── freescreen/
    │   │   ├── FreeScreenIcon.kt       — draggable icon (edit mode), AxisSnap handles guide line snapping per axis
    │   │   ├── FreescreenEditDialog.kt — per-icon size, shape, rotation, and label toggle
    │   │   ├── GuideDraw.kt            — the one place guide lines get painted
    │   │   ├── GuideLineLayer.kt       — creates, drags, and deletes guide lines
    │   │   ├── GuidePatternLayer.kt    — tap a region and halve it
    │   │   ├── MarqueeLayer.kt         — drag-a-box multi-select over empty canvas
    │   │   └── HomeFreescreen.kt       — freescreen canvas, orders the gesture layers
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
- [x] 0.9 — Pre-release 
- [x] 1.0 — F-Droid release (and other stores, if Android stays open)
- [x] 1.0.1 — Bugfixes from the first weeks on F-Droid
- [x] 1.1 — QoL bundle: picker search, guide patterns, multi-select, home app switching

## Contributing

Contributions are welcome, see [CONTRIBUTING.md](CONTRIBUTING.md) for scope, build instructions, and style. If you're unsure whether an idea fits, open a nissue first.

## Device quirks

- **MIUI / HyperOS:** Xiaomi builds gesture navigation into its own System Launcher, so setting any third-party launcher as default forces the phone back to three-button navigation. 
This has been the case since MIUI 12 and still holds on HyperOS. No launcher can work around it. See [Niagara Launcher's notes](https://help.niagaralauncher.app/article/7-gesture-navigation).
Slate's settings has a shortcut to the system home app picker under **home app** if you want to switch back.
