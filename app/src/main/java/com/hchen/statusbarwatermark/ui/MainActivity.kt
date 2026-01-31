package com.hchen.statusbarwatermark.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hchen.hooktool.utils.PrefsTool
import com.hchen.hooktool.utils.ShellTool
import com.hchen.statusbarwatermark.R
import com.hchen.statusbarwatermark.ui.base.SuperButtonDialog
import com.hchen.statusbarwatermark.ui.base.SuperEditDialog
import com.hchen.statusbarwatermark.ui.base.SuperTextSlider
import com.hchen.statusbarwatermark.ui.data.LocalPrefs
import com.hchen.statusbarwatermark.ui.data.LocalPrefsRefresh
import com.hchen.statusbarwatermark.ui.data.PrefsKey
import com.hchen.statusbarwatermark.ui.data.UIConstants.MEDIUM_WIDTH_THRESHOLD
import com.hchen.statusbarwatermark.ui.data.UIConstants.PORTRAIT_ASPECT_RATIO_THRESHOLD
import com.hchen.statusbarwatermark.ui.data.UIConstants.WIDE_SCREEN_THRESHOLD
import com.hchen.statusbarwatermark.ui.data.rememberBooleanPreference
import com.hchen.statusbarwatermark.ui.data.rememberFloatPreference
import com.hchen.statusbarwatermark.ui.data.rememberStringPreference
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.VerticalDivider
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            App()
        }
    }

    @Composable
    private fun App() {
        val prefs = PrefsTool.prefs(this)
        val prefsRefresh = remember { mutableIntStateOf(0) }

        CompositionLocalProvider(
            LocalPrefsRefresh provides prefsRefresh,
            LocalPrefs provides prefs
        ) {
            MiuixTheme(controller = ThemeController(ColorSchemeMode.System)) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isDefinitelyWide = maxWidth > WIDE_SCREEN_THRESHOLD
                    val isWideByShape = maxWidth > MEDIUM_WIDTH_THRESHOLD && (maxHeight.value / maxWidth.value < PORTRAIT_ASPECT_RATIO_THRESHOLD)
                    val isWideScreen = isDefinitelyWide || isWideByShape
                    if (isWideScreen) WideScreenLayout() else CompactScreenLayout()
                }
            }
        }
    }

    @Composable
    private fun CompactScreenLayout() {
        val scrollBehavior = MiuixScrollBehavior()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = stringResource(R.string.app_name),
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            UiContent(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()), nestedScrollConnection = scrollBehavior.nestedScrollConnection)
        }
    }

    @Composable
    private fun WideScreenLayout() {
        val windowWidth = LocalWindowInfo.current.containerSize.width
        var weight by remember(windowWidth) { mutableFloatStateOf(0.4f) }
        val dragState = rememberDraggableState { delta ->
            val nextWeight = weight + delta / windowWidth
            val clampedWeight = nextWeight.coerceIn(0.2f, 0.5f)
            if (clampedWeight == nextWeight) {
                weight = clampedWeight
            }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MiuixTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.weight(weight = weight)) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 18.dp, end = 12.dp),
                        topBar = {
                            TopAppBar(
                                title = stringResource(R.string.app_name),
                                horizontalPadding = 12.dp,
                            )
                        },
                        popupHost = {}
                    ) {
                    }
                }
                VerticalDivider(
                    modifier = Modifier
                        .draggable(
                            state = dragState,
                            orientation = Orientation.Horizontal
                        )
                        .padding(horizontal = 6.dp)
                )
                Box(modifier = Modifier.weight(weight = 1f - weight)) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 6.dp),
                        topBar = {
                            SmallTopAppBar(
                                title = stringResource(R.string.app_name)
                            )
                        },
                        popupHost = {}
                    ) { paddingValues ->
                        UiContent(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))
                    }
                }
            }
        }
    }

    @Composable
    private fun UiContent(modifier: Modifier, nestedScrollConnection: NestedScrollConnection? = null) {
        val isShowSystemUiRestartDialog = remember { mutableStateOf(false) }
        val isShowWatermarkContentEditDialog = remember { mutableStateOf(false) }

        var watermarkShow by rememberBooleanPreference(key = PrefsKey.WATERMARK_SHOW, defaultValue = false)
        var showOnControlCenter by rememberBooleanPreference(key = PrefsKey.WATERMARK_SHOW_ON_CONTROL_CENTER, defaultValue = false)
        var watermarkContent by rememberStringPreference(key = PrefsKey.WATERMARK_CONTENT, defaultValue = "")
        val watermarkSize = rememberFloatPreference(key = PrefsKey.WATERMARK_SIZE, defaultValue = 12f)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
                .scrollEndHaptic()
                .overScrollVertical()
                .then(
                    if (nestedScrollConnection != null) Modifier.nestedScroll(nestedScrollConnection) else Modifier
                )
                .clipToBounds(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            overscrollEffect = null
        ) {
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    BasicComponent(
                        summary = stringResource(R.string.basic_tip)
                    )
                }
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    SuperArrow(
                        title = stringResource(R.string.restart_systemui),
                        summary = stringResource(R.string.restart_systemui_root),
                        onClick = {
                            isShowSystemUiRestartDialog.value = true
                        }
                    )
                }
                SmallTitle(text = stringResource(R.string.basic_fun))
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    SuperSwitch(
                        title = stringResource(R.string.watermark_show),
                        summary = stringResource(R.string.watermark_show_details),
                        checked = watermarkShow,
                        onCheckedChange = {
                            watermarkShow = it
                        }
                    )
                    SuperSwitch(
                        title = stringResource(R.string.watermark_show_on_control_center),
                        summary = stringResource(R.string.watermark_show_on_control_center_details),
                        checked = showOnControlCenter,
                        onCheckedChange = {
                            showOnControlCenter = it
                        }
                    )
                    SuperArrow(
                        title = stringResource(R.string.watermark_content),
                        summary = stringResource(R.string.watermark_content_details),
                        endActions = {
                            Text(
                                text = watermarkContent,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .align(Alignment.CenterVertically)
                                    .weight(1f, fill = false),
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                                textAlign = TextAlign.End
                            )
                        },
                        onClick = {
                            isShowWatermarkContentEditDialog.value = true
                        }
                    )
                    SuperTextSlider(
                        title = stringResource(R.string.watermark_size),
                        summary = stringResource(R.string.watermark_size_details),
                        value = watermarkSize,
                        valueRange = 10f..15f,
                        keyPoints = listOf(13f),
                        unit = "sp",
                        onValueChange = {
                            watermarkSize.value = it
                        }
                    )
                }

                SuperEditDialog(
                    show = isShowWatermarkContentEditDialog,
                    title = stringResource(R.string.watermark_content),
                    summary = stringResource(R.string.watermark_content_details),
                    text = watermarkContent,
                    onDismissRequest = {
                        isShowWatermarkContentEditDialog.value = false
                    },
                    onClickCancel = {
                        isShowWatermarkContentEditDialog.value = false
                    },
                    onClickConfirm = {
                        isShowWatermarkContentEditDialog.value = false
                    },
                    onTextChanged = {
                        watermarkContent = it
                    }
                )
                SuperButtonDialog(
                    show = isShowSystemUiRestartDialog,
                    title = stringResource(R.string.tip),
                    summary = stringResource(R.string.restart_systemui_message),
                    onDismissRequest = {
                        isShowSystemUiRestartDialog.value = false
                    },
                    onClickCancel = {
                        isShowSystemUiRestartDialog.value = false
                    },
                    onClickConfirm = {
                        ShellTool.obtain(true).cmd("killall com.android.systemui").exec()
                        isShowSystemUiRestartDialog.value = false
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ShellTool.close()
    }
}