var PLUGIN_NAME = 'MarketPlugin';

var MarketPlugin = {
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
