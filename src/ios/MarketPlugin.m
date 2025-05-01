#import "MarketPlugin.h"

@implementation MarketPlugin

- (void)pluginInitialize {
    // Optional: initialization logic
}

- (void)open:(CDVInvokedUrlCommand *)command {
    NSString *appId = [command.arguments firstObject];
    CDVPluginResult *pluginResult;

    if (appId && [appId length] > 0) {
        NSString *url = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/app/%@", appId];
        NSURL *appURL = [NSURL URLWithString:url];
        UIApplication *application = [UIApplication sharedApplication];

        if ([application respondsToSelector:@selector(openURL:options:completionHandler:)]) {
            [application openURL:appURL options:@{} completionHandler:nil];
        } else {
            [application openURL:appURL];
        }

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid app ID"];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)search:(CDVInvokedUrlCommand *)command {
    NSString *query = [command.arguments firstObject];
    CDVPluginResult *pluginResult;

    if (query && [query length] > 0) {
        NSString *encodedQuery = [query stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet];
        NSString *url = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/search?term=%@", encodedQuery];
        NSURL *appURL = [NSURL URLWithString:url];
        UIApplication *application = [UIApplication sharedApplication];

        if ([application respondsToSelector:@selector(openURL:options:completionHandler:)]) {
            [application openURL:appURL options:@{} completionHandler:nil];
        } else {
            [application openURL:appURL];
        }

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid search query"];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
