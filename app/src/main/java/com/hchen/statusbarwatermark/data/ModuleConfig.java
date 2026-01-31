package com.hchen.statusbarwatermark.data;

import static com.hchen.hooktool.log.XposedLog.logE;

import com.hchen.hooktool.utils.PrefsTool;
import com.hchen.statusbarwatermark.ui.data.PrefsKey;

public class ModuleConfig {
    private static final String TAG = "ModuleConfig";

    public static boolean isShowWatermark() {
        try {
            return PrefsTool.prefs().getBoolean(PrefsKey.WATERMARK_SHOW, false);
        } catch (Throwable e) {
            logE(TAG, e);
        }
        return false;
    }

    public static String getWatermarkContent() {
        try {
            return PrefsTool.prefs().getString(PrefsKey.WATERMARK_CONTENT, "");
        } catch (Throwable e) {
            logE(TAG, e);
        }
        return "";
    }

    public static float getWatermarkSize() {
        try {
            return PrefsTool.prefs().getFloat(PrefsKey.WATERMARK_SIZE, 13f);
        } catch (Throwable e) {
            logE(TAG, e);
        }
        return 13f;
    }

    public static boolean isShowOnControlCenter() {
        try {
            return PrefsTool.prefs().getBoolean(PrefsKey.WATERMARK_SHOW_ON_CONTROL_CENTER, false);
        } catch (Throwable e) {
            logE(TAG, e);
        }
        return false;
    }
}
