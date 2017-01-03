package com.tanguyantoine.react;

import android.support.v4.media.RatingCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class MusicControlListener extends MediaSessionCompat.Callback {

    private static void sendEvent(ReactApplicationContext context, String type, Double value) {
        WritableMap data = Arguments.createMap();
        data.putString("name", type);
        if(value != null) data.putDouble("value", value);

        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RNMusicControlEvent", data);
    }

    private final ReactApplicationContext context;

    MusicControlListener(ReactApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onPlay() {
        sendEvent(context, "play", null);
    }

    @Override
    public void onPause() {
        sendEvent(context, "pause", null);
    }

    @Override
    public void onStop() {
        sendEvent(context, "stop", null);
    }

    @Override
    public void onSkipToNext() {
        sendEvent(context, "nextTrack", null);
    }

    @Override
    public void onSkipToPrevious() {
        sendEvent(context, "previousTrack", null);
    }

    @Override
    public void onSeekTo(long pos) {
        sendEvent(context, "seek", pos / 1000D);
    }

    @Override
    public void onFastForward() {
        sendEvent(context, "seekForward", null);
    }

    @Override
    public void onRewind() {
        sendEvent(context, "seekBackward", null);
    }

    @Override
    public void onSetRating(RatingCompat rating) {
        sendEvent(context, "rate", (double)rating.getPercentRating());
    }

    public static class VolumeListener extends VolumeProviderCompat {

        private final ReactApplicationContext context;
        public VolumeListener(ReactApplicationContext context, boolean changeable, int currentVolume) {
            super(changeable ? VolumeProviderCompat.VOLUME_CONTROL_FIXED : VOLUME_CONTROL_ABSOLUTE, 100, currentVolume);
            this.context = context;
        }

        public boolean isChangeable() {
            return getVolumeControl() != VolumeProviderCompat.VOLUME_CONTROL_FIXED;
        }

        @Override
        public void onSetVolumeTo(int volume) {
            sendEvent(context, "volume", (double)volume);
        }

        public VolumeListener create(Boolean changeable, Integer currentVolume) {
            if(changeable == null) changeable = isChangeable();
            if(currentVolume == null) currentVolume = getCurrentVolume();
            if(changeable == isChangeable() && currentVolume == getCurrentVolume()) return this;

            return new VolumeListener(context, changeable, currentVolume);
        }
    }

}
