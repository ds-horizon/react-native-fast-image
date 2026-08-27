package com.dylanvann.fastimage.events;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;

public class FastImageErrorEvent extends Event<FastImageErrorEvent> {

    private final String error;

    public FastImageErrorEvent(int surfaceId, int viewTag, @NonNull String error) {
        super(surfaceId, viewTag);
        this.error = error;
    }
    @NonNull
    @Override
    public String getEventName() {
        return "onFastImageError";
    }

    @Override
    protected WritableMap getEventData() {
        WritableMap eventData = Arguments.createMap();
        eventData.putString("error", error);
        return eventData;
    }
}
