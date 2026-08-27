package com.dylanvann.fastimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.react.bridge.ReadableMap;
import com.dylanvann.fastimage.events.FastImageErrorEvent;
import com.dylanvann.fastimage.events.FastImageLoadStartEvent;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

class FastImageViewWithUrl extends AppCompatImageView {
    private static final String TAG = "FastImageViewWithUrl";
    private boolean mNeedsReload = false;
    private ReadableMap mSource = null;
    private Drawable mDefaultSource = null;
    private int mBlurRadius = 0;
    private int mBlurRadiusPrevious = 0;
    public GlideUrl glideUrl;
    @Nullable
    final RequestManager requestManager;
    private String mTransition = "none"; // "none" | "fade"

    public FastImageViewWithUrl(Context context, @Nullable RequestManager requestManager) {
        super(context);
        this.requestManager = requestManager;
    }

    public void setSource(@Nullable ReadableMap source) {
        mNeedsReload = true;
        mSource = source;
    }

    public void setDefaultSource(@Nullable Drawable source) {
        mNeedsReload = true;
        mDefaultSource = source;
    }

    public void setResizeMode(ScaleType scaleType) {
        if (getScaleType() != scaleType) {
            setScaleType(scaleType);
            mNeedsReload = true;
        }
    }

    public void setBlurRadius(@Nullable Integer blurRadius) {
        mNeedsReload = true;
        mBlurRadiusPrevious = mBlurRadius;
        mBlurRadius = blurRadius == null ? 0 : blurRadius;
    }

    public void setTransition(@Nullable String transition) {
        mNeedsReload = true;
        if (transition == null) {
            mTransition = "none";
        } else {
            mTransition = transition;
        }
    }

    private boolean isNullOrEmpty(final String url) {
        return url == null || url.trim().isEmpty();
    }

    @SuppressLint("CheckResult")
    public void onAfterUpdate(
            @NonNull FastImageViewManager manager,
            @Nullable RequestManager requestManager,
            @NonNull Map<String, List<FastImageViewWithUrl>> viewsForUrlsMap) {
        if (!mNeedsReload)
            return;
        mNeedsReload = false;
        clearView(requestManager);
        clearProgressListener(viewsForUrlsMap);

        if ((mSource == null ||
                !mSource.hasKey("uri") ||
                isNullOrEmpty(mSource.getString("uri"))) &&
                mDefaultSource == null) {

            // Clear the image.
            setImageDrawable(null);

            ThemedReactContext context = (ThemedReactContext) getContext();
            EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, getId());
            int surfaceId = UIManagerHelper.getSurfaceId(this);
            FastImageErrorEvent event = new FastImageErrorEvent(surfaceId, getId(), "Invalid source: missing URI");
            if (dispatcher != null) {
                dispatcher.dispatchEvent(event);
            }
            return;
        }

        //final GlideUrl glideUrl = FastImageViewConverter.getGlideUrl(view.getContext(), mSource);
        final FastImageSource imageSource = FastImageViewConverter.getImageSource(getContext(), mSource);

        if (imageSource != null && imageSource.getUri().toString().length() == 0) {
            ThemedReactContext context = (ThemedReactContext) getContext();
            EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, getId());
            int surfaceId = UIManagerHelper.getSurfaceId(this);
            FastImageErrorEvent event = new FastImageErrorEvent(surfaceId, getId(), "Invalid source: resource not found");

            if (dispatcher != null) {
                dispatcher.dispatchEvent(event);
            }
            // Clear the image.
            setImageDrawable(null);
            return;
        }

        // `imageSource` may be null and we still continue, if `defaultSource` is not null
        final GlideUrl glideUrl = imageSource == null ? null : imageSource.getGlideUrl();

        this.glideUrl = glideUrl;

        String key = glideUrl == null ? null : glideUrl.toStringUrl();

        if (glideUrl != null) {
            FastImageOkHttpProgressGlideModule.expect(key, manager);
            List<FastImageViewWithUrl> viewsForKey = viewsForUrlsMap.get(key);
            if (viewsForKey != null && !viewsForKey.contains(this)) {
                viewsForKey.add(this);
            } else if (viewsForKey == null) {
                List<FastImageViewWithUrl> newViewsForKeys = new ArrayList<>(Collections.singletonList(this));
                viewsForUrlsMap.put(key, newViewsForKeys);
            }
        }

        ThemedReactContext context = (ThemedReactContext) getContext();
        if (imageSource != null) {
            // This is an orphan event without a load/loadend when only loading a placeholder
            EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, getId());
            int surfaceId = UIManagerHelper.getSurfaceId(this);
            FastImageLoadStartEvent event = new FastImageLoadStartEvent(surfaceId, getId());

            if (dispatcher != null) {
                dispatcher.dispatchEvent(event);
            }
        }

        if (requestManager != null) {
            RequestBuilder<? extends Drawable> builder;
            Map<String, Object> builderOptions = new HashMap<>();
            builderOptions.put("view", this);
            builderOptions.put("blurRadius", mBlurRadius);
            builderOptions.put("blurRadiusShouldClean", mBlurRadiusPrevious > 0 && mBlurRadius <= 0);

            try {
                builder = requestManager
                        .load(imageSource == null ? null : imageSource.getSourceForLoad())
                        .apply(FastImageViewConverter
                                .getOptions(context, imageSource, mSource, builderOptions)
                                .placeholder(mDefaultSource) // show until loaded
                                .fallback(mDefaultSource)); // null will not be treated as error

                if (key != null) {
                    builder.listener(new FastImageRequestListener<>(key));
                }

                if ("fade".equals(mTransition)) {
                    builder = builder.transition(DrawableTransitionOptions.withCrossFade());
                }

                builder.into(this);
            } catch (Exception e) {
                Log.e(TAG, String.format("Error detecting image type for URI: %s. Exception: %s",
                imageSource != null ? imageSource.getUri().toString() : "null", e.getMessage()), e);
            }
        }
    }

    public void clearProgressListener(@NonNull Map<String, List<FastImageViewWithUrl>> viewsForUrlsMap) {
        if (glideUrl == null) return;
        String key = glideUrl.toStringUrl();
        List<FastImageViewWithUrl> views = viewsForUrlsMap.get(key);
        if (views != null) views.remove(this);
        if (views == null || views.isEmpty()) {
            viewsForUrlsMap.remove(key);
            FastImageOkHttpProgressGlideModule.forget(key);
        }
        glideUrl = null;
    }

    public void clearView(@Nullable RequestManager requestManager) {
        if (requestManager != null && getTag() != null && getTag() instanceof Request) {
            requestManager.clear(this);
        }
    }
}
