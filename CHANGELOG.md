# Slate Changelog

## 0.5.1 → 0.5.2
 
**0.5.2** adds icon shape customization:
 
- **Icon shapes:** Five clipping shapes for app icons: round (default), square, squircle, hexagon, and octagon.
- **Per-app shape:** In edit mode, tap any app and pick a shape in its settings dialog. Works in both freescreen and list modes.
- **Blanket-set shape:** The bulk-edit dialog now includes the shape picker, so you can set all icons to the same shape at once.
- **Unmasked icon rendering:** Adaptive icon drawables are now rendered without the system's circular mask, so square and squircle shapes display correctly instead of clipping an already-circular bitmap.
- **Position-stable resizing:** Changing an icon's size in the edit dialog or via blanket-set now adjusts its stored position so the icon center stays in place relative to guide lines.
- **Fixed stale edit data:** The edit dialog now resolves app data live from DataStore instead of using a snapshot from tap time, preventing position corruption after drag-then-edit sequences.
- **Fixed stale snap math:** The drag gesture handler now recreates when icon size changes, so guideline snapping uses the correct center offset for the current size instead of the original.


## 0.5 → 0.5.1
 
**0.5.1** adds toolbar position snapping:
 
- **Toolbar position:** The toolbar can now sit on any edge of the screen. Set it in settings under "toolbar position."
- Top and bottom positions keep the familiar horizontal layout. Left and right switch to a vertical strip with the "slate" title rotated sideways.
- System bar insets adapt per position: status bar padding on top, navigation bar padding on bottom, both on sides.
- Settings panel now scrolls to accommodate the extra options.

## 0.4.1 → 0.5

**0.5** introduces guide lines for freescreen mode:

- **Guide lines:** In edit mode, swipe inward from the left, right, or bottom edge of the screen to pull out a guide line. Vertical guides come from the sides, horizontal from the bottom. Drag them wherever you want.
- **Snap-to-guide:** Drag an icon near a guide line and its center snaps onto it, constraining movement along the line. Drag perpendicular hard enough and it breaks free.
- **Intersection snapping:** Vertical and horizontal guides are tracked independently per axis, so when two guides cross, an icon snaps to both. You can break free from one axis while staying on the other.
- **Delete by returning:** Drag a guide line back to any screen edge to remove it.
- Guide lines are persisted in DataStore as JSON.

## 0.4.0 → 0.4.1

**0.4.1** adds image wallpapers:

- **Image wallpapers:** Pick any image from your device via the system file picker. The image is copied to internal storage as WebP (scaled down if oversized) so it loads fast and doesn't depend on the original file sticking around.
- **Palette color extraction:** On pick, `androidx.palette` analyzes the image and extracts a dominant color used for auto-contrast text, same as solid/gradient. Stored in the config.
- **Wallpaper picker:** Mode row now shows `solid | gradient | image`. Image mode shows a thumbnail preview and a pick/change button.
- Backward compatible.

## 0.3.2 → 0.4

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