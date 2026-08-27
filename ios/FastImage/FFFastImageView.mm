#import "FFFastImageView.h"
#import "FFFastImageBlurTransformation.h"
#import <CoreImage/CoreImage.h>
#import <SDWebImage/UIImage+MultiFormat.h>
#import <SDWebImage/UIView+WebCache.h>
#import <SDWebImageAVIFCoder/SDImageAVIFCoder.h>
#import <SDWebImageWebPCoder/SDImageWebPCoder.h>
#if !defined(DISABLE_SVG) || DISABLE_SVG == 0
#import <SDWebImageSVGCoder/SDImageSVGCoder.h>
#endif

@interface FFFastImageView ()

@property(nonatomic, assign) BOOL hasSentOnLoadStart;
@property(nonatomic, assign) BOOL hasCompleted;
@property(nonatomic, assign) BOOL hasErrored;
// Whether the latest change of props requires the image to be reloaded
@property(nonatomic, assign) BOOL needsReload;
@property(nonatomic, assign) NSUInteger loadGeneration;
@property(nonatomic, strong) UIImage *originalImage;

@property(nonatomic, strong) NSDictionary* onLoadEvent;

@property(nonatomic, strong) NSDictionary *lastErrorEvent;

@end

@implementation FFFastImageView

static NSString * const kFFFastImageDefaultErrorMessage = @"Load failed";


- (void)onLoadEventSend:(UIImage *)image {
    NSDictionary* onLoadEvent = @{
            @"width": [NSNumber numberWithDouble: image.size.width],
            @"height": [NSNumber numberWithDouble: image.size.height]
    };
    self.onLoadEvent = onLoadEvent;
    #ifdef RCT_NEW_ARCH_ENABLED
        if (_eventEmitter != nullptr) {
            std::dynamic_pointer_cast<const facebook::react::FastImageViewEventEmitter>(_eventEmitter)
                ->onFastImageLoad(facebook::react::FastImageViewEventEmitter::OnFastImageLoad{.width = image.size.width, .height = image.size.height});
          }
    #else
    if (self.onFastImageLoad) {
        self.onFastImageLoad(onLoadEvent);
    }
#endif
}

- (void)onLoadStartEvent {
    #ifdef RCT_NEW_ARCH_ENABLED
        if (_eventEmitter != nullptr) {
            std::dynamic_pointer_cast<const facebook::react::FastImageViewEventEmitter>(_eventEmitter)
            ->onFastImageLoadStart(facebook::react::FastImageViewEventEmitter::OnFastImageLoadStart{});
        }
    #else
        if (self.onFastImageLoadStart) {
            self.onFastImageLoadStart(@{});
            self.hasSentOnLoadStart = YES;
        } else {
            self.hasSentOnLoadStart = NO;
        }
    #endif
}

- (void)onProgressEvent:(NSInteger)receivedSize expectedSize:(NSInteger)expectedSize {
    #ifdef RCT_NEW_ARCH_ENABLED
        if (_eventEmitter != nullptr) {
            std::dynamic_pointer_cast<const facebook::react::FastImageViewEventEmitter>(_eventEmitter)
            ->onFastImageProgress(facebook::react::FastImageViewEventEmitter::OnFastImageProgress{.loaded = static_cast<int>(receivedSize), .total = static_cast<int>(expectedSize)});
        }
    #else
        if (self.onFastImageProgress) {
            self.onFastImageProgress(@{
                @"loaded": @(receivedSize),
                @"total": @(expectedSize)
            });
        }
    #endif
}

- (void)onLoadEndEvent {
    #ifdef RCT_NEW_ARCH_ENABLED
        if (_eventEmitter != nullptr) {
            std::dynamic_pointer_cast<const facebook::react::FastImageViewEventEmitter>(_eventEmitter)
            ->onFastImageLoadEnd(facebook::react::FastImageViewEventEmitter::OnFastImageLoadEnd{});
        }
    #else
    if (self.onFastImageLoadEnd) {
        self.onFastImageLoadEnd(@{});
    }
#endif
}

- (void)onErrorEvent:(NSError *)error {

    NSString *msg = error.localizedDescription ?: kFFFastImageDefaultErrorMessage;
    NSDictionary *event = @{ @"error": msg };
    self.lastErrorEvent = event;

    #ifdef RCT_NEW_ARCH_ENABLED
        if (_eventEmitter != nullptr) {
            std::dynamic_pointer_cast<const facebook::react::FastImageViewEventEmitter>(_eventEmitter)
            ->onFastImageError(facebook::react::FastImageViewEventEmitter::OnFastImageError{.error = std::string([msg UTF8String])});
        }
    #else
        if (self.onFastImageError) {
            self.onFastImageError(@{
                    @"error": error.localizedDescription ?: kFFFastImageDefaultErrorMessage
                }
            );
        }
    #endif
}


- (void)commonInitUtils {
    self.resizeMode = RCTResizeModeCover;
    self.clipsToBounds = YES;
    [[SDImageCodersManager sharedManager] addCoder:[SDImageAVIFCoder sharedCoder]];
    [[SDImageCodersManager sharedManager] addCoder:[SDImageWebPCoder sharedCoder]];
#if !defined(DISABLE_SVG) || DISABLE_SVG == 0
    [[SDImageCodersManager sharedManager] addCoder:[SDImageSVGCoder sharedCoder]];
#endif
}

- (instancetype)initWithFrame:(CGRect)frame {
//     Called on new arch from FFFastImageComponentView
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInitUtils];
    }
    return self;
}

- (id) init {
//     Called on old arch from FFFastImageViewManager
    self = [super init];
    if (self) {
        [self commonInitUtils];
    }
    return self;
}

- (void) setResizeMode: (RCTResizeMode)resizeMode {
    if (_resizeMode != resizeMode) {
        _resizeMode = resizeMode;
        self.contentMode = (UIViewContentMode) resizeMode;
    }
}

- (void) setOnFastImageLoadEnd: (RCTDirectEventBlock)onFastImageLoadEnd {
    _onFastImageLoadEnd = onFastImageLoadEnd;
    if ((self.hasCompleted || self.hasErrored) && _onFastImageLoadEnd) {
        _onFastImageLoadEnd(@{});
    }
}

- (void) setOnFastImageLoad: (RCTDirectEventBlock)onFastImageLoad {
    _onFastImageLoad = onFastImageLoad;
    if (self.hasCompleted && _onFastImageLoad) {
        _onFastImageLoad(self.onLoadEvent);
    }
}

- (void) setOnFastImageError: (RCTDirectEventBlock)onFastImageError {
    _onFastImageError = onFastImageError;
    if (self.hasErrored && _onFastImageError) {
        _onFastImageError(self.lastErrorEvent ?: @{ @"error": kFFFastImageDefaultErrorMessage});
    }
}

- (void) setOnFastImageLoadStart: (RCTDirectEventBlock)onFastImageLoadStart {
    _onFastImageLoadStart = onFastImageLoadStart;
    if (_onFastImageLoadStart && _source.url && !self.needsReload &&
        !self.hasSentOnLoadStart && !self.hasCompleted && !self.hasErrored) {
        [self onLoadStartEvent];
    }
}

- (void) setImageColor: (UIColor*)imageColor {
    if (_imageColor != imageColor && ![_imageColor isEqual:imageColor]) {
        _imageColor = imageColor;
        [self setImage:self.originalImage];
    }
}

- (void)setBlurRadius:(CGFloat)blurRadius {
    if (_blurRadius != blurRadius) {
        _blurRadius = blurRadius;
        [self setImage:self.originalImage];
    }
}

- (UIImage*) makeImage: (UIImage*)image withTint: (UIColor*)color {
    if (image.size.width <= 0 || image.size.height <= 0) return image;
    UIImage* newImage = [image imageWithRenderingMode: UIImageRenderingModeAlwaysTemplate];
    UIGraphicsImageRenderer *renderer = [[UIGraphicsImageRenderer alloc] initWithSize:image.size];
    newImage = [renderer imageWithActions:^(UIGraphicsImageRendererContext * _Nonnull rendererContext) {
    [color setFill];
    [newImage drawInRect:CGRectMake(0, 0, image.size.width, newImage.size.height)];
    }];
    return newImage;
}

- (void) setImage: (UIImage*)image {
    self.originalImage = image;
    if (!image) {
        super.image = nil;
        return;
    }
    if (_blurRadius && _blurRadius > 0) {
        FFFastImageBlurTransformation *transformation =
            [[FFFastImageBlurTransformation alloc] initWithRadius:_blurRadius];
        image = [transformation transform:image];
    }

    if (self.imageColor != nil) {
        super.image = [self makeImage: image withTint: self.imageColor];
    } else {
        super.image = image;
    }
}

- (void) sendOnLoad: (UIImage*)image {
    [self onLoadEventSend:image];
}

- (void) setSource: (FFFastImageSource*)source {
    if (_source != source) {
        BOOL sameSource = _source && source &&
            (_source.url == source.url || [_source.url isEqual:source.url]) &&
            (_source.headers == source.headers || [_source.headers isEqual:source.headers]) &&
            _source.priority == source.priority && _source.cacheControl == source.cacheControl;
        if (sameSource) return;
        _source = source;
        _needsReload = YES;
    }
}

