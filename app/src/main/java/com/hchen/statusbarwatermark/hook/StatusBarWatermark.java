package com.hchen.statusbarwatermark.hook;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hchen.hooktool.HCBase;
import com.hchen.hooktool.HCData;
import com.hchen.hooktool.hook.IHook;
import com.hchen.statusbarwatermark.data.StatusBarSlots;

import java.util.ArrayList;
import java.util.Arrays;

import dalvik.system.PathClassLoader;

public class StatusBarWatermark extends HCBase {
    @Override
    protected void init() {
        ClassLoader myClassLoader = new PathClassLoader(HCData.getModulePath(), classLoader);

        hookConstructor("com.android.systemui.statusbar.phone.ui.StatusBarIconList",
            String[].class,
            new IHook() {
                @Override
                public void before() {
                    String[] slots = (String[]) getArg(0);
                    ArrayList<String> list = new ArrayList<>(Arrays.asList(slots));
                    if (!list.contains(StatusBarSlots.status_bar_watermark)) {
                        list.add(0, StatusBarSlots.status_bar_watermark);
                        String[] finalSlots = list.toArray(String[]::new);
                        setArg(0, finalSlots);
                    }
                }
            }
        );

        hookMethod("com.android.systemui.statusbar.phone.CentralSurfacesImpl",
            "start",
            new IHook() {
                @Override
                public void after() {
                    Object mIconPolicy = getThisField("mIconPolicy");
                    Object mIconController = getField(mIconPolicy, "mIconController");
                    Object holder = callStaticMethod("com.hchen.statusbarwatermark.hook.helper.StatusBarIconHolder", myClassLoader, "newInstance");
                    if (holder != null) {
                        setField(holder, "type", 513);
                        callMethod(mIconController, "setIcon", StatusBarSlots.status_bar_watermark, holder);
                    }
                }
            }
        );

        hookMethod("com.android.systemui.statusbar.phone.ui.IconManager",
            "addHolder",
            int.class, String.class, boolean.class, "com.android.systemui.statusbar.phone.StatusBarIconHolder",
            new IHook() {
                @Override
                public void before() {
                    int index = (int) getArg(0);
                    String slot = (String) getArg(1);
                    boolean blocked = (boolean) getArg(2);
                    if (TextUtils.equals(slot, StatusBarSlots.status_bar_watermark)) {
                        ViewGroup group = (ViewGroup) getThisField("mGroup");
                        Context context = (Context) getThisField("mContext");
                        TextView watermarkView = (TextView) newInstance("com.hchen.statusbarwatermark.hook.slots.StatusBarWatermarkView", myClassLoader, context);
                        callMethod(watermarkView, "setBlocked", blocked);
                        group.addView(watermarkView, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        watermarkView.setText("TEST");
                        setResult(watermarkView);
                    }
                }
            }
        );

        @SuppressWarnings("unchecked")
        ArrayList<String> CONTROL_CENTER_BLOCK_LIST = (ArrayList<String>) getStaticField("com.android.systemui.statusbar.phone.MiuiIconManagerUtils", "CONTROL_CENTER_BLOCK_LIST");
        if (!CONTROL_CENTER_BLOCK_LIST.contains(StatusBarSlots.status_bar_watermark)) {
            CONTROL_CENTER_BLOCK_LIST.add(StatusBarSlots.status_bar_watermark);
            setStaticField("com.android.systemui.statusbar.phone.MiuiIconManagerUtils", "CONTROL_CENTER_BLOCK_LIST", CONTROL_CENTER_BLOCK_LIST);
        }
    }
}
