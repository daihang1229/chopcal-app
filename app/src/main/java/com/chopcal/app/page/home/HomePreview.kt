package com.chopcal.app.page.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.MacroTargets
import com.chopcal.app.domain.model.MealSummary
import com.chopcal.app.domain.model.MealType
import java.time.LocalDate

private fun previewState(): HomeUiState {
    val today = LocalDate.now().toEpochDay()
    return HomeUiState(
        loading = false,
        selectedDateEpochDay = today,
        todayEpochDay = today,
        week = (0L until 7L).map { i ->
            DayItem(
                epochDay = today - 3 + i,
                weekdayLabel = listOf("一", "二", "三", "四", "五", "六", "日")[i.toInt()],
                dayOfMonth = (18 + i).toInt(),
                isToday = i == 3L,
                isFuture = i > 3L,
                isSelected = i == 3L,
            )
        },
        dailyTarget = DailyTarget(
            dailyKcal = 1650,
            proteinG = 110,
            projectedDateEpochDay = today + 84,
            floorApplied = false,
            bmr = 1450,
            tdee = 1990,
            adjustment = -340,
            activityFactor = 1.375,
        ),
        goalType = GoalType.LOSE,
        nickname = "Chop",
        macroTargets = MacroTargets(proteinG = 110, carbsG = 206, fatG = 46),
        summary = DailySummary(
            dateEpochDay = today,
            totalKcal = 850,
            proteinG = 42,
            carbsG = 96,
            fatG = 28,
            perMeal = mapOf(
                MealType.BREAKFAST to MealSummary(kcal = 320),
                MealType.LUNCH to MealSummary(kcal = 530),
            ),
            waterMl = 850,
            stepCount = 3280,
        ),
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomeScreenPreview() {
    ChopCalTheme(darkTheme = false) {
        HomeContent(
            state = previewState(),
            onIntent = {},
            onOpenProfile = {},
            onOpenPro = {},
            onOpenRecord = {},
            onWaterClick = {},
            onStepClick = {},
        )
    }
}
