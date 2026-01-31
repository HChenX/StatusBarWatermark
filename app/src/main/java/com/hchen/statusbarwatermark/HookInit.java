package com.hchen.statusbarwatermark;

import static com.hchen.hooktool.HCInit.LOG_D;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hchen.hooktool.HCEntrance;
import com.hchen.hooktool.HCInit;
import com.hchen.statusbarwatermark.hook.StatusBarWatermark;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookInit extends HCEntrance {
    @NonNull
    @Override
    public HCInit.BasicData initHC(@NonNull HCInit.BasicData basicData) {
        return basicData.setTag("StatusBarWatermark")
            .setLogLevel(LOG_D)
            .setModulePackageName(BuildConfig.APPLICATION_ID)
            .setPrefsName("status_bar_watermark")
            .setLogExpandPath("com.hchen.statusbarwatermark.hook");
    }

    @NonNull
    @Override
    public String[] ignorePackageNameList() {
        return new String[]{
            "com.miui.contentcatcher",
            "com.android.providers.settings",
            "com.android.server.telecom",
            "com.google.android.webview"
        };
    }

    @Override
    public void onLoadPackage(@NonNull XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (TextUtils.equals(loadPackageParam.packageName, "com.android.systemui")) {
            HCInit.initLoadPackageParam(loadPackageParam);
            new StatusBarWatermark().onLoadPackage();
        }
    }
}
