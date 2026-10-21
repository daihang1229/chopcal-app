package com.chopcal.app.page.onboarding.step

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.BigNumberField
import com.chopcal.app.core.design.component.FieldLabel
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.component.StepHeader
import com.chopcal.app.core.design.theme.CardWhite
import com.chopcal.app.core.design.theme.ForestGreen
import com.chopcal.app.core.design.theme.InkMuted
import com.chopcal.app.core.design.theme.Line
import com.chopcal.app.core.design.theme.SurfaceMint
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.page.onboarding.FieldId
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState
import kotlin.math.roundToInt

/**
 * Item 5：目标体重 + 达成时间（仅 lose/gain/muscle）。
 */
@Composable
fun TargetStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    val goal = state.draft.goalType
    val imperial = state.draft.weightUnit == WeightUnit.IMPERIAL
    val targetKg = state.draft.targetWeightKg
    val titleRes = if (goal == GoalType.LOSE) R.string.target_title_lose else R.string.target_title_gain
    val weeks = state.draft.timelineWeeks
    val isCustom = weeks != null && weeks !in listOf(4, 13, 26)

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        StepHeader(
            kicker = stringResource(R.string.target_kicker),
            title = stringResource(titleRes),
            sub = stringResource(R.string.target_sub),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .offset(y = (-40).dp),
            verticalArrangement = Arrangement.Center,
        ) {
            FieldLabel(stringResource(R.string.label_target_weight))
            Spacer(Modifier.height(12.dp))
            BigNumberField(
                value = targetKg?.let { if (imperial) (it / 0.4536).roundToInt().toString() else formatNumber(it) } ?: "",
                onValueChange = { v ->
                    val num = v.toDoubleOrNull()
                    val kg = num?.let { if (imperial) it * 0.4536 else it }
                    onIntent(OnboardingIntent.UpdateTargetWeightKg(kg))
                },
                unit = if (imperial) "lb" else "kg",
                placeholder = if (imperial) "132" else "60",
                decimal = !imperial,
                error = state.fieldErrors[FieldId.TARGET_WEIGHT],
            )

            Spacer(Modifier.height(24.dp))
            FieldLabel(stringResource(R.string.label_timeline))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                TimelineOption(stringResource(R.string.option_1_month), weeks == 4) { onIntent(OnboardingIntent.SelectTimeline(4)) }
                TimelineOption(stringResource(R.string.option_3_months), weeks == 13) { onIntent(OnboardingIntent.SelectTimeline(13)) }
                TimelineOption(stringResource(R.string.option_6_months), weeks == 26) { onIntent(OnboardingIntent.SelectTimeline(26)) }
                TimelineOption(stringResource(R.string.option_custom), isCustom) { onIntent(OnboardingIntent.SelectTimeline(12)) }
            }
            if (isCustom) {
                Spacer(Modifier.height(14.dp))
                BigNumberField(
                    value = weeks?.toString() ?: "",
                    onValueChange = { v -> onIntent(OnboardingIntent.SelectTimeline(v.toIntOrNull())) },
                    unit = stringResource(R.string.unit_weeks),
                    placeholder = "12",
                    error = state.fieldErrors[FieldId.TIMELINE],
                )
            }
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
private fun RowScope.TimelineOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (selected) ForestGreen else InkMuted,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .weight(1f)
            .background(if (selected) SurfaceMint else CardWhite, RoundedCornerShape(10.dp))
            .border(1.dp, if (selected) ForestGreen else Line, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
    )
}

private fun formatNumber(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
