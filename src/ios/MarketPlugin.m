#import "MarketPlugin.h"
#import <StoreKit/StoreKit.h>

@implementation MarketPlugin

- (void)pluginInitialize {
    // Optional: initialization logic
}

/** The first argument when it is a string; nil for a missing argument or a JS null (NSNull). */
- (NSString *)stringArgument:(CDVInvokedUrlCommand *)command {
    id value = [command.arguments firstObject];
    return [value isKindOfClass:[NSString class]] ? (NSString *)value : nil;
}

- (void)open:(CDVInvokedUrlCommand *)command {
    // A JS null/undefined arrives as NSNull (or nothing): never send it -length.
    NSString *appId = [self stringArgument:command];
    NSString *trimmed = [appId stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    CDVPluginResult *pluginResult;
    NSURL *appURL = nil;
    if (trimmed.length > 0 && ![trimmed isEqualToString:@"null"]) {
        appURL = [NSURL URLWithString:[NSString stringWithFormat:@"itms-apps://itunes.apple.com/app/%@", appId]];
    }

    if (appURL) {
        // Cordova iOS targets iOS 11+, where openURL:options:completionHandler: always exists
        // (the deprecated openURL: does nothing on iOS 18+).
        [[UIApplication sharedApplication] openURL:appURL options:@{} completionHandler:nil];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid app ID"];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)search:(CDVInvokedUrlCommand *)command {
    NSString *query = [self stringArgument:command];
    CDVPluginResult *pluginResult;
    NSURL *appURL = nil;
    if ([query stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]].length > 0) {
        // URLQueryAllowedCharacterSet keeps '&', '=', '+' and '#' literal, which would cut the term short.
        NSMutableCharacterSet *allowed = [[NSCharacterSet URLQueryAllowedCharacterSet] mutableCopy];
        [allowed removeCharactersInString:@"&=+#?"];
        NSString *encodedQuery = [query stringByAddingPercentEncodingWithAllowedCharacters:allowed];
        appURL = [NSURL URLWithString:[NSString stringWithFormat:@"itms-apps://itunes.apple.com/search?term=%@", encodedQuery]];
    }

    if (appURL) {
        // Cordova iOS targets iOS 11+, where openURL:options:completionHandler: always exists
        // (the deprecated openURL: does nothing on iOS 18+).
        [[UIApplication sharedApplication] openURL:appURL options:@{} completionHandler:nil];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid search query"];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)requestReview:(CDVInvokedUrlCommand *)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (@available(iOS 14.0, *)) {
            UIWindowScene *scene = nil;
            for (UIScene *s in [UIApplication sharedApplication].connectedScenes) {
                if (s.activationState == UISceneActivationStateForegroundActive && [s isKindOfClass:[UIWindowScene class]]) {
                    scene = (UIWindowScene *)s;
                    break;
                }
            }
            if (scene) {
                [SKStoreReviewController requestReviewInScene:scene];
            } else {
                [SKStoreReviewController requestReview];
            }
        } else {
            [SKStoreReviewController requestReview];
        }

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    });
}

@end
