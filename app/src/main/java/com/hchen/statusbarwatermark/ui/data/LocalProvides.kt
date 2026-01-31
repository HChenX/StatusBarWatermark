package com.hchen.statusbarwatermark.ui.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import com.hchen.hooktool.callback.IPrefsApply

val LocalPrefsRefresh = compositionLocalOf<MutableState<Int>> { error("No prefs refresh") }
val LocalPrefs = compositionLocalOf<IPrefsApply> { error("No prefs") }