package com.dylanvann.fastimage;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UiThreadUtil;

class FastImageViewModuleImplementation {
    private final ReactApplicationContext reactContext;
    FastImageViewModuleImplementation(ReactApplicationContext reactContext){

    this.reactContext = reactContext;
    }

    public static final String REACT_CLASS = "FastImageViewModule";

    public void preload(final ReadableArray sources) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestManager manager = Glide.with(reactContext);
                for (int i = 0; i < sources.size(); i++) {
                    final ReadableMap source = sources.getMap(i);
                    final FastImageSource imageSource = FastImageViewConverter.getImageSource(reactContext, source);
                    if (imageSource == null || imageSource.getUri().toString().isEmpty()) {
                        continue;
                    }
                    manager
                            // This will make this work for remote and local images. e.g.
                            //    - file:///
                            //    - content://
                            //    - res:/
                            //    - android.resource://
                            //    - data:image/png;base64
                            .load(imageSource.getSourceForLoad())
                            .apply(FastImageViewConverter.getOptions(reactContext, imageSource, source, null))
                            .preload();
                }
            }
        });
    }

    public void clearMemoryCache(final Promise promise) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Glide.get(reactContext).clearMemory();
                    promise.resolve(null);
                } catch (Exception error) {
                    promise.reject("E_CLEAR_MEMORY_CACHE", error);
                }
            }
        });
    }
    public void clearDiskCache(Promise promise) {
        try {
            Glide.get(reactContext).clearDiskCache();
            promise.resolve(null);
        } catch (Exception error) {
            promise.reject("E_CLEAR_DISK_CACHE", error);
        }
    }
}
