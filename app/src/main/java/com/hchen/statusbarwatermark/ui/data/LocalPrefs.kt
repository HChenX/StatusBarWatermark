package com.hchen.statusbarwatermark.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hchen.hooktool.callback.IPrefsApply

@Composable
fun rememberBooleanPreference(
    prefs: IPrefsApply? = null,
    key: String,
    defaultValue: Boolean
): MutableState<Boolean> {
    return rememberPreference(key = key, defaultValue = defaultValue, prefs = prefs)
}

@Composable
fun rememberIntPreference(
    prefs: IPrefsApply? = null,
    key: String,
    defaultValue: Int
): MutableState<Int> {
    return rememberPreference(key = key, defaultValue = defaultValue, prefs = prefs)
}

@Composable
fun rememberLongPreference(
    prefs: IPrefsApply? = null,
    key: String,
    defaultValue: Long
): MutableState<Long> {
    return rememberPreference(key = key, defaultValue = defaultValue, prefs = prefs)
}

@Composable
fun rememberFloatPreference(
    prefs: IPrefsApply? = null,
    key: String,
    defaultValue: Float
): MutableState<Float> {
    return rememberPreference(key = key, defaultValue = defaultValue, prefs = prefs)
}

@Composable
fun rememberStringPreference(
    prefs: IPrefsApply? = null,
    key: String,
    defaultValue: String
): MutableState<String> {
    return rememberPreference(key = key, defaultValue = defaultValue, prefs = prefs)
}

@Composable
fun rememberStringSetPreference(
    prefs: IPrefsApply? = null,
    key: String,
    defaultValue: Set<String>
): MutableState<Set<String>> {
    return rememberPreference(key = key, defaultValue = defaultValue, prefs = prefs)
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T : Any> rememberPreference(
    prefs: IPrefsApply? = null,
    key: String,
    defaultValue: T
): MutableState<T> {
    val prefsRefresh = LocalPrefsRefresh.current
    val prefs = prefs ?: LocalPrefs.current
    val state = remember(prefs, prefsRefresh.value) {
        mutableStateOf(prefs.get(key, defaultValue) as T)
    }

    LaunchedEffect(state.value, prefsRefresh.value) {
        when (val value = state.value) {
            is Boolean -> prefs.editor().putBoolean(key, value).apply()
            is Int -> prefs.editor().putInt(key, value).apply()
            is Long -> prefs.editor().putLong(key, value).apply()
            is Float -> prefs.editor().putFloat(key, value).apply()
            is String -> prefs.editor().putString(key, value).apply()
            is Set<*> -> prefs.editor().putStringSet(key, value as Set<String>).apply()
        }
    }

    return state
}