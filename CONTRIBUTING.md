# Contributing to Slate

First off, thanks for taking the time to contribute! Slate started as a personal project built out of a desire for a truly minimal, gridless Android launcher.
Whether you're fixing a bug, improving docs, or proposing a feature, your help is welcome.

One thing to set expectations: I'm a 17-year-old student and Slate is a hobby project. Reviews can take days, sometimes a week or two. That is not disinterest, something might just be going on.
Slate 1.1 was slightly delayed because of a driving test for example. Ping the thread if something has gone quiet.

## The scope
 
Slate has one job: **it launches apps.** Everything in the codebase either serves that or just isn't there.
 
**Welcome:**
 
- Bug fixes, especially device or OEM-specific ones. These are the hardest for me to find or test, since I have only one Samsung phone.
- Performance work: cold start, icon rasterization, main-thread stalls.
- Accessibility: potentially language options, touch target sizes, content descriptions, TalkBack, RTL.
- Layout and customization options that stay per-app or blanket-settable.
- Correctness in the existing subsystems: contrast, snapping, work profiles, icon packs.
- Documentation, README fixes, fixing grammar (English is not my 1st language). Genuinely useful.

**Not in scope:**
 
- Widgets.
- News feeds, discover pages, sponsored anything.
- AI features of any kind.
- Analytics, telemetry, crash reporters, remote config.
- Account systems or cloud sync.
- Any dependency that drags in Google Play Services.
- A grid layout with fixed slots. Freescreen is the most important and central part of the project.

If you are unsure whether an idea fits, open an issue before writing code. I would much rather green light something up front than turn down a finished PR. That already took effort.


## How to contribute

The same path whether you're fixing a bug, adding a feature, or correcting a typo. Only step 1 changes.

**1. Open an issue first.**

Say what you want to change and why. For a bug, include the checklist below. For a feature, say what problem it solves and what it would do.

Skip it only when the change is obviously correct and self-contained: a typo, a broken link, a crash with a one-line fix. If you're unsure whether yours counts, then guess it doesn't, open the issue.

**2. Fork, and branch off `master`.**

    git checkout -b your-change-name

**3. Make the change.**

One thing per branch. Two features are two branches and two PRs. Follow the code style below.

**4. Check it builds and runs.**

    ./gradlew test
    ./gradlew installDebug # or assembleDebug

Try it on a real device if you can. Emulators hide layout and gesture problems. Also it's better if you test it with your actual finger I learnt during development.

Unit tests live in `app/src/test/java/com/braniik/slate/` and cover the logic that doesn't need a device:

```
├── AppSearchTest.kt               — match ranking, fuzzy matching, and when a query launches an app
├── GroupDragTest.kt               — group drag: edge limits, solo drags, followers copying the lead
├── GuidePatternTest.kt            — guide regions, halving, no duplicate guides
├── MarqueeSelectionTest.kt        — which icons a selection box catches
├── SwipeUpTest.kt                 — what counts as a swipe up
└── SystemWallpaperContrastTest.kt — light or dark text against the system wallpaper
```

If you add logic like that, add tests for it there.

CI (`.github/workflows/ci.yml`) runs `./gradlew test assembleDebug` on every push and pull request, so you'll know within minutes whether your PR builds.

**5. Open the PR.**

Link the issue. Say what you changed and why, what devices you tested on, and include a screenshot or short recording for anything visual.

### Reporting a bug

I imagine a lot of launcher bugs are gonna be OEM skin bugs, so what phone you're on is the single most useful thing in the report. Please include:

- Device and Android version
- The skin, if any (HyperOS/MIUI, One UI, ColorOS, ...)
- Slate version
- Layout mode and any relevant settings
- What happened, and what you expected instead

## Code style

- **One job per file.** Small files that each do one thing named after it. If a file starts doing two things then split it.
- **Keep logic out of composables.** Calculations like ranking, snapping and bounds go in plain functions so they can be unit tested without a device.
- **Dialogs report and the screen saves.** Only `AppDrawerScreen` writes to `HomeAppsStore`. Dialogs hand their result back through a callback.
- **Lowercase UI text.** "add apps", not "Add Apps".
- **Comments for the why and not the what.** Leave one when something is surprising, or to credit where an idea came from.
- **No new dependencies** beyond AndroidX/Compose without asking in an issue first.

## LLM contributions

I won't be investigating your PR for signs of LLMs, I'd advise against using LLM assistance, I am not sure how well they know Kotlin, but if you do, don't vibecode, use it as an assistant, 
make sure you understand what it does and is properly reviewed. Just to prevent slop code. I am not saying my code or any human's code is perfect but someone actually thinking about the design has a 
higher chance of being closer to the quality than a fancy RNG word generator.
