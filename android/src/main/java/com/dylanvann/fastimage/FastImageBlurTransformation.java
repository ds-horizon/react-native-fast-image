package com.dylanvann.fastimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.dylanvann.fastimage.transformations.FastImageBlurCompat;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class FastImageBlurTransformation extends BitmapTransformation {
    private static final float BLUR_MIN_INPUT = 0.1f;
    private static final float BLUR_MAX_INPUT = 200f;

    private static final String ID = "com.dylanvann.fastimage.FastImageBlurTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final Context context;
    private final float radius;
    private final ImageView view;

    public FastImageBlurTransformation(@NonNull Context context, float radius, ImageView view) {
        this.context = context.getApplicationContext();
        this.radius = normalizeBlurRadius(radius);
        this.view = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? view : null;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return FastImageBlurCompat.blur(context, toTransform, radius, view);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return false;
        }

        if (o instanceof FastImageBlurTransformation other) {
            return radius == other.radius;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + Float.valueOf(radius).hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        messageDigest.update(ByteBuffer.allocate(4).putFloat(radius).array());
    }

    private float normalizeBlurRadius(float radius) {
        return Math.min(BLUR_MAX_INPUT, Math.max(BLUR_MIN_INPUT, radius));
    }

    public static void clean(@Nullable ImageView view) {
        FastImageBlurCompat.clean(view);
    }
}
