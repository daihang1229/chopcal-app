package com.chopcal.app.page.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.CaptureMealTarget
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.InputMethod
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.page.home.CaptureStage
import com.chopcal.app.page.home.HomeIntent
import com.chopcal.app.page.home.HomeUiState

/**
 * 拍摄底部弹窗（核心录入）：餐次 + 录入方式 → 识别 → 候选 → 分量/做法 → 结果。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureSheet(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = chopCalColors.screen,
    ) {
        when (state.captureStage) {
            CaptureStage.INPUT -> InputStage(state, onIntent)
            CaptureStage.RECOGNIZING -> RecognizingStage()
            CaptureStage.CANDIDATES -> CandidatesStage(state, onIntent)
            CaptureStage.ADJUST -> AdjustStage(state, onIntent)
            CaptureStage.RESULT -> ResultStage(state, onIntent)
        }
    }
}

// ── 输入阶段 ──────────────────────────────────────────────────

@Composable
private fun InputStage(state: HomeUiState, onIntent: (HomeIntent) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.capture_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = chopCalColors.ink,
        )

        // 餐次 tab
        Row(
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MealType.entries.forEach { mealType ->
                MealTab(
                    label = stringResource(mealType.labelRes),
                    selected = state.captureTarget == CaptureMealTarget.Meal(mealType),
                    onClick = { onIntent(HomeIntent.SelectMealTarget(CaptureMealTarget.Meal(mealType))) },
                    modifier = Modifier.weight(1f),
                )
            }
            MealTab(
                label = stringResource(R.string.capture_query_only),
                selected = state.captureTarget == CaptureMealTarget.QueryOnly,
                onClick = { onIntent(HomeIntent.SelectMealTarget(CaptureMealTarget.QueryOnly)) },
                modifier = Modifier.weight(1f),
            )
        }

        HorizontalDivider(color = chopCalColors.line, modifier = Modifier.padding(vertical = 16.dp))

        // 录入方式（纵向单列）
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            InputMethodRow(
                icon = "📷",
                label = stringResource(R.string.input_camera),
                enabled = true,
                onClick = { onIntent(HomeIntent.StartCapture(null)) },
            )
            InputMethodRow(
                icon = "🖼",
                label = stringResource(R.string.input_album),
                enabled = true,
                onClick = { onIntent(HomeIntent.StartCapture(null)) },
            )
            InputMethodRow(
                icon = "▦",
                label = stringResource(R.string.input_barcode) + "  " + stringResource(R.string.phase_two),
                enabled = false,
                onClick = {},
            )
            InputMethodRow(
                icon = "⌨️",
                label = stringResource(R.string.input_text_voice) + "  " + stringResource(R.string.phase_two),
                enabled = false,
                onClick = {},
            )
        }
    }
}

@Composable
private fun MealTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = if (selected) chopCalColors.surfaceMint else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) chopCalColors.forestGreen else chopCalColors.inkMuted,
        )
    }
}

@Composable
private fun InputMethodRow(
    icon: String,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (enabled) chopCalColors.card else Color.Transparent,
                shape = RoundedCornerShape(14.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (enabled) FontWeight.Medium else FontWeight.Normal,
            color = if (enabled) chopCalColors.ink else chopCalColors.inkFaint,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

// ── 识别中 ────────────────────────────────────────────────────

@Composable
private fun RecognizingStage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = chopCalColors.forestGreen)
        Text(
            text = stringResource(R.string.recognizing_text),
            fontSize = 13.sp,
            color = chopCalColors.inkMuted,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

// ── 候选阶段 ──────────────────────────────────────────────────

@Composable
private fun CandidatesStage(state: HomeUiState, onIntent: (HomeIntent) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.candidates_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = chopCalColors.ink,
        )
        Column(
            modifier = Modifier.padding(top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.candidates.forEachIndexed { index, candidate ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(chopCalColors.card, RoundedCornerShape(14.dp))
                        .clickable { onIntent(HomeIntent.SelectCandidate(index)) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = candidate.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = chopCalColors.ink,
                        )
                        Text(
                            text = "${candidate.baseKcal} kcal / 份",
                            fontSize = 10.sp,
                            color = chopCalColors.inkMuted,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Text(
                        text = "${candidate.confidence}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = chopCalColors.forestGreen,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onIntent(HomeIntent.NotThisDish) }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.not_this_dish),
                fontSize = 12.sp,
                color = chopCalColors.inkMuted,
            )
        }
    }
}

// ── 分量 / 做法 ───────────────────────────────────────────────

@Composable
private fun AdjustStage(state: HomeUiState, onIntent: (HomeIntent) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = chopCalColors.inkMuted,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onIntent(HomeIntent.BackToCandidates) },
            )
            Text(
                text = state.selectedCandidate?.name.orEmpty(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        SectionLabel(stringResource(R.string.label_portion))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Portion.entries.forEach { portion ->
                ChoiceChip(
                    label = stringResource(portion.labelRes),
                    selected = state.portion == portion,
                    onClick = { onIntent(HomeIntent.SelectPortion(portion)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SectionLabel(stringResource(R.string.label_method))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CookingMethod.entries.forEach { method ->
                ChoiceChip(
                    label = stringResource(method.labelRes),
                    selected = state.cookingMethod == method,
                    onClick = { onIntent(HomeIntent.SelectCookingMethod(method)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SectionLabel(stringResource(R.string.label_servings))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StepperButton("−") { onIntent(HomeIntent.UpdateServings(state.servings - 1)) }
            Text(
                text = "${state.servings}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            StepperButton("+") { onIntent(HomeIntent.UpdateServings(state.servings + 1)) }
        }

        com.chopcal.app.core.design.component.PrimaryButton(
            text = stringResource(R.string.capture_compute),
            onClick = { onIntent(HomeIntent.ComputeResult) },
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = chopCalColors.inkMuted,
        modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
    )
}

@Composable
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = if (selected) chopCalColors.surfaceMint else chopCalColors.card,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) chopCalColors.forestGreen else chopCalColors.inkMuted,
        )
    }
}

@Composable
private fun StepperButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(chopCalColors.surfaceMuted, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, fontSize = 18.sp, color = chopCalColors.ink)
    }
}

// ── 结果阶段 ──────────────────────────────────────────────────

@Composable
private fun ResultStage(state: HomeUiState, onIntent: (HomeIntent) -> Unit) {
    val result = state.calorieResult ?: return
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.result_title_home),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = chopCalColors.ink,
        )
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 10.dp)) {
            Text(
                text = "${result.kcalMin}–${result.kcalMax}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.forestGreen,
            )
            Text(
                text = " kcal · ${result.dishName}",
                fontSize = 11.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
            )
        }

        // 宏量拆解
        Row(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ResultMacro(stringResource(R.string.home_protein), "${result.proteinG}g")
            ResultMacro(stringResource(R.string.home_carbs), "${result.carbsG}g")
            ResultMacro(stringResource(R.string.home_fat), "${result.fatG}g")
        }

        // 食材明细
        if (result.ingredients.isNotEmpty()) {
            Text(
                text = stringResource(R.string.result_ingredients),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
            )
            result.ingredients.forEach { line ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = line.name,
                        fontSize = 12.sp,
                        color = chopCalColors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${line.grams}g · ${line.kcal} kcal",
                        fontSize = 11.sp,
                        color = chopCalColors.inkMuted,
                    )
                }
            }
        }

        // 可解释性
        Text(
            text = result.explanation,
            fontSize = 10.sp,
            color = chopCalColors.inkFaint,
            modifier = Modifier.padding(top = 12.dp),
        )

        val queryOnly = state.captureTarget == CaptureMealTarget.QueryOnly
        com.chopcal.app.core.design.component.PrimaryButton(
            text = stringResource(if (queryOnly) R.string.result_got_it else R.string.result_save),
            onClick = {
                if (queryOnly) onIntent(HomeIntent.DismissResult) else onIntent(HomeIntent.SaveResult)
            },
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun ResultMacro(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = chopCalColors.ink)
        Text(text = label, fontSize = 10.sp, color = chopCalColors.inkMuted)
    }
}
