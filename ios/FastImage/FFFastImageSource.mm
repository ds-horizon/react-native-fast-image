#import "FFFastImageSource.h"
#import <SDWebImage/SDWebImageDownloaderRequestModifier.h>

@implementation FFFastImageSource

- (instancetype)initWithURL:(NSURL *)url
                   priority:(FFFPriority)priority
                    headers:(NSDictionary *)headers
               cacheControl:(FFFCacheControl)cacheControl
{
    self = [super init];
    if (self) {
        _url = url;
        _priority = priority;
        _headers = headers;
        _cacheControl = cacheControl;
    }
    return self;
}

- (SDWebImageContext *)contextWithRequestHeaders:(SDWebImageContext *)context {
    NSDictionary *headers = [self.headers copy];
    if (headers.count == 0) return context;
    id<SDWebImageDownloaderRequestModifier> previousModifier = context[SDWebImageContextDownloadRequestModifier];
    SDWebImageDownloaderRequestModifier *modifier = [SDWebImageDownloaderRequestModifier requestModifierWithBlock:^NSURLRequest *(NSURLRequest *request) {
        NSURLRequest *modifiedRequest = previousModifier ? [previousModifier modifiedRequestWithRequest:request] : request;
        if (!modifiedRequest) return nil;
        NSMutableURLRequest *result = [modifiedRequest mutableCopy];
        for (NSString *header in headers) {
            [result setValue:headers[header] forHTTPHeaderField:header];
        }
        return [result copy];
    }];
    NSMutableDictionary *result = [context mutableCopy] ?: [NSMutableDictionary new];
    result[SDWebImageContextDownloadRequestModifier] = modifier;
    return result;
}

@end
