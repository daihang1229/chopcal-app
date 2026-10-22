package com.chopcal.app.page.progress

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.page.progress.component.CaloriesChart
import com.chopcal.app.page.progress.component.GoalCard
import com.chopcal.app.page.progress.component.StepsWaterChart
import com.chopcal.app.page.progress.component.WeightChart
import com.chopcal.app.page.progress.component.WeightInputDialog
import com.chopcal.app.page.progress.component.WeeklySummaryCard

/** 进度页背景色：干净的冷白，区别于暖纸色 paper。 */
private val ProgressBackground = Color(0xFFFAFBF8)

/**
 * My Progress（趋势页）。
 *
 * 自上而下：标题 → 目标卡片 → 体重动态 → 卡路里摄入 → 步数&饮水 → 本周小结。
 */
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onBack: () -> Unit,
    onOpenRecord: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProgressEffect.OpenRecordList -> onOpenRecord()
            }
        }
    }

    var showWeightDialog by remember { mutableStateOf(false) }

    if (showWeightDialog) {
        WeightInputDialog(
            weightUnit = state.weightUnit,
            onConfirm = { kg, dateEpochDay ->
                viewModel.sendIntent(
                    ProgressIntent.RecordWeight(
                        weightKg = kg,
                        dateEpochDay = dateEpochDay,
                    )
                )
                showWeightDialog = false
            },
            onDismiss = { showWeightDialog = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProgressBackground)
            .statusBarsPadding(),
    ) {
        ProgressHeader(
            hasData = state.weight.hasData || state.calories.hasData || state.weekly.hasData,
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GoalCard(
                data = state.goalCard,
                weightUnit = state.weightUnit,
            )

            WeightChart(
                data = state.weight,
                weightUnit = state.weightUnit,
                onRangeSelect = { viewModel.sendIntent(ProgressIntent.SelectWeightRange(it)) },
                onRecordWeight = { showWeightDialog = true },
            )

            CaloriesChart(
                data = state.calories,
                onRangeSelect = { viewModel.sendIntent(ProgressIntent.SelectCaloriesRange(it)) },
            )

            StepsWaterChart(
                data = state.stepsWater,
                onRangeSelect = { viewModel.sendIntent(ProgressIntent.SelectStepsWaterRange(it)) },
            )

            WeeklySummaryCard(
                data = state.weekly,
                onViewAll = { viewModel.sendIntent(ProgressIntent.OpenRecordList) },
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProgressHeader(hasData: Boolean, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(chopCalColors.surfaceMuted)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.common_back),
                tint = chopCalColors.forestGreen,
                modifier = Modifier.size(16.dp),
            )
        }

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = stringResource(R.string.progress_title),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
            )
            Text(
                text = stringResource(
                    if (hasData) R.string.progress_subtitle_filled else R.string.progress_subtitle_empty
                ),
                fontSize = 9.sp,
                color = chopCalColors.inkMuted,
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(chopCalColors.surfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                contentDescription = null,
                tint = chopCalColors.forestGreen,
                modifier = Modifier.size(17.dp),
            )
        }
    }
}
