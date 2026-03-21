

[![NPM version](https://img.shields.io/npm/v/community-cordova-plugin-market)](https://www.npmjs.com/package/community-cordova-plugin-market)

# community-cordova-plugin-market

I dedicate a considerable amount of my free time to developing and maintaining many Cordova plugins for the community ([See the list with all my maintained plugins][community_plugins]).  
To help ensure this plugin is kept updated, new features are added, and bugfixes are implemented quickly,  
please donate a couple of dollars (or a little more if you can stretch) as this will help me to afford to dedicate time to its maintenance.  
Please consider donating if you're using this plugin in an app that makes you money, or if you're asking for new features or priority bug fixes. Thank you!

[![](https://img.shields.io/static/v1?label=Sponsor%20Me&style=for-the-badge&message=%E2%9D%A4&logo=GitHub&color=%23fe8e86)](https://github.com/sponsors/eyalin)

---

## community-cordova-plugin-market

`community-cordova-plugin-market` is a Cordova plugin that allows you to open app pages or perform search queries in the Google Play Store (Android) and App Store (iOS).

## Features

- Open an app’s store page by its ID.
- Search for apps in the Play Store or App Store using a keyword.
- Request an in-app review dialog (iOS: SKStoreReviewController, Android: Google Play In-App Review API).

## Installation

```bash
cordova plugin add community-cordova-plugin-market
```

## Supported Platforms

- Android
- iOS

## Usage

To use the plugin, call the `open`, `search`, or `requestReview` methods. All return promises.

### Example

```javascript
document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
    MarketPlugin.open('com.example.app')
        .then(() => console.log('Opened market page'))
        .catch(error => console.error('Error:', error));

    MarketPlugin.search('productivity apps')
        .then(() => console.log('Opened market search'))
        .catch(error => console.error('Error:', error));

    MarketPlugin.requestReview()
        .then(() => console.log('Review dialog requested'))
        .catch(error => console.error('Error:', error));
}
```

## API

### Methods

#### `open(appId: string): Promise<void>`

Opens the market (App Store or Play Store) to the app page using the provided application ID.

#### `search(query: string): Promise<void>`

Performs a search in the App Store or Play Store using the provided query string.

#### `requestReview(): Promise<void>`

Requests an in-app review dialog. On iOS, this uses `SKStoreReviewController` to show the native App Store rating dialog. On Android, this uses the Google Play In-App Review API.

**Note:** The OS may choose not to show the dialog (e.g., if it has been shown too recently). This is controlled by Apple/Google and cannot be overridden.

---

## License

This project is licensed under the MIT License.

---

[community_plugins]: https://github.com/EYALIN?tab=repositories&q=community&type=&language=&sort=
