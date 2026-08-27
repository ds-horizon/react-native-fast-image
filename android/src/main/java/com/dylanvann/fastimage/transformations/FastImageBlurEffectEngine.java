package com.dylanvann.fastimage.transformations;

import android.graphics.Bitmap;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.facebook.react.bridge.UiThreadUtil;

@RequiresApi(31)
public class FastImageBlurEffectEngine {
    private static final float BLUR_REFERENCE_SIZE = 540f;
    private static final float BLUR_MIN_INPUT = 0.1F;
    private static final float BLUR_MAX_INPUT = 200f;
    private static final int RADIUS_TAG_ID = 0xbabecafe;
    private static final int LISTENER_TAG_ID = 0xdeadbeef;

    /**
     * Scales the image and blurs it with RenderEffect.
     */
    public static Bitmap apply(Bitmap src, float radius, ImageView view) {
        apply(radius, view);
        return src;
    }

    public static void apply(float radius, ImageView view) {
        UiThreadUtil.runOnUiThread(() -> {
            view.setTag(RADIUS_TAG_ID, radius);
            ensureDynamicApply(view);

            float scale = (view.getWidth() + (float) view.getHeight()) / (2f * BLUR_REFERENCE_SIZE);
            float radiusInput = Math.max(BLUR_MIN_INPUT, Math.min(BLUR_MAX_INPUT, radius));
            float radiusNormalized = Math.max(BLUR_MIN_INPUT, Math.min(BLUR_MAX_INPUT, radiusInput * scale));
            view.setRenderEffect(RenderEffect.createBlurEffect(radiusNormalized, radiusNormalized, Shader.TileMode.CLAMP));
            view.invalidate();
        });
    }

    /**
     * RenderEffect only applies the blur effect to the View layer.
     * It must be reapplied when the dimensions change.
     */
    private static void ensureDynamicApply(ImageView view) {
        Object tag = view.getTag(LISTENER_TAG_ID);
        if (tag instanceof View.OnLayoutChangeListener) return;

        View.OnLayoutChangeListener listener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (right - left == oldRight - oldLeft && bottom - top == oldBottom - oldTop) return;

            Object radiusTag = view.getTag(RADIUS_TAG_ID);
            if (!(radiusTag instanceof Number radiusNumber)) return;
            float radius = radiusNumber.floatValue();

            apply(radius, view);
        };
        view.addOnLayoutChangeListener(listener);
        view.setTag(LISTENER_TAG_ID, listener);
    }

    /**
     * Cleanup method for RenderEffect.
     */
    public static void clean(@Nullable ImageView view) {
        if (view == null) return;
        UiThreadUtil.runOnUiThread(() -> {
            view.setRenderEffect(null);
            view.invalidate();

            Object tag = view.getTag(LISTENER_TAG_ID);
            if (tag instanceof View.OnLayoutChangeListener listener) {
                view.removeOnLayoutChangeListener(listener);
            }

            view.setTag(RADIUS_TAG_ID, null);
            view.setTag(LISTENER_TAG_ID, null);
        });
    }
}
