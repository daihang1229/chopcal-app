package com.chopcal.app.page.progress.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.page.progress.MetricPoint
import com.chopcal.app.page.progress.ShortRange
import com.chopcal.app.page.progress.StepsWaterChartUi

private val StepsColor = Color(0xFF78985D)
private val WaterColor = Color(0xFF79AEB3)

/**
 * 步数 & 饮水卡片（双折线图 + 图例）。
 */
@Composable
fun StepsWaterChart(
    data: StepsWaterChartUi,
    onRangeSelect: (ShortRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProgressCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.progress_steps_water_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = chopCalColors.ink,
                )
                Text(
                    text = stringResource(
                        if (data.hasData) R.string.progress_steps_water_sub else R.string.progress_steps_water_sub_empty
                    ),
                    fontSize = 9.sp,
                    color = chopCalColors.inkMuted,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            TimeRangeSelector(
                options = listOf(
                    stringResource(R.string.progress_range_7d),
                    stringResource(R.string.progress_range_1m),
                ),
                selectedIndex = if (data.range == ShortRange.SEVEN_DAYS) 0 else 1,
                onSelect = { onRangeSelect(if (it == 0) ShortRange.SEVEN_DAYS else ShortRange.ONE_MONTH) },
            )
        }

        Spacer(Modifier.height(12.dp))

        if (data.hasData) {
            DualLineChart(
                steps = data.steps,
                water = data.water,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Legend(color = StepsColor, label = stringResource(R.string.progress_legend_steps))
                Legend(color = WaterColor, label = stringResource(R.string.progress_legend_water))
            }
        } else {
            StepsWaterEmptyState()
        }
    }
}

@Composable
private fun Legend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = chopCalColors.inkMuted,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun StepsWaterEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(chopCalColors.surfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.progress_steps_water_empty_hint),
            fontSize = 9.sp,
            color = chopCalColors.inkFaint,
        )
    }
}

@Composable
private fun DualLineChart(
    steps: List<MetricPoint>,
    water: List<MetricPoint>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val leftPad = 6.dp.toPx()
        val rightPad = 6.dp.toPx()
        val topPad = 8.dp.toPx()
        val bottomPad = 8.dp.toPx()
        val plotWidth = size.width - leftPad - rightPad
        val plotHeight = size.height - topPad - bottomPad

        val allValues = (steps.map { it.value } + water.map { it.value })
        val yMax = (allValues.maxOrNull() ?: 0f).coerceAtLeast(1f) * 1.15f

        val allDays = (steps.map { it.epochDay } + water.map { it.epochDay })
        val xMin = (allDays.minOrNull() ?: 0L).toFloat()
        val xMax = (allDays.maxOrNull() ?: 1L).toFloat()
        val xSpan = (xMax - xMin).coerceAtLeast(1f)

        fun xFor(epochDay: Long) = leftPad + ((epochDay - xMin) / xSpan) * plotWidth
        fun yFor(value: Float) = topPad + (1f - value / yMax) * plotHeight

        drawLineSeries(steps, ::xFor, ::yFor, StepsColor)
        drawLineSeries(water, ::xFor, ::yFor, WaterColor)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLineSeries(
    points: List<MetricPoint>,
    xFor: (Long) -> Float,
    yFor: (Float) -> Float,
    color: Color,
) {
    if (points.isEmpty()) return
    val path = Path()
    points.forEachIndexed { index, p ->
        val x = xFor(p.epochDay)
        val y = yFor(p.value)
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(path, color, style = Stroke(width = 4f))
    points.forEach { p ->
        drawCircle(color, radius = 4f, center = Offset(xFor(p.epochDay), yFor(p.value)))
    }
}
