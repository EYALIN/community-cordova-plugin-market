#import <Cordova/CDVPlugin.h>

@interface MarketPlugin : CDVPlugin
- (void)open:(CDVInvokedUrlCommand*)command;
- (void)search:(CDVInvokedUrlCommand*)command;
@end
