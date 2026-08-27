package com.dylanvann.fastimage.transformations;

import android.graphics.Bitmap;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(31)
public class FastImageBlurEffectEngine {
    private static final float BLUR_REFERENCE_SIZE = 540f;
    private static final float BLUR_MIN_INPUT = 0.1F;
    private static final float BLUR_MAX_INPUT = 200f;
    private static final int SOURCE_TAG_ID = 0xcafebabe;
    private static final int RADIUS_TAG_ID = 0xbabecafe;
    private static final int LISTENER_TAG_ID = 0xdeadbeef;

    /**
     * Scales the image and blurs it with RenderEffect.
     */
    public static Bitmap apply(Bitmap src, float radius, ImageView view) {
        view.setTag(SOURCE_TAG_ID, src);
        view.setTag(RADIUS_TAG_ID, radius);
        ensureDynamicApply(view);

        float scaleFactorX = src.getWidth() / BLUR_REFERENCE_SIZE;
        float scaleFactorY = src.getHeight() / BLUR_REFERENCE_SIZE;
        float scaleX = view.getWidth() * scaleFactorX / src.getWidth();
        float scaleY = view.getHeight() * scaleFactorY / src.getHeight();
        float scale = (scaleX + scaleY) / 2f;

        float radiusScaled = radius * scale;
        float radiusNormalized = Math.max(BLUR_MIN_INPUT, Math.min(BLUR_MAX_INPUT, radiusScaled));
        return blur(src, radiusNormalized, view);
    }

    /**
     * Used to create blur with RenderEffect.
     */
    static Bitmap blur(Bitmap src, float radius, ImageView view) {
        RenderEffect blur = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP);
        view.setRenderEffect(blur);
        view.invalidate();
        return src;
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

            Object srcTag = view.getTag(SOURCE_TAG_ID);
            if (!(srcTag instanceof Bitmap src)) return;

            Object radiusTag = view.getTag(RADIUS_TAG_ID);
            if (!(radiusTag instanceof Number radiusNumber)) return;
            float radius = radiusNumber.floatValue();

            apply(src, radius, view);
        };
        view.addOnLayoutChangeListener(listener);
        view.setTag(LISTENER_TAG_ID, listener);
    }

    /**
     * Cleanup method for RenderEffect.
     */
    public static void clean(@Nullable ImageView view) {
        if (view == null) return;

        view.setRenderEffect(null);
        view.invalidate();

        Object tag = view.getTag(LISTENER_TAG_ID);
        if (tag instanceof View.OnLayoutChangeListener listener) {
            view.removeOnLayoutChangeListener(listener);
        }

        view.setTag(SOURCE_TAG_ID, null);
        view.setTag(RADIUS_TAG_ID, null);
        view.setTag(LISTENER_TAG_ID, null);
    }
}
