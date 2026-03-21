#import <Cordova/CDVPlugin.h>

@interface MarketPlugin : CDVPlugin
- (void)open:(CDVInvokedUrlCommand*)command;
- (void)search:(CDVInvokedUrlCommand*)command;
- (void)requestReview:(CDVInvokedUrlCommand*)command;
@end
