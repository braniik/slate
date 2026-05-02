# Slate

A FOSS minimal Android launcher. Clean by default, yours visually.

⚠️ IN DEVELOPMENT ⚠️

## The idea

Most launchers are too bloated and basically the same thing over and over, so I decided I'll try my take on a launcher :D. Slate does the opposite, you start with nothing and add what you want, where you want it. There will be no widget or news feeds, and definetly not AI. **You just launch apps.**

Freescreen mode is the centerpiece: your home screen is a blank **slate** where icons go anywhere at any coordinate. Guide lines let you impose structure when you want it without forcing a grid. List mode exists for people who prefer ordered rows. Both are fully customizable per-app.

## Screenshots

Coming soon.

## Features

- **Freescreen mode:** Drag icons to any position on screen.
- **List mode:** Vertical or horizontal scrolling list. Drag-to-reorder in edit mode.
- **Guide lines:** In edit mode, swipe from screen edges to create guide lines. Icons snap to guides and slide along them.
- **Wallpapers:** Solid color, gradient (8 directions), or image. Auto-contrast text adapts to whatever you set.
- **Per-app customization:** Icon size, label visibility, text size (list mode), all configurable per individual app.
- **Blanket-set:** Apply icon size or label settings to every app at once.
- **You choose what shows up.** Tap + to add apps, tap the trash to remove. Nothing appears unless you put it there.

## Toolbar modes

| Icon | Mode | What it does |
|------|------|-------------|
| Plus | Adding | Browse installed apps and add them to your home screen |
| Pen | Editing | Tap an app to customize it, drag to reposition (freescreen) or reorder (list). Create and manage guide lines in freescreen |
| Bin | Deleting | Tap an app to remove it from the home screen |
| Cog | Settings | Switch layout mode, change list orientation, customize wallpaper |
| Tune | Blanket-set | Appears in edit mode. Set icon size/labels for all apps at once |

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
├── MainActivity.kt                — activity, edge-to-edge, wallpaper background
├── data/
│   ├── GuideLine.kt               — guide line model, JSON serialization, DataStore persistence
│   ├── LauncherPreferences.kt     — DataStore keys, HomeScreenApp model, settings flows
│   ├── WallpaperConfig.kt         — wallpaper mode/colors/gradient, auto-contrast text color
│   └── WallpaperImageStore.kt     — image save/load/compress, palette extraction
└── ui/
    ├── drawer/
    │   ├── AddAppsOverlay.kt       — scrollable picker for adding apps to home
    │   ├── AppDrawerScreen.kt      — main state hub, wires all modes and dialogs together
    │   ├── AppLoader.kt            — queries PackageManager for installed launcher apps
    │   ├── HomeMode.kt             — NORMAL, ADDING, EDITING, DELETING enum
    │   ├── Toolbar.kt              — top bar with mode toggle buttons
    │   ├── common/
    │   │   ├── BlanketSetDialog.kt — bulk-set icon size/labels for all apps
    │   │   └── EditDialogShell.kt  — reusable dialog frame with save/close
    │   ├── freescreen/
    │   │   ├── FreeScreenIcon.kt       — draggable icon with per-axis guide line snapping
    │   │   ├── FreescreenEditDialog.kt — per-icon size and label toggle
    │   │   ├── GuideLineLayer.kt       — renders, creates, drags, and deletes guide lines
    │   │   └── HomeFreescreen.kt       — freescreen canvas, layers guides behind icons
    │   ├── list/
    │   │   ├── HomeList.kt         — vertical/horizontal list with drag-to-reorder
    │   │   └── ListEditDialog.kt   — per-item text size, icon size, icon toggle
    │   └── settings/
    │       ├── SlateSettingsSheet.kt — layout mode switch, list orientation, wallpaper access
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
- [ ] 0.5.1 — Toolbar snapping (top, bottom, left, right)
- [ ] 0.6 — Icon shapes, icon customization, icon pack support
- [ ] 0.6.1 — Icon rotation
- [ ] 0.6.2 — App folders
- [ ] 0.7 — Polish pass
- [ ] 1.0 — F-Droid release (and other stores, if Android stays open)

## Contributing

Slate is a personal project but contributions are welcome. If you find a bug, have a feature idea, or want to help get it on F-Droid, open an issue or PR.