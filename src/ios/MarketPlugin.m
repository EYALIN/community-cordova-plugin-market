#import "MarketPlugin.h"
#import <StoreKit/StoreKit.h>

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
