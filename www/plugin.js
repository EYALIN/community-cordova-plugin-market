var PLUGIN_NAME = 'MarketPlugin';

var MarketPlugin = {
    /**
     * What this build of the plugin can do. `safeInAppReview` (1.0.5+): on Android requestReview()
     * launches the Play review sheet only on a resumed activity, never with a null package, and falls
     * back to the store listing if the Play library throws IllegalStateException.
     */
    capabilities: { safeInAppReview: true },

    open: function(appId) {
        return new Promise(function(resolve, reject) {
            cordova.exec(resolve, reject, PLUGIN_NAME, 'open', [appId]);
        });
    },

    search: function(query) {
        return new Promise(function(resolve, reject) {
            cordova.exec(resolve, reject, PLUGIN_NAME, 'search', [query]);
        });
    },

    requestReview: function() {
        return new Promise(function(resolve, reject) {
            cordova.exec(resolve, reject, PLUGIN_NAME, 'requestReview', []);
        });
    }
};

module.exports = MarketPlugin;
