# Slate

A minimal Android launcher. Clean by default, yours visually.

⚠️⚠️ STILL IN DEVELOPMENT ⚠️⚠️

## The idea

A simple free and open source minimal launcher with tailored experience in mind

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
- No third-party deps beyond AndroidX/Compose\
