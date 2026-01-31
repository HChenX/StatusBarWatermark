package com.android.systemui.plugins;

import android.graphics.Rect;

import java.util.ArrayList;

public interface DarkIconDispatcher {
    interface DarkReceiver {
        int VERSION = 3;

        void onDarkChanged(ArrayList<Rect> areas, float darkIntensity, int tint);

        default void onDarkChangedWithContrast(ArrayList<Rect> areas, int color, int contrast) {
            throw new RuntimeException("Stub!");
        }

        default void onLightDarkTintChanged(int lightColor, int darkColor, boolean useTint) {
            throw new RuntimeException("Stub!");
        }
    }
}
