## Release Notes – `community-cordova-plugin-market` v1.0.5

**Release Date:** September 26, 2026

### Fixed (Android)

- `requestReview()` is crash-safe: the Play review sheet is launched on the UI thread only while the
  activity is resumed and not finishing/destroyed; otherwise the promise rejects with
  `activity_not_resumed` and nothing is launched.
- An `IllegalStateException` from the Play review library (at launch, or as the flow's failure) no
  longer escapes: the plugin opens this app's own store listing instead and resolves `store_fallback`.
- The package name is never null: `requestReview()` rejects with `no_package`, and `open(null)` now
  rejects "Invalid app ID" instead of opening `market://details?id=null`.
- Every path answers the JS callback exactly once; an exception in `execute()` no longer produces a
  second "invalid action" error.
- `open()` / `search()` no longer reject "Play Store not found on device" on Android 11+ just because
  package visibility hides the Play Store from `resolveActivity()`; when the Play Store really is
  missing they open the `https://play.google.com` page instead.

### Fixed (iOS)

- `open(null)` / `search(null)` reject ("Invalid app ID" / "Invalid search query") instead of
  crashing on `-[NSNull length]`.
- `search()` percent-encodes `&`, `=`, `+`, `#` and `?` so the whole query reaches the App Store.
- `open()` / `search()` always use `openURL:options:completionHandler:` (the deprecated `openURL:`
  fallback was dead code on iOS 10+ and does nothing on iOS 18+).

### Added

- `MarketPlugin.capabilities.safeInAppReview === true`, so apps can tell a crash-safe build apart.
- Android `requestReview()` resolves with `'launched'` or `'store_fallback'`.

### Dependencies

- `com.google.android.play:review` stays at **2.0.2** — the newest release on Maven Google.
- `com.google.android.gms:play-services-tasks` stays at **18.4.1** (newest).

### Packaging

- package.json now has `repository`, `keywords` (`ecosystem:cordova`), `license` and `files`;
  plugin.xml has a real name/description and requires `cordova-android >= 10`.

---

## Release Notes – `community-cordova-plugin-market` v1.0.4

**Release Date:** July 5, 2026

- `com.google.android.play:review` 2.0.2 and `com.google.android.gms:play-services-tasks` 18.4.1.

---

## Release Notes – `community-cordova-plugin-market` v1.0.3

**Release Date:** March 31, 2026

- Android: `Task` comes from `com.google.android.gms.tasks` (`play-services-tasks`), not the removed
  legacy Play Core library.

---

## Release Notes – `community-cordova-plugin-market` v1.0.2

**Release Date:** March 21, 2026

- New `requestReview()`: iOS `SKStoreReviewController`, Android Google Play In-App Review API.

---

## 📦 Release Notes – `community-cordova-plugin-market` v1.0.0

**Release Date:** May 1, 2025 (1.0.1 the same day: README only)

### ✨ New Features

- ✅ **Initial Release**
- 📱 **Android & iOS Support**
- 🛒 **`open(appId)`**  
  Opens the app's page directly in the App Store (iOS) or Play Store (Android) using its app ID.
- 🔍 **`search(query)`**  
  Performs a search in the App Store or Play Store with a given search phrase.

### 🧠 Plugin Details

- Written in **native Java (Android)** and **Objective-C (iOS)**.
- Promises-based JS interface (`MarketPlugin.open()` and `MarketPlugin.search()`).
- Compatible with Cordova 10+ and AndroidX.
- Plugin ID: `community-cordova-plugin-market`

### 🛠️ Installation

```bash
cordova plugin add community-cordova-plugin-market
```
