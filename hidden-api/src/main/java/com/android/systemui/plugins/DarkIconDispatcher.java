package com.android.systemui.plugins;

import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;

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

    static boolean isInAreas(Collection<Rect> collection, View view) {
        throw new RuntimeException("Stub!");
    }
}
