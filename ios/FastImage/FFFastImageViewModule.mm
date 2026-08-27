#import "FFFastImageViewModule.h"
#import "FFFastImageSource.h"

#import <SDWebImage/SDImageCache.h>
#import <SDWebImage/SDWebImagePrefetcher.h>
#import <SDWebImage/SDWebImageDownloader.h>

@implementation FFFastImageViewModule

RCT_EXPORT_MODULE(FastImageViewModule)

RCT_EXPORT_METHOD(preload:(nonnull NSArray<FFFastImageSource *> *)sources)
{
    SDWebImagePrefetcher *prefetcher = [SDWebImagePrefetcher sharedImagePrefetcher];
    for (FFFastImageSource *source in sources) {
        if (!source.url) continue;
        [prefetcher prefetchURLs:@[source.url]
                       options:prefetcher.options
                       context:[source contextWithRequestHeaders:prefetcher.context]
                      progress:nil
                     completed:nil];
    }
}

RCT_EXPORT_METHOD(clearMemoryCache:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    [SDImageCache.sharedImageCache clearMemory];
    resolve(NULL);
}

RCT_EXPORT_METHOD(clearDiskCache:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    [SDImageCache.sharedImageCache clearDiskOnCompletion:^(){
        resolve(NULL);
    }];
}
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeFastImageViewModuleSpecJSI>(params);
}
#endif

@end
