package com.chopcal.app.page.onboarding.step

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.BigNumberField
import com.chopcal.app.core.design.component.FieldLabel
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.component.StepHeader
import com.chopcal.app.core.design.theme.ForestGreen
import com.chopcal.app.core.design.theme.InkFaint
import com.chopcal.app.domain.model.LengthUnit
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.page.onboarding.FieldId
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState
import kotlin.math.roundToInt

/**
 * Item 3：身高 + 当前体重（含单位切换）。
 * 内部始终以 cm/kg 存储，仅展示时换算。
 */
@Composable
fun BodyDataStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    val imperial = state.draft.lengthUnit == LengthUnit.IMPERIAL
    val heightCm = state.draft.heightCm
    val weightKg = state.draft.weightKg

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                StepHeader(kicker = stringResource(R.string.body_kicker), title = stringResource(R.string.body_title))
            }
            UnitToggle(
                imperial = imperial,
                onClick = { onIntent(OnboardingIntent.ToggleUnit) },
            )
        }
        Spacer(Modifier.height(9.dp))
        Text(text = stringResource(R.string.body_sub), fontSize = 13.sp, color = InkFaint)

        Column(
            modifier = Modifier
                .weight(1f)
                .offset(y = (-40).dp),
            verticalArrangement = Arrangement.Center,
        ) {
            FieldLabel(stringResource(R.string.label_height))
            Spacer(Modifier.height(12.dp))
            if (imperial) {
                val (ft, inch) = cmToFtIn(heightCm)
                Row {
                    BigNumberField(
                        value = ft.takeIf { heightCm != null }?.toString() ?: "",
                        onValueChange = { v ->
                            onIntent(OnboardingIntent.UpdateHeightCm(ftInToCm(v.toIntOrNull() ?: 0, inch)))
                        },
                        unit = "ft",
                        placeholder = "5",
                        modifier = Modifier.weight(1f),
                        error = state.fieldErrors[FieldId.HEIGHT],
                    )
                    Spacer(Modifier.padding(horizontal = 12.dp))
                    BigNumberField(
                        value = inch.takeIf { heightCm != null }?.toString() ?: "",
                        onValueChange = { v ->
                            onIntent(OnboardingIntent.UpdateHeightCm(ftInToCm(ft, v.toIntOrNull() ?: 0)))
                        },
                        unit = "in",
                        placeholder = "7",
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                BigNumberField(
                    value = heightCm?.let { formatNumber(it) } ?: "",
                    onValueChange = { v -> onIntent(OnboardingIntent.UpdateHeightCm(v.toDoubleOrNull())) },
                    unit = "cm",
                    placeholder = "170",
                    error = state.fieldErrors[FieldId.HEIGHT],
                )
            }

            Spacer(Modifier.height(30.dp))
            FieldLabel(stringResource(R.string.label_weight))
            Spacer(Modifier.height(12.dp))
            if (imperial) {
                BigNumberField(
                    value = weightKg?.let { kgToLb(it).roundToInt().toString() } ?: "",
                    onValueChange = { v ->
                        val lb = v.toDoubleOrNull()
                        onIntent(OnboardingIntent.UpdateWeightKg(lb?.let { lbToKg(it) }))
                    },
                    unit = "lb",
                    placeholder = "143",
                    decimal = true,
                    error = state.fieldErrors[FieldId.WEIGHT],
                )
            } else {
                BigNumberField(
                    value = weightKg?.let { formatNumber(it) } ?: "",
                    onValueChange = { v -> onIntent(OnboardingIntent.UpdateWeightKg(v.toDoubleOrNull())) },
                    unit = "kg",
                    placeholder = "65",
                    decimal = true,
                    error = state.fieldErrors[FieldId.WEIGHT],
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(text = stringResource(R.string.hint_update_profile), fontSize = 11.sp, color = InkFaint)
        }
        StepActions(
            primaryText = stringResource(R.string.common_continue),
            primaryEnabled = state.canProceed,
            onPrimary = { onIntent(OnboardingIntent.Next) },
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun UnitToggle(imperial: Boolean, onClick: () -> Unit) {
    Text(
        text = if (imperial) stringResource(R.string.toggle_imperial) else stringResource(R.string.toggle_metric),
        fontSize = 11.sp,
        color = ForestGreen,
        modifier = Modifier
            .background(ForestGreen.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
    )
}

private fun cmToFtIn(cm: Double?): Pair<Int, Int> {
    if (cm == null) return 0 to 0
    val totalIn = (cm / 2.54).roundToInt()
    return (totalIn / 12) to (totalIn % 12)
}

private fun ftInToCm(ft: Int, inch: Int): Double = (ft * 12 + inch) * 2.54
private fun kgToLb(kg: Double): Double = kg / 0.4536
private fun lbToKg(lb: Double): Double = lb * 0.4536

private fun formatNumber(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
