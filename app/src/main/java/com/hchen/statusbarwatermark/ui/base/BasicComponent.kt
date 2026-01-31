package com.hchen.statusbarwatermark.ui.base

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hchen.statusbarwatermark.R
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SliderDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.math.RoundingMode
import kotlin.math.roundToInt

@Composable
fun SuperTextSlider(
    title: String,
    summary: String? = null,
    unit: String = "",
    precision: Int = 0,
    value: MutableState<Float>,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    keyPoints: List<Float>? = null,
    onValueChange: (Float) -> Unit = {},
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val show = remember { mutableStateOf(false) }
    var text by remember(value.value) { mutableStateOf(value.value.toString()) }
    var error by remember(text) { mutableStateOf(text.toFloatOrNull()?.let { it !in valueRange } ?: true) }

    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current
    val haptic = LocalHapticFeedback.current

    SuperDialog(
        show = show,
        title = title,
        summary = summary,
        onDismissRequest = {
            show.value = false
        },
        onDismissFinished = {
            text = value.value.toString()
        }
    ) {
        if (error) {
            Text(
                text = stringResource(R.string.value_error, valueRange.start, valueRange.endInclusive),
                modifier = Modifier.padding(bottom = 4.dp),
                fontSize = 14.sp,
                color = MiuixTheme.colorScheme.error
            )
        }
        TextField(
            value = text,
            singleLine = true,
            onValueChange = { newText ->
                text = newText.filter { it.isDigit() || it == '.' }
            },
            label = "${valueRange.start} .. ${valueRange.endInclusive}",
            borderColor = if (error) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.primary,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.Reject)
                    show.value = false
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = stringResource(R.string.confirm),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    if (!error) {
                        value.value = if (precision > 0) {
                            text.toBigDecimal().setScale(precision, RoundingMode.HALF_UP).toFloat()
                        } else {
                            text.toFloat().roundToInt().toFloat()
                        }
                        text = value.value.toString()
                        show.value = false
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }

    Column(
        modifier = Modifier.clickable(
            indication = indication,
            interactionSource = interactionSource,
            onClick = {
                show.value = true
            }
        )
    ) {
        SuperArrow(
            title = title,
            summary = summary,
            endActions = {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "${value.value}$unit",
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = run {
                        if (enabled) {
                            MiuixTheme.colorScheme.onSurfaceVariantActions
                        } else {
                            MiuixTheme.colorScheme.disabledOnSecondaryVariant
                        }
                    }
                )
            },
            enabled = enabled
        )
        Slider(
            enabled = enabled,
            value = value.value,
            valueRange = valueRange,
            onValueChange = { newValue ->
                val formattedValue = if (precision > 0) {
                    newValue.toBigDecimal().setScale(precision, RoundingMode.HALF_UP).toFloat()
                } else {
                    newValue.roundToInt().toFloat()
                }
                onValueChange(formattedValue)
            },
            onValueChangeFinished = onValueChangeFinished,
            hapticEffect = SliderDefaults.SliderHapticEffect.Step,
            showKeyPoints = true,
            keyPoints = keyPoints,
            magnetThreshold = 0.01f,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        )
    }
}

@Composable
fun SuperEditDialog(
    text: String,
    show: MutableState<Boolean>,
    title: String,
    summary: String? = null,
    onDismissRequest: (() -> Unit)? = null,
    onClickCancel: () -> Unit = {},
    onClickConfirm: () -> Unit = {},
    onTextChanged: (String) -> Unit = {}
) {
    var innerText by remember(text) { mutableStateOf(text) }

    SuperButtonDialog(
        show = show,
        title = title,
        summary = summary,
        onDismissRequest = onDismissRequest,
        onClickCancel = onClickCancel,
        onClickConfirm = {
            onClickConfirm()
            onTextChanged(innerText)
        },
        content = {
            TextField(
                value = innerText,
                singleLine = true,
                onValueChange = {
                    innerText = it
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    )
}

@Composable
fun SuperButtonDialog(
    show: MutableState<Boolean>,
    title: String,
    summary: String? = null,
    showCancel: Boolean = true,
    showConfirm: Boolean = true,
    enableCancel: Boolean = true,
    enableConfirm: Boolean = true,
    cancelTitle: String = stringResource(R.string.cancel),
    confirmTitle: String = stringResource(R.string.confirm),
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    onClickCancel: () -> Unit = {},
    onClickConfirm: () -> Unit = {},
    content: @Composable (() -> Unit) = {}
) {
    val haptic = LocalHapticFeedback.current

    SuperDialog(
        show = show,
        title = title,
        summary = summary,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished
    ) {
        content()
        Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
            if (showCancel) {
                TextButton(
                    enabled = enableCancel,
                    text = cancelTitle,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Reject)
                        onClickCancel()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            if (showCancel && showConfirm)
                Spacer(Modifier.width(20.dp))
            if (showConfirm) {
                TextButton(
                    enabled = enableConfirm,
                    text = confirmTitle,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        onClickConfirm()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    }
}