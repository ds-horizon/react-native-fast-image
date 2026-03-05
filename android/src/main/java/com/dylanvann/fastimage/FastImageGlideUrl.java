package com.dylanvann.fastimage;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

class FastImageGlideUrl extends GlideUrl {
    private final String mCacheKey;

    FastImageGlideUrl(String url, Headers headers, String cacheKey) {
        super(url, headers);
        mCacheKey = cacheKey;
    }

    @Override
    public String getCacheKey() {
        return mCacheKey;
    }
}
