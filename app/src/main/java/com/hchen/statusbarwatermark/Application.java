package com.hchen.statusbarwatermark;

import static com.hchen.hooktool.HCInit.LOG_D;

import com.hchen.hooktool.HCInit;
import com.hchen.hooktool.utils.PrefsTool;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HCInit.initBasicData(new HCInit.BasicData()
            .setTag("StatusBarWatermark")
            .setLogLevel(LOG_D)
            .setPrefsName("status_bar_watermark")
            .setModulePackageName(BuildConfig.APPLICATION_ID)
        );
        PrefsTool.prefs(this, "status_bar_watermark");
    }
}
