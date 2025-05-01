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
}
