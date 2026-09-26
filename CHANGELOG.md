# Changelog

All notable changes to `community-cordova-plugin-market`. Versions and dates match the npm releases.

## [1.0.5] - 2026-09-26

### Fixed
- Android `requestReview()`: launch only on a resumed, non-finishing activity; IllegalStateException
  falls back to the app's store listing; never a null package; one callback per call.
- `open(null)` rejects instead of opening `market://details?id=null` (Android) or crashing on
  `NSNull` (iOS); `search(null)` likewise rejects on iOS instead of crashing.
- Android `open()` / `search()` no longer report "Play Store not found" on Android 11+ because of
  package visibility (`resolveActivity()` returned null without a `<queries>` entry); when the Play
  Store really is missing they fall back to the `https://play.google.com` page.
- iOS `search()` percent-encodes `&`, `=`, `+`, `#` and `?` in the query.

### Added
- `capabilities.safeInAppReview` flag; Android `requestReview()` resolves `'launched' | 'store_fallback'`.
- package.json `repository`, `keywords` (`ecosystem:cordova`), `license`, `files`; plugin.xml name,
  description and a `cordova-android >= 10` engine.

## [1.0.4] - 2026-07-05

### Changed
- `com.google.android.play:review` 2.0.1 → 2.0.2, `com.google.android.gms:play-services-tasks`
  18.2.0 → 18.4.1.

## [1.0.3] - 2026-03-31

### Fixed
- Android build: import `Task` from `com.google.android.gms.tasks` (declared
  `play-services-tasks` 18.2.0) instead of the removed legacy Play Core `com.google.android.play.core.tasks`.

## [1.0.2] - 2026-03-21

### Added
- `requestReview()`: iOS `SKStoreReviewController`, Android Google Play In-App Review
  (`com.google.android.play:review` 2.0.1).

## [1.0.1] - 2025-05-01

### Changed
- README cleanup only.

## [1.0.0] - 2025-05-01

### Added
- Initial release of `community-cordova-plugin-market`.
- `open(appId)` to open an app's page in the App Store / Play Store.
- `search(query)` to search in the App Store / Play Store.
- Android and iOS support.
