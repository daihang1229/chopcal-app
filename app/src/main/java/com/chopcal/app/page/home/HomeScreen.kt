package com.chopcal.app.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chopcal.app.R
import com.chopcal.app.core.design.component.ChopLoadingIndicator
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.page.home.component.CalorieRing
import com.chopcal.app.page.home.component.CaptureSheet
import com.chopcal.app.page.home.component.HomeTopBar
import com.chopcal.app.page.home.component.MacroSection
import com.chopcal.app.page.home.component.MealSection
import com.chopcal.app.page.home.component.WaterStepSection
import com.chopcal.app.page.home.component.WeekSelector
import com.chopcal.app.page.home.component.WheelPickerSheet

private val WaterValues = (0..5000 step 50).toList()
private val StepValues = (0..50000 step 500).toList()

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenProfile: () -> Unit,
    onOpenPro: () -> Unit,
    onOpenRecord: () -> Unit,
    onOpenProgress: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 水 / 步数滚轮选择器本地状态
    var showWaterPicker by remember { mutableStateOf(false) }
    var showStepPicker by remember { mutableStateOf(false) }

    if (state.loading) {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            ChopLoadingIndicator()
        }
        return
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingAddButton(onClick = { viewModel.sendIntent(HomeIntent.OpenCapture) })
        },
    ) { padding ->
        HomeContent(
            state = state,
            onIntent = viewModel::sendIntent,
            onOpenProfile = onOpenProfile,
            onOpenPro = onOpenPro,
            onOpenRecord = onOpenRecord,
            onOpenProgress = onOpenProgress,
            onWaterClick = { showWaterPicker = true },
            onStepClick = { showStepPicker = true },
            modifier = Modifier.padding(padding),
        )
    }

    if (state.captureVisible) {
        CaptureSheet(
            state = state,
            onIntent = viewModel::sendIntent,
            onDismiss = { viewModel.sendIntent(HomeIntent.DismissCapture) },
        )
    }

    if (showWaterPicker) {
        WheelPickerSheet(
            title = stringResource(R.string.home_water),
            unit = stringResource(R.string.home_water_unit),
            values = WaterValues,
            initialValue = state.summary.waterMl,
            confirmText = stringResource(R.string.common_confirm),
            onConfirm = {
                viewModel.sendIntent(HomeIntent.SetWaterMl(it))
                showWaterPicker = false
            },
            onDismiss = { showWaterPicker = false },
        )
    }

    if (showStepPicker) {
        WheelPickerSheet(
            title = stringResource(R.string.home_step),
            unit = stringResource(R.string.home_step_unit),
            values = StepValues,
            initialValue = state.summary.stepCount,
            confirmText = stringResource(R.string.common_confirm),
            onConfirm = {
                viewModel.sendIntent(HomeIntent.SetStepCount(it))
                showStepPicker = false
            },
            onDismiss = { showStepPicker = false },
        )
    }
}

@Composable
internal fun HomeContent(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenPro: () -> Unit,
    onOpenRecord: () -> Unit,
    onOpenProgress: () -> Unit,
    onWaterClick: () -> Unit,
    onStepClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        HomeTopBar(
            nickname = state.nickname,
            onOpenProfile = onOpenProfile,
            onOpenPro = onOpenPro,
            onOpenRecord = onOpenRecord,
            onOpenProgress = onOpenProgress,
        )

        // 问候语
        Column(Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
            Text(
                text = stringResource(R.string.home_greeting_title),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
            )
            Text(
                text = stringResource(R.string.home_greeting_sub),
                fontSize = 9.5.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        WeekSelector(
            days = state.week,
            onSelect = { day -> onIntent(HomeIntent.SelectDate(day.epochDay)) },
        )

        // 环形进度卡
        CalorieCard(
            state = state,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )

        // 宏量
        SectionHeader(
            title = stringResource(R.string.home_nutrition),
            trailing = stringResource(R.string.home_today_target),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )
        MacroSection(
            summary = state.summary,
            targets = state.macroTargets,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        // 水 / 步数
        WaterStepSection(
            waterMl = state.summary.waterMl,
            stepCount = state.summary.stepCount,
            onWaterClick = onWaterClick,
            onStepClick = onStepClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )

        // 四餐
        SectionHeader(
            title = stringResource(R.string.home_meals),
            trailing = stringResource(R.string.home_view_records) + "  ›",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
        )
        MealSection(
            meals = state.summary.perMeal,
            onMealClick = { mealType -> onIntent(HomeIntent.OpenCaptureForMeal(mealType)) },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )

        Spacer(Modifier.height(96.dp)) // 给悬浮按钮留出空间
    }
}

@Composable
private fun CalorieCard(state: HomeUiState, modifier: Modifier = Modifier) {
    val target = state.dailyTarget?.dailyKcal ?: 0
    val current = state.summary.totalKcal
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(chopCalColors.card, RoundedCornerShape(24.dp))
            .border(1.dp, chopCalColors.line, RoundedCornerShape(24.dp))
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.home_daily_intake),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = chopCalColors.inkMuted,
        )
        CalorieRing(
            currentKcal = current,
            targetKcal = target,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            text = remainingHint(state, current, target),
            fontSize = 10.sp,
            color = chopCalColors.inkMuted,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun remainingHint(state: HomeUiState, current: Int, target: Int): String {
    val remaining = (target - current).coerceAtLeast(0)
    return when (state.goalType) {
        GoalType.LOSE -> stringResource(R.string.home_ring_lose, remaining)
        GoalType.MAINTAIN -> stringResource(R.string.home_ring_maintain, remaining)
        GoalType.GAIN, GoalType.MUSCLE -> stringResource(R.string.home_ring_gain, remaining)
        null -> stringResource(R.string.home_ring_maintain, remaining)
    }
}

@Composable
private fun SectionHeader(title: String, trailing: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = chopCalColors.ink,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = trailing,
            fontSize = 8.5.sp,
            color = chopCalColors.inkFaint,
        )
    }
}

@Composable
private fun FloatingAddButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(chopCalColors.forestGreen, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = stringResource(R.string.home_capture_fab),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}
