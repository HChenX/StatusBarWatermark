package com.android.systemui.statusbar;

import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.views.IBlurEffect;

public interface StatusIconDisplayable  extends DarkIconDispatcher.DarkReceiver, IBlurEffect {
    boolean getDeemHide();

    boolean getRemoveFlag();

    String getSlot();

    int getVisibleState();

    default boolean isIconBlocked() {
        throw new RuntimeException("Stub!");
    }

    boolean isIconVisible();

    default boolean isSignalView() {
        throw new RuntimeException("Stub!");
    }

    void setAnimationEnable(boolean enabled);

    void setBlocked(boolean blocked);

    void setDecorColor(int decorColor);

    void setDeemHide(boolean deemHide);

    void setDripEnd(boolean dripEnd);

    void setStaticDrawableColor(int color);

    void setStaticDrawableColor(int color, int contrastColor);

    void setVisibleState(int visibleState);

    void setVisibleState(int visibleState, boolean animated);

    default void onMiuiThemeChanged$1(boolean isThemeChanged) {
        throw new RuntimeException("Stub!");
    }

    default void onDensityOrFontScaleChanged() {
        throw new RuntimeException("Stub!");
    }

    default void onUiModeChanged() {
        throw new RuntimeException("Stub!");
    }

    default void performRemove() {
        throw new RuntimeException("Stub!");
    }
}
