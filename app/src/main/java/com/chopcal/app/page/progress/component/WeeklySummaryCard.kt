package com.chopcal.app.page.progress.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.page.progress.WeeklySummaryUi

/**
 * 本周小结卡片（日均摄入 + 达标 + 宏量 + 连续记录）。
 */
@Composable
fun WeeklySummaryCard(
    data: WeeklySummaryUi,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ProgressCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.progress_weekly_title),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
            )
            Text(
                text = stringResource(R.string.progress_weekly_badge),
                fontSize = 9.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(chopCalColors.surfaceMuted)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }

        Spacer(Modifier.height(14.dp))

        if (data.hasData) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 日均摄入
                Column {
                    Text(
                        text = stringResource(R.string.progress_weekly_avg),
                        fontSize = 9.sp,
                        color = chopCalColors.inkMuted,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = data.avgKcal.toString(),
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = chopCalColors.ink,
                        )
                        Text(
                            text = stringResource(R.string.progress_kcal_unit),
                            fontSize = 9.sp,
                            color = chopCalColors.inkMuted,
                            modifier = Modifier.padding(start = 3.dp, bottom = 3.dp),
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                // 较上周变化
                if (data.deltaKcal != 0) {
                    Text(
                        text = stringResource(R.string.progress_vs_last, data.deltaKcal),
                        fontSize = 9.sp,
                        color = if (data.deltaKcal < 0) chopCalColors.forestGreen else chopCalColors.warning,
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(chopCalColors.surfaceMint)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .padding(horizontal = 6.dp),
                    color = chopCalColors.line,
                )

                Spacer(Modifier.weight(1f))

                // 达标天数
                Column {
                    Text(
                        text = stringResource(R.string.progress_weekly_met),
                        fontSize = 9.sp,
                        color = chopCalColors.inkMuted,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = data.metDays.toString(),
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = chopCalColors.ink,
                        )
                        Text(
                            text = stringResource(R.string.progress_weekly_days, data.totalDays),
                            fontSize = 9.sp,
                            color = chopCalColors.inkMuted,
                            modifier = Modifier.padding(start = 3.dp, bottom = 3.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(color = chopCalColors.line)

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                MacroCell(
                    label = stringResource(R.string.progress_protein),
                    value = data.proteinG,
                    unit = "g",
                    modifier = Modifier.weight(1f),
                )
                MacroCell(
                    label = stringResource(R.string.progress_carbs),
                    value = data.carbsG,
                    unit = "g",
                    modifier = Modifier.weight(1f),
                )
                MacroCell(
                    label = stringResource(R.string.progress_fat),
                    value = data.fatG,
                    unit = "g",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(color = chopCalColors.line)

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onViewAll),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.progress_streak_fmt, data.streakDays),
                    fontSize = 9.sp,
                    color = chopCalColors.inkMuted,
                )
                Text(
                    text = stringResource(R.string.progress_view_all),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = chopCalColors.forestGreen,
                )
            }
        } else {
            WeeklySummaryEmptyState()
        }
    }
}

@Composable
private fun MacroCell(label: String, value: Int, unit: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 9.sp, color = chopCalColors.inkMuted)
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 2.dp)) {
            Text(
                text = value.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
            )
            Text(
                text = unit,
                fontSize = 8.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier.padding(start = 2.dp, bottom = 1.dp),
            )
        }
    }
}

@Composable
private fun WeeklySummaryEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(chopCalColors.surfaceMuted)
            .padding(16.dp),
    ) {
        Column {
            Text(
                text = stringResource(R.string.progress_weekly_empty),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.inkMuted,
            )
            Text(
                text = stringResource(R.string.progress_weekly_empty_sub),
                fontSize = 9.sp,
                color = chopCalColors.inkFaint,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
