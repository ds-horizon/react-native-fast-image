package com.dylanvann.fastimage;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.views.imagehelper.ImageSource;

class FastImageViewModule extends ReactContextBaseJavaModule {

    FastImageViewModuleImplementation impl;

    FastImageViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        impl = new FastImageViewModuleImplementation(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return impl.REACT_CLASS;
    }

    @ReactMethod
    public void preload(final ReadableArray sources) {
        impl.preload(sources);
    }

    @ReactMethod
    public void clearMemoryCache(final Promise promise) {
        impl.clearMemoryCache(promise);
    }

    @ReactMethod
    public void clearDiskCache(Promise promise) {
        impl.clearDiskCache(promise);
    }

    @ReactMethod
    public void getOriginalSize(final ReadableMap source, final ReadableMap options, final Promise promise) {
        impl.getOriginalSize(source, options, promise);
    }
}
