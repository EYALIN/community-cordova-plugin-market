export interface MarketCapabilities {
    /**
     * 1.0.5+: Android requestReview() launches only on a resumed, live activity, never with a null
     * package, and falls back to the store listing on IllegalStateException.
     */
    safeInAppReview?: boolean;
}

/**
 * What requestReview() resolves with on Android (iOS resolves with nothing):
 * - 'launched': Play's flow completed (Play decides whether the sheet was actually shown),
 * - 'store_fallback': the Play library threw IllegalStateException, so the store listing was opened.
 * It rejects with 'activity_not_resumed', 'no_package', 'request_failed: …' or 'launch_failed: …'.
 */
export type ReviewResult = 'launched' | 'store_fallback' | void;

export default class MarketManager {
    /** Undefined on plugins older than 1.0.5. */
    capabilities?: MarketCapabilities;

    /**
     * Opens the app's page on the App Store (iOS) or Play Store (Android).
     * @param appId - Android: the package name. iOS: the numeric App Store id with its `id` prefix
     *   (e.g. `'id123456789'`, optionally `'id123456789?action=write-review'`).
     * @returns A Promise that resolves when the store was opened; rejects "Invalid app ID" for an
     *   empty / null id. Android falls back to the https://play.google.com page without a Play Store.
     */
    open(appId: string): Promise<void>;

    /**
     * Searches the Play Store (Android) or App Store (iOS) using the given query string.
     * @param query - The search query.
     * @returns A Promise that resolves when the operation is complete.
     */
    search?(query: string): Promise<void>;

    /**
     * Requests an in-app review dialog.
     * On iOS: Uses SKStoreReviewController (native App Store rating dialog).
     * On Android: Uses Google Play In-App Review API.
     * Note: The OS may choose not to show the dialog (e.g., if shown too recently).
     * Android (1.0.5+): launched only while the activity is resumed and not finishing.
     * @returns A Promise that resolves when the request is complete (see ReviewResult).
     */
    requestReview(): Promise<ReviewResult>;
}
