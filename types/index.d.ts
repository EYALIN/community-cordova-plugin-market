export default class MarketManager {
    /**
     * Opens the app's page on the App Store (iOS) or Play Store (Android).
     * @param appId - The app ID to open (iOS bundle ID or Android package name).
     * @returns A Promise that resolves when the operation is complete.
     */
    open(appId: string): Promise<void>;

    /**
     * Searches for an app using the given query string.
     * Only available on Android.
     * @param query - The search query.
     * @returns A Promise that resolves when the operation is complete.
     */
    search?(query: string): Promise<void>;

    /**
     * Requests an in-app review dialog.
     * On iOS: Uses SKStoreReviewController (native App Store rating dialog).
     * On Android: Uses Google Play In-App Review API.
     * Note: The OS may choose not to show the dialog (e.g., if shown too recently).
     * @returns A Promise that resolves when the request is complete.
     */
    requestReview(): Promise<void>;
}
