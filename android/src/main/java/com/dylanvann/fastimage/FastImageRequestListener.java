package com.dylanvann.fastimage;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.dylanvann.fastimage.events.FastImageErrorEvent;
import com.dylanvann.fastimage.events.FastImageLoadEndEvent;
import com.dylanvann.fastimage.events.FastImageLoadEvent;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;

public class FastImageRequestListener<T extends Drawable> implements RequestListener<T> {
    static final String REACT_ON_ERROR_EVENT = "onFastImageError";
    static final String REACT_ON_LOAD_EVENT = "onFastImageLoad";
    static final String REACT_ON_LOAD_END_EVENT = "onFastImageLoadEnd";

    public FastImageRequestListener(String key) {
    }

    @Override
    public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<T> target, boolean isFirstResource) {
        if (!(target instanceof ImageViewTarget)) {
            return false;
        }
        FastImageViewWithUrl view = (FastImageViewWithUrl) ((ImageViewTarget) target).getView();
        ThemedReactContext context = (ThemedReactContext) view.getContext();
        EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.getId());
        int surfaceId = UIManagerHelper.getSurfaceId(view);
        String error = e != null && e.getMessage() != null ? e.getMessage() : "Load Failed";

        if (dispatcher != null) {
            dispatcher.dispatchEvent(new FastImageErrorEvent(surfaceId, view.getId(), error));
            dispatcher.dispatchEvent(new FastImageLoadEndEvent(surfaceId, view.getId()));
        }
        return false;
    }

    @Override
    public boolean onResourceReady(T resource, Object model, Target<T> target, DataSource dataSource, boolean isFirstResource) {
        if (!(target instanceof ImageViewTarget)) {
            return false;
        }
        FastImageViewWithUrl view = (FastImageViewWithUrl) ((ImageViewTarget) target).getView();
        ThemedReactContext context = (ThemedReactContext) view.getContext();
        EventDispatcher dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.getId());
        int surfaceId = UIManagerHelper.getSurfaceId(view);

        if (dispatcher != null) {
            int width = resource.getIntrinsicWidth();
            int height = resource.getIntrinsicHeight();

            dispatcher.dispatchEvent(new FastImageLoadEvent(surfaceId, view.getId(), width, height));
            dispatcher.dispatchEvent(new FastImageLoadEndEvent(surfaceId, view.getId()));
        }
        return false;
    }
}
