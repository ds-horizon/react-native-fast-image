package com.dylanvann.fastimage;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.facebook.react.views.imagehelper.ImageSource;

import javax.annotation.Nullable;

public class FastImageSource {
    private static final String DATA_SCHEME = "data";
    private static final String LOCAL_RESOURCE_SCHEME = "res";
    private static final String ANDROID_RESOURCE_SCHEME = "android.resource";
    private static final String ANDROID_CONTENT_SCHEME = "content";
    private static final String LOCAL_FILE_SCHEME = "file";
    private static final String RAW_RESOURCE_FOLDER = "raw";
    private static final String HTTP_SCHEME_PREFIX = "http";
    private static final String DATA_URI_PREFIX = "data:";

    private final Headers mHeaders;
    private Uri mUri;
    private String mSource;

    public static boolean isBase64Uri(Uri uri) {
        return DATA_SCHEME.equals(uri.getScheme());
    }

    public static boolean isLocalResourceUri(Uri uri) {
        return LOCAL_RESOURCE_SCHEME.equals(uri.getScheme());
    }

    public static boolean isResourceUri(Uri uri) {
        return ANDROID_RESOURCE_SCHEME.equals(uri.getScheme());
    }

    public static boolean isContentUri(Uri uri) {
        return ANDROID_CONTENT_SCHEME.equals(uri.getScheme());
    }

    public static boolean isLocalFileUri(Uri uri) {
        return LOCAL_FILE_SCHEME.equals(uri.getScheme());
    }

    public FastImageSource(Context context, String source) {
        this(context, source, null);
    }

    public FastImageSource(Context context, String source, @Nullable Headers headers) {
        this(context, source, 0.0d, 0.0d, headers);
    }

    public FastImageSource(Context context, String source, double width, double height, @Nullable Headers headers) {
        ImageSource imageSource = new ImageSource(context, source, width, height);
        mSource = imageSource.getSource();
        mHeaders = headers == null ? Headers.DEFAULT : headers;
        mUri = resolveResourceUri(context, source, imageSource.getUri());

        boolean isUriEmpty = mUri == null || TextUtils.isEmpty(mUri.toString());
        boolean isResourceMissing = isResource() && isUriEmpty;

        if (isResourceMissing) {
            throw new Resources.NotFoundException("Local Resource Not Found. Resource: '" + getSource() + "'.");
        }

        boolean shouldConvertLocalResourceScheme = mUri != null && isLocalResourceUri(mUri);

        if (shouldConvertLocalResourceScheme) {
            // Convert res:/ scheme to android.resource:// so glide can understand the uri.
            String convertedUri = mUri.toString().replace("res:/", ANDROID_RESOURCE_SCHEME + "://" + context.getPackageName() + "/");
            mUri = Uri.parse(convertedUri);
        }
    }

    /**
     * Attempts to find a resource URI for the given source.
     * First uses the URI from ImageSource (checks drawable folder), 
     * then falls back to raw folder for assets like SVGs in release builds.
     *
     * @param context Application context
     * @param source Original source string
     * @param initialUri URI resolved by ImageSource
     * @return Resolved URI or null if not found
     */
    @Nullable
    private Uri resolveResourceUri(Context context, String source, Uri initialUri) {
        boolean isInitialUriValid = initialUri != null && !TextUtils.isEmpty(initialUri.toString());

        if (isInitialUriValid) {
            return initialUri;
        }

        boolean isSourceNull = source == null;
        boolean isRemoteUrl = source != null && source.startsWith(HTTP_SCHEME_PREFIX);
        boolean isDataUri = source != null && source.startsWith(DATA_URI_PREFIX);
        boolean shouldSkipRawLookup = isSourceNull || isRemoteUrl || isDataUri;

        if (shouldSkipRawLookup) {
            return initialUri;
        }

        return tryResolveFromRawFolder(context, source);
    }

    /**
     * Attempts to find a resource in the res/raw folder.
     * This is needed because SVG and some other assets are bundled to res/raw/ 
     * in Android release builds, but ImageSource only checks res/drawable/.
     *
     * @param context Application context
     * @param source Original source string
     * @return URI pointing to raw resource, or null if not found
     */
    @Nullable
    private Uri tryResolveFromRawFolder(Context context, String source) {
        String rawResourceName = toAndroidResourceName(source);
        int rawResId = context.getResources().getIdentifier(
            rawResourceName,
            RAW_RESOURCE_FOLDER,
            context.getPackageName()
        );

        boolean resourceNotFound = rawResId == 0;

        if (resourceNotFound) {
            return null;
        }

        String rawResourceUri = ANDROID_RESOURCE_SCHEME + "://" +
            context.getPackageName() + "/" + RAW_RESOURCE_FOLDER + "/" + rawResourceName;

        return Uri.parse(rawResourceUri);
    }

    /**
     * Converts a source name to Android resource name format.
     * Android resources must be lowercase and cannot contain hyphens.
     * Hyphens are removed to match React Native's bundling behavior.
     *
     * @param source Original source name
     * @return Android-compatible resource name
     */
    private String toAndroidResourceName(String source) {
        return source.toLowerCase().replace("-", "");
    }

    public boolean isBase64Resource() {
        return mUri != null && FastImageSource.isBase64Uri(mUri);
    }

    public boolean isResource() {
        return mUri != null && FastImageSource.isResourceUri(mUri);
    }

    public boolean isLocalFile() {
        return mUri != null && FastImageSource.isLocalFileUri(mUri);
    }

    public boolean isContentUri() {
        return mUri != null && FastImageSource.isContentUri(mUri);
    }

    public Object getSourceForLoad() {
        if (isContentUri() || isBase64Resource()) {
            return getSource();
        }

        if (isResource() || isLocalFile()) {
            return getUri();
        }

        return getGlideUrl();
    }

    public Uri getUri() {
        return mUri;
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public GlideUrl getGlideUrl() {
        return new GlideUrl(getUri().toString(), getHeaders());
    }

    public String getSource() {
            return mSource; // Delegate to ImageSource
    }
}
