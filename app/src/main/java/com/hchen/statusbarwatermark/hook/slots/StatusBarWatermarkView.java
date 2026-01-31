package com.hchen.statusbarwatermark.hook.slots;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.DarkIconDispatcherExt;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.anim.StatusBarIconAnimHelper;
import com.hchen.statusbarwatermark.data.StatusBarSlots;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class StatusBarWatermarkView extends AppCompatTextView implements StatusIconDisplayable {
    private StatusBarIconAnimHelper helper;
    private int visibleState;
    private boolean visible;
    private boolean blocked;
    private ArrayList<Rect> areas;
    private float darkIntensity;
    private int tint;
    private int lightColor;
    private int darkColor;
    private boolean useTint;

    public StatusBarWatermarkView(@NonNull Context context) {
        super(context);
        initialization(context);
    }

    public StatusBarWatermarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    public StatusBarWatermarkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(context);
    }

    private void initialization(@NonNull Context context) {
        visibleState = 0;
        areas = new ArrayList<>(2);
        helper = new StatusBarIconAnimHelper(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateResources();
    }

    @Override
    public void getDrawingRect(Rect outRect) {
        super.getDrawingRect(outRect);
        outRect.left = (int) (outRect.left + getTranslationX());
        outRect.right = (int) (outRect.right + getTranslationX());
        outRect.top = (int) (outRect.top + getTranslationY());
        outRect.bottom = (int) (outRect.bottom + getTranslationY());
    }

    private void setRemove(boolean remove) {
        helper.removeFlag = remove;
    }

    @Override
    public boolean getRemoveFlag() {
        return helper.removeFlag;
    }

    @Override
    public void performRemove() {
        if (!helper.removeFlag) {
            return;
        }
        helper.removeFlag = false;
        helper.view.setVisibility(GONE);
    }

    @Override
    public String getSlot() {
        return StatusBarSlots.status_bar_watermark;
    }

    @Override
    public int getVisibleState() {
        return visibleState;
    }

    @Override
    public boolean isIconVisible() {
        return (visible && !blocked) || helper.removeFlag;
    }

    @Override
    public void onDensityOrFontScaleChanged() {
        updateResources();
    }

    @Override
    public void setAnimationEnable(boolean enabled) {
        helper.animateEnable = enabled;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            updateVisibility();
        }
    }

    @Override
    public void setBlocked(boolean blocked) {
        if (this.blocked != blocked) {
            this.blocked = blocked;
            updateVisibility();
        }
    }

    @Override
    public boolean isIconBlocked() {
        return blocked;
    }

    @Override
    public boolean getDeemHide() {
        return false;
    }

    @Override
    public void setDeemHide(boolean deemHide) {
        // Do Nothing
    }

    @Override
    public void setDecorColor(int decorColor) {
        // Do Nothing
    }

    @Override
    public void setDripEnd(boolean dripEnd) {
        // Do Nothing
    }

    @Override
    public void setStaticDrawableColor(int color) {
        // Do Nothing
    }

    @Override
    public void setStaticDrawableColor(int color, int contrastColor) {
        // Do Nothing
    }

    @Override
    public void setVisibleState(int visibleState) {
        setVisibleState(visibleState, false);
    }

    @Override
    public void setVisibleState(int visibleState, boolean animated) {
        if (helper.removeFlag || this.visibleState == visibleState) {
            return;
        }
        this.visibleState = visibleState;
        setVisibleState(visibleState != 2 ? VISIBLE : INVISIBLE);
    }

    @Override
    public void onDarkChanged(ArrayList<Rect> areas, float darkIntensity, int tint) {
        this.areas = areas;
        this.darkIntensity = darkIntensity;
        this.tint = tint;
        updateLightDarkTint();
    }

    @Override
    public void onLightDarkTintChanged(int lightColor, int darkColor, boolean useTint) {
        this.lightColor = lightColor;
        this.darkColor = darkColor;
        this.useTint = useTint;
        updateLightDarkTint();
    }

    @Override
    public void onMiuiThemeChanged$1(boolean isThemeChanged) {
        updateResources();
    }

    @Override
    public int getBlurRadius() {
        return helper.blurRadius;
    }

    @Override
    public void setBlurRadius(int blurRadius) {
        helper.setBlurRadius(blurRadius);
    }

    private final boolean hasTargetMethod = Arrays.stream(DarkIconDispatcherExt.class.getDeclaredMethods()).anyMatch(
        new Predicate<Method>() {
            @Override
            public boolean test(Method method) {
                return TextUtils.equals(method.getName(), "getTint");
            }
        }
    );

    private void updateLightDarkTint() {
        if (hasTargetMethod) {
            if (useTint) {
                setTextColor(DarkIconDispatcherExt.getTint(areas, this, tint));
                return;
            }
            setTextColor(
                DarkIconDispatcherExt.getDarkIntensity(areas, this, darkIntensity) > 0.0f ?
                    darkColor :
                    lightColor
            );
        } else {
            if (useTint) {
                setTextColor(DarkIconDispatcherExt.Companion.getTint(areas, this, tint));
                return;
            }
            setTextColor(
                (DarkIconDispatcher.isInAreas(areas, this) ? darkIntensity : 0.0f) > 0.0f ?
                    darkColor :
                    lightColor
            );
        }
    }

    private void updateResources() {
        int style = getResources().getIdentifier("TextAppearance.StatusBar.NetWorkSpeedNumber", "style", getContext().getPackageName());
        setTextAppearance(style);
        setTextSize(13f);
        updateLightDarkTint();
    }

    private void updateVisibility() {
        if (visible && !blocked) {
            if (helper.removeFlag && getVisibility() == VISIBLE) {
                visibleState = 2;
                setRemove(false);
                requestLayout();
                return;
            }
            setRemove(false);
            setVisibility(VISIBLE);
            return;
        }
        if (getVisibility() != GONE && helper.animateEnable) {
            setRemove(true);
            requestLayout();
        } else {
            setRemove(false);
            setVisibility(GONE);
        }
    }
}
