# Slate

A minimal Android launcher. Clean by default, yours visually.

⚠️⚠️ STILL IN DEVELOPMENT ⚠️⚠️

## The idea

A simple free and open source minimal launcher with tailored experience in mind

## 0.4.0 → 0.4.1

**0.4.1** adds image wallpapers:

- **Image wallpapers:** Pick any image from your device via the system file picker. The image is copied to internal storage as WebP (scaled down if oversized) so it loads fast and doesn't depend on the original file sticking around.
- **Palette color extraction:** On pick, `androidx.palette` analyzes the image and extracts a dominant color used for auto-contrast text, same as solid/gradient. Stored in the config.
- **Wallpaper picker:** Mode row now shows `solid | gradient | image`. Image mode shows a thumbnail preview and a pick/change button.
- Backward compatible — existing configs load fine with image fields defaulting to empty.

## 0.3.2 → 0.4.0

**0.4.0** adds wallpaper support, the first visual customization beyond layout:

- **Solid color wallpapers:** Pick any color via hex input or RGB sliders. Defaults to the original black `#080808`.
- **Gradient wallpapers:** Toggle to gradient mode and configure two color stops independently. Choose from 8 directions via an arrow grid: cardinal and diagonal.
- **Auto-contrast text:** App labels, the toolbar title, and toolbar icons adapt between white and dark automatically based on the luminance of the wallpaper behind them.
- Wallpaper config is persisted in DataStore alongside the rest of launcher preferences. Existing installs default to solid `#080808`.

## 0.3.1 → 0.3.2

**0.3.2** adds drag-to-reorder in list mode:

- **Drag reordering:** In edit mode (pen icon), drag any app to rearrange it. Tap still opens the per-app settings dialog, drag moves it, same behavior as freescreen, constrained to one axis. Non-dragged items animate into place on swap.
- Scroll is disabled while in edit mode so drag gestures don't fight the list. Exit edit mode to scroll normally.

## 0.3 → 0.3.1

**0.3.1** adds in-app settings and bulk customization:

- **Settings panel:** Gear icon in the toolbar opens a settings sheet where you can switch between freescreen and list mode. Switching resets arrangements with a confirmation warning. List orientation is also adjustable here.
- **Blanket-set:** A Tune icon appears next to the pen in edit mode. Tap it to set icon size, text size, or show/hide labels for all apps at once.
- **Reorder arrows removed** from list mode. A better reordering solution is planned for a future update.

## 0.2 → 0.3

**0.3** expands list mode customization:

- **Vertical or horizontal list:** Setup now asks list users which direction they want. Horizontal mode lays out icon-on-top, label-below and scrolls left/right.
- **Per-app icon size in list mode:** The edit dialog (pen) now controls icon size (20–64dp) alongside text size. Freescreen and list icon sizes are independent.

## 0.1 → 0.2

**0.1** was me checking I could make a working launcher at all. It worked, so now I can experiment with my visions and opinions in **0.2+**

**0.2** is the first real move toward what Slate is supposed to be:

- **Freescreen mode option:** No grid or snapping. Drag any icon to any coordinate. Your home screen is a canvas. A blank **slate** even ;)
- **You curate what shows up.** Instead of dumping every installed app on screen, you add the ones you want. With a toolbar on the top that you can use to either add, edit, or delete apps from the drawer.
- **List mode still exists** for when you want ordered rows. Reorder with arrow buttons in edit mode, resize text per entry, toggle icons per entry. Might add dragging since it's for phones, so far arrows were convenient for a VM.

## Stack

- Kotlin + Jetpack Compose (Material 3)
- DataStore (Preferences) for persistence, with `HomeScreenApp` entries serialized as JSON
- Min SDK 29 (Android 10)
- No third-party deps beyond AndroidX/Compose
