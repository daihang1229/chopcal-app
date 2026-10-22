package com.chopcal.app.page.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.OnTrackStatus
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.page.progress.component.CaloriesChart
import com.chopcal.app.page.progress.component.GoalCard
import com.chopcal.app.page.progress.component.StepsWaterChart
import com.chopcal.app.page.progress.component.WeightChart
import com.chopcal.app.page.progress.component.WeeklySummaryCard

private val DemoWeightState = WeightChartUi(
    range = WeightRange.SINCE_START,
    points = listOf(
        WeightPoint(19_000, 75.0),
        WeightPoint(19_300, 74.0),
        WeightPoint(19_600, 73.0),
        WeightPoint(19_900, 72.5),
        WeightPoint(20_000, 72.0),
    ),
    targetWeightKg = 65.0,
    projectedDateEpochDay = 20_060,
    daysRemaining = 60,
    onTrack = OnTrackStatus.ON_TRACK,
    hasData = true,
)

private val DemoCaloriesState = CaloriesChartUi(
    range = ShortRange.SEVEN_DAYS,
    bars = listOf(
        CalorieBar(19_994, 1650f, "四", false),
        CalorieBar(19_995, 1780f, "五", false),
        CalorieBar(19_996, 1400f, "六", false),
        CalorieBar(19_997, 1550f, "日", false),
        CalorieBar(19_998, 1720f, "一", false),
        CalorieBar(19_999, 1200f, "二", false),
        CalorieBar(20_000, 900f, "三", true),
    ),
    targetKcal = 1650,
    hasData = true,
)

private val DemoStepsWaterState = StepsWaterChartUi(
    range = ShortRange.SEVEN_DAYS,
    steps = listOf(
        MetricPoint(19_994, 8000f),
        MetricPoint(19_996, 11000f),
        MetricPoint(19_998, 9500f),
        MetricPoint(20_000, 6000f),
    ),
    water = listOf(
        MetricPoint(19_994, 1500f),
        MetricPoint(19_996, 2000f),
        MetricPoint(19_998, 1800f),
        MetricPoint(20_000, 1200f),
    ),
    hasData = true,
)

@Preview(showBackground = true, widthDp = 393, heightDp = 990)
@Composable
private fun ProgressComponentsPreview() {
    ChopCalTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFBF8))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GoalCard(
                data = GoalCardUi(
                    goalType = GoalType.LOSE,
                    initialWeightKg = 75.0,
                    currentWeightKg = 72.0,
                    targetWeightKg = 65.0,
                    changedKg = 3.0,
                    remainingKg = 7.0,
                    projectedDateEpochDay = 20_060,
                    progressFraction = 0.3f,
                ),
                weightUnit = WeightUnit.METRIC,
            )
            WeightChart(
                data = DemoWeightState,
                weightUnit = WeightUnit.METRIC,
                onRangeSelect = {},
                onRecordWeight = {},
            )
            CaloriesChart(data = DemoCaloriesState, onRangeSelect = {})
            StepsWaterChart(data = DemoStepsWaterState, onRangeSelect = {})
            WeeklySummaryCard(
                data = WeeklySummaryUi(
                    avgKcal = 1650,
                    deltaKcal = -80,
                    proteinG = 110,
                    carbsG = 180,
                    fatG = 45,
                    metDays = 4,
                    totalDays = 7,
                    streakDays = 12,
                    hasData = true,
                ),
                onViewAll = {},
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}
