# Ascend — Level Up Your Real Life

Real-life RPG productivity app. Kotlin, Jetpack Compose, MVVM + Clean Architecture, Hilt, Room.

**Status:** Foundation milestone. Not a runnable full app yet — this is the architectural
skeleton plus one working vertical slice (Player Profile + Quest system + Dashboard +
Quests screen), matching the approved PRD and the mockup you provided.

---

## How to open

1. Android Studio (Ladybug or newer recommended for AGP 8.7 / Kotlin 2.0 compatibility).
2. `File > Open` → select this folder.
3. **Gradle Wrapper**: `gradle/wrapper/gradle-wrapper.properties` is included (pins Gradle
   8.9, matching AGP 8.7.2), but `gradlew`, `gradlew.bat`, and `gradle-wrapper.jar` are
   NOT — those require either a network download or a locally-installed Gradle to
   generate, neither of which this sandbox had. **This is a 10-second fix**: Android
   Studio detects the missing wrapper on first open and offers to regenerate it
   automatically — accept that prompt. (Command-line alternative if you have Gradle
   installed: run `gradle wrapper --gradle-version 8.9` once in this folder.)
4. Let Gradle sync. **This sandbox has no network access**, so dependency versions in
   `gradle/libs.versions.toml` haven't been verified against the live Maven Central index —
   accept Android Studio's "newer version available" prompts if it offers any during sync.
5. Run on an emulator/device with API 26+.

## What's actually implemented

- **Domain layer**: `PlayerProfile`, `Quest`, `Rank`, `AttributeType`, `XpCurve` (level 1–100
  curve), `CalculateXpUseCase` (full multiplier engine — streak/combo/boss/difficulty),
  `CompleteQuestUseCase`, `Habit`/`HabitMasteryCurve`, `StreakState`, `FocusSettings`.
- **Data layer**: Room entities + DAOs for Player Profile, Quests, Habits, Streak state,
  and Inventory; DataStore-backed Focus Lock settings; repository implementations; Hilt
  DI wiring throughout.
- **Presentation layer**: Dashboard, Daily Missions (with difficulty-tiered Mission
  creation), Habits (with Good/Bad habit template picker), Focus Lock settings, and the
  full-screen Focus Block popup — all with real ViewModels backed by Room/DataStore, not
  mock data.
- **Streak Engine**: daily streak tracking, Streak Saver auto-consumption at daily
  rollover, 8 PM reminder notification if today isn't qualified yet.
- **Habit System**: positive + negative habits (avoidance-framed, never punishing),
  6 good-habit and 6 bad-habit-to-break presets including digital wellbeing (no phone
  before bed, no social media doomscroll) and health (no sugar, no smoking, no porn,
  no junk food), each with its own Mastery level via `HabitMasteryCurve`.
- **Quest difficulty selection**: Easy/Medium/Hard/Boss picker when creating a custom
  Mission, with a live XP/Credit preview computed from `QuestDifficulty.xpMultiplier` and
  clamped to the FR-QST-04 anti-inflation caps.
- **Focus Lock**: opt-in Accessibility-Service-based distraction blocker. Watches only
  foreground-app-change events (`canRetrieveWindowContent=false` — structurally can't
  read screen content, not just policy-promised), shows a full-screen reminder (never a
  hard lock — always has a "go home" escape) when a user-chosen app is opened with
  Missions still incomplete. Master switch + per-app picker in-app; actual system
  permission grant happens in Android Settings (Android requires this, no in-app API
  exists for it) — the in-app screen links straight there.
- **Design system**: `AscendColors`/`AscendTypography`/`AscendTheme` implementing the
  locked brand tokens (AMOLED dark, Crimson/Gold, Inter/JetBrains Mono placeholders).
- **Tests**: `CalculateXpUseCaseTest`, `XpCurveTest`, `HabitMasteryCurveTest`,
  `StreakRepositoryImplTest` (streak saver consumption, streak-break, same-day
  idempotency, via fake DAOs — no Room needed to run these).

## What's NOT implemented yet (by design — see PRD §4 for the full V1 scope)

Study System, Fitness System, Vault/Shop, Statistics, Health Connect integration,
Milestones/Achievements UI, Rewards/chest-opening, Notifications center (in-app feed —
push notifications already work), Settings, Onboarding/Login/Splash screens, Energy
system (Dashboard shows a static 110/110), same-session combo tracking (the XP engine
supports it, nothing calls it with a real value yet), level-up celebration animation,
Backup & Restore. Each follows the exact same recipe already established here (domain
model → repository interface → Room entity/DAO or DataStore → repository impl → Hilt
binding → ViewModel → Compose screen) — the foundation exists specifically so those are
fast, consistent additions, not redesigns.

## Asset drop-in system

Real assets already wired: `avatar_warden.jpg`, `avatar_recon.jpg`, `avatar_drift.jpg`,
`avatar_ember.jpg`, `ic_notification_bell.png`, `ic_credits.xml`, `ic_essence_stone.xml`,
`ic_energy.xml` (all in `app/src/main/res/drawable` or `drawable-nodpi`).

Empty placeholders waiting for real art — **replace the file at the same path/name and no
code changes are needed**:
- `ic_avatar_placeholder.xml`
- `ic_streak_flame.xml` (real source exists as `Fire.svg`/`Fire.json` — fastest path is
  Android Studio's Vector Asset importer, File ▸ New ▸ Vector Asset ▸ Local file (SVG))
- `ic_chest_reward.xml`
- `ic_mission_study.xml`, `ic_mission_workout.xml`, `ic_mission_habit.xml`
- `ic_avatar_frame_equip_slot.xml`

Mipmap launcher icons (`ic_launcher`, `ic_launcher_round`) also still need generating from
`assets/appicon/ic_launcher_foreground.svg` + `ic_launcher_background.svg` via Android
Studio's Image Asset tool (Right-click `res` ▸ New ▸ Image Asset ▸ Adaptive and Legacy).

## Known gaps / honesty check

- Gradle dependency versions are best-effort, not network-verified (see "How to open" above).
- Inter and JetBrains Mono fonts are NOT bundled — `Type.kt` currently falls back to system
  fonts. Drop `.ttf` files into `res/font/` and wire them in `Type.kt` (both are free/open
  license, no blocker, just missing files).
- Focus Lock's block decision only checks Missions, not Habits — deliberately simple for
  V1 (see `ShouldBlockDistractingAppUseCase` kdoc). Also: "Go to Missions" from the block
  screen relaunches MainActivity fresh rather than deep-linking straight to the Quests
  screen — functional, just not the smoothest possible handoff.
- No CI, no instrumented tests yet.
- This has not been compiled/run — no Android SDK or network in this environment to verify.
  Treat as "should compile" based on careful API usage and a manual resource/import audit,
  not "verified compiling."

---

## Creator

**Kushagra Singh Bisht** — Android Developer & Computer Science Student
GitHub: [@bkushagra742](https://github.com/bkushagra742) · Portfolio: [bkushagra742.vercel.app](https://bkushagra742.vercel.app)

**License:** All Rights Reserved...
