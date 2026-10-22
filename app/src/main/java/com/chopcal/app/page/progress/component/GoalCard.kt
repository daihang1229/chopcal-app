package com.chopcal.app.page.progress.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.page.progress.GoalCardUi
import com.chopcal.app.page.progress.formatMonthDay
import com.chopcal.app.page.progress.formatWeight
import com.chopcal.app.page.progress.weightUnitLabel

private val GoalCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF618A37), Color(0xFF4E6E2C)),
)

/**
 * 目标卡片（减脂 / 增重 / 增肌 / 保持差异化）。
 */
@Composable
fun GoalCard(
    data: GoalCardUi,
    weightUnit: WeightUnit,
    modifier: Modifier = Modifier,
) {
    val unit = weightUnitLabel(weightUnit)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(21.dp))
            .background(GoalCardGradient)
            .padding(horizontal = 18.dp, vertical = 17.dp),
    ) {
        Column {
            // 目标徽标
            Text(
                text = stringResource(goalBadgeRes(data.goalType)),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE6F0E1),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x21FFFFFF))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )

            // 初始 → 当前 → 目标
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WeightSegment(
                    value = formatWeight(data.initialWeightKg, weightUnit),
                    unit = unit,
                    valueColor = Color.White,
                    unitColor = Color(0xFFB9C9BA),
                )
                SegmentArrow(color = Color(0xFFA9BEA7))
                if (data.targetWeightKg != null) {
                    WeightSegment(
                        value = formatWeight(data.currentWeightKg, weightUnit),
                        unit = unit,
                        valueColor = chopCalColors.lime,
                        unitColor = Color(0xFFC4D4C3),
                    )
                    SegmentArrow(color = Color(0xFFA9BEA7))
                    WeightSegment(
                        value = formatWeight(data.targetWeightKg, weightUnit),
                        unit = unit,
                        valueColor = Color.White,
                        unitColor = Color(0xFFB9C9BA),
                    )
                } else {
                    WeightSegment(
                        value = formatWeight(data.currentWeightKg, weightUnit),
                        unit = unit,
                        valueColor = Color.White,
                        unitColor = Color(0xFFB9C9BA),
                    )
                }
            }

            // 已减/已增 + 还剩
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = changeText(data, weightUnit, unit),
                    fontSize = 11.sp,
                    color = Color(0xFFDFE8DB),
                )
                if (data.targetWeightKg != null) {
                    if (data.projectedDateEpochDay != null) {
                        Text(
                            text = stringResource(
                                R.string.progress_reach_by,
                                formatMonthDay(data.projectedDateEpochDay),
                            ),
                            fontSize = 9.sp,
                            color = Color(0xFFC2D0C2),
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.progress_reach_later),
                            fontSize = 9.sp,
                            color = Color(0xFFC2D0C2),
                        )
                    }
                } else if (data.fluctuationKg != null) {
                    Text(
                        text = stringResource(
                            R.string.progress_fluctuation,
                            formatWeight(data.fluctuationKg, weightUnit),
                        ),
                        fontSize = 9.sp,
                        color = Color(0xFFC2D0C2),
                    )
                }
            }

            // 进度条
            if (data.targetWeightKg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(Color(0x2BFFFFFF)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(data.progressFraction.coerceIn(0f, 1f))
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(chopCalColors.lime),
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightSegment(value: String, unit: String, valueColor: Color, unitColor: Color) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(text = value, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        Text(
            text = unit,
            fontSize = 10.sp,
            color = unitColor,
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
        )
    }
}

@Composable
private fun SegmentArrow(color: Color) {
    Text(
        text = "→",
        fontSize = 14.sp,
        color = color,
        modifier = Modifier.padding(horizontal = 6.dp),
    )
}

private fun goalBadgeRes(type: GoalType): Int = when (type) {
    GoalType.LOSE -> R.string.progress_badge_lose
    GoalType.MAINTAIN -> R.string.progress_badge_maintain
    GoalType.GAIN -> R.string.progress_badge_gain
    GoalType.MUSCLE -> R.string.progress_badge_muscle
}

@Composable
private fun changeText(data: GoalCardUi, unit: WeightUnit, unitLabel: String): String = when (data.goalType) {
    GoalType.LOSE -> stringResource(
        R.string.progress_lost_fmt,
        formatWeight(data.changedKg, unit),
        formatWeight(data.remainingKg, unit),
        unitLabel,
    )
    GoalType.MAINTAIN -> stringResource(
        R.string.progress_maintain_fmt,
        formatWeight(data.currentWeightKg, unit),
        unitLabel,
    )
    GoalType.GAIN, GoalType.MUSCLE -> stringResource(
        R.string.progress_gained_fmt,
        formatWeight(data.changedKg, unit),
        formatWeight(data.remainingKg, unit),
        unitLabel,
    )
}