- (void) setDefaultSource: (UIImage*)defaultSource {
    if (_defaultSource != defaultSource) {
        _defaultSource = defaultSource;
        _needsReload = YES;
    }
}

- (void) didSetProps: (NSArray<NSString*>*)changedProps {
    if (_needsReload) {
        [self reloadImage];
    }
}

- (void) reloadImage {
    _needsReload = NO;
    self.loadGeneration += 1;
    [self sd_cancelCurrentImageLoad];
    self.hasSentOnLoadStart = NO;
    self.hasCompleted = NO;
    self.hasErrored = NO;
    self.onLoadEvent = nil;
    self.lastErrorEvent = nil;

    if (_source.url) {
        // Load base64 images.
        NSString* url = [_source.url absoluteString];
        if (url && [url hasPrefix: @"data:image"]) {
            [self onLoadStartEvent];
            // Use SDWebImage API to support external format like WebP images
            UIImage* image = [UIImage sd_imageWithData: [NSData dataWithContentsOfURL: _source.url]];
            [self setImage: image];
            if (image) {
                [self onProgressEvent:1 expectedSize:1];
                self.hasCompleted = YES;
                [self sendOnLoad: image];
            } else {
                self.hasErrored = YES;
                [self onErrorEvent:[NSError errorWithDomain:@"FastImage" code:1 userInfo:@{NSLocalizedDescriptionKey: @"Failed to decode image data"}]];
            }
            [self onLoadEndEvent];
            return;
        }

        // Set headers.
        NSDictionary* headers = _source.headers;
        SDWebImageDownloaderRequestModifier* requestModifier = [SDWebImageDownloaderRequestModifier requestModifierWithBlock: ^NSURLRequest* _Nullable (NSURLRequest* _Nonnull request) {
            NSMutableURLRequest* mutableRequest = [request mutableCopy];
            for (NSString* header in headers) {
                NSString* value = headers[header];
                [mutableRequest setValue: value forHTTPHeaderField: header];
            }
            return [mutableRequest copy];
        }];
        SDWebImageContext* context = @{SDWebImageContextDownloadRequestModifier: requestModifier};

        // Set priority.
        SDWebImageOptions options = SDWebImageRetryFailed | SDWebImageHandleCookies;
        switch (_source.priority) {
            case FFFPriorityLow:
                options |= SDWebImageLowPriority;
                break;
            case FFFPriorityNormal:
                // Priority is normal by default.
                break;
            case FFFPriorityHigh:
                options |= SDWebImageHighPriority;
                break;
        }

        switch (_source.cacheControl) {
            case FFFCacheControlWeb:
                options |= SDWebImageRefreshCached;
                break;
            case FFFCacheControlCacheOnly:
                options |= SDWebImageFromCacheOnly;
                break;
            case FFFCacheControlImmutable:
                break;
        }
        [self onLoadStartEvent];
        [self downloadImage: _source options: options context: context];
    } else {
        [self setImage: _defaultSource];
    }
}

- (void) downloadImage: (FFFastImageSource*)source options: (SDWebImageOptions)options context: (SDWebImageContext*)context {
    __weak FFFastImageView *weakSelf = self; // Always use a weak reference to self in blocks
    NSUInteger generation = self.loadGeneration;
    // transition: default to none; enable fade if requested
    self.sd_imageTransition = [self.transition isEqualToString:@"fade"] ? SDWebImageTransition.fadeTransition : nil;
    [self sd_setImageWithURL: source.url
            placeholderImage: _defaultSource
                     options: options
                     context: context
                    progress: ^(NSInteger receivedSize, NSInteger expectedSize, NSURL* _Nullable targetURL) {
        dispatch_block_t reportProgress = ^{
            FFFastImageView *view = weakSelf;
            if (view && view.loadGeneration == generation && !view.hasCompleted && !view.hasErrored) {
                [view onProgressEvent:receivedSize expectedSize:expectedSize];
            }
        };
        if ([NSThread isMainThread]) {
            reportProgress();
        } else {
            dispatch_async(dispatch_get_main_queue(), reportProgress);
        }
                    } completed: ^(UIImage* _Nullable image,
                    NSError* _Nullable error,
                    SDImageCacheType cacheType,
                    NSURL* _Nullable imageURL) {
                FFFastImageView *view = weakSelf;
                if (!view || view.loadGeneration != generation) return;
                if (error) {
                    view.hasErrored = YES;
                    [view onErrorEvent:error];

                    [view onLoadEndEvent];
                } else {
                    view.hasCompleted = YES;
                    [view sendOnLoad: image];
                    [view onLoadEndEvent];
                }
            }];
}

- (void) dealloc {
    [self sd_cancelCurrentImageLoad];
}

@end
