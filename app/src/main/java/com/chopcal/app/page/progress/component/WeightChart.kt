package com.chopcal.app.page.progress.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.OnTrackStatus
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.page.progress.WeightChartUi
import com.chopcal.app.page.progress.WeightPoint
import com.chopcal.app.page.progress.WeightRange
import com.chopcal.app.page.progress.formatShortDate
import com.chopcal.app.page.progress.formatWeight
import com.chopcal.app.page.progress.weightUnitLabel

private val TargetLineColor = Color(0xFFB0C39E)
private val ProjectionLineColor = Color(0xFFD0A56B)

/**
 * 体重动态卡片（折线 + 目标虚线 + 达成日竖线 + 在轨道状态）。
 */
@Composable
fun WeightChart(
    data: WeightChartUi,
    weightUnit: WeightUnit,
    onRangeSelect: (WeightRange) -> Unit,
    onRecordWeight: () -> Unit,
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
                    text = stringResource(R.string.progress_weight_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = chopCalColors.ink,
                )
                Text(
                    text = stringResource(
                        if (data.hasData) R.string.progress_weight_sub else R.string.progress_weight_sub_empty
                    ),
                    fontSize = 9.sp,
                    color = chopCalColors.inkMuted,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            TimeRangeSelector(
                options = listOf(
                    stringResource(R.string.progress_range_since_start),
                    stringResource(R.string.progress_range_1m),
                    stringResource(R.string.progress_range_7d),
                ),
                selectedIndex = data.range.ordinal,
                onSelect = { onRangeSelect(WeightRange.entries[it]) },
            )
        }

        Spacer(Modifier.height(14.dp))

        if (data.hasData) {
            WeightLineChart(
                points = data.points,
                targetWeightKg = data.targetWeightKg,
                projectedDateEpochDay = data.projectedDateEpochDay,
                unit = weightUnit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OnTrackStatusLabel(
                    status = data.onTrack,
                    daysRemaining = data.daysRemaining,
                    lateWeeks = data.lateWeeks,
                    modifier = Modifier.weight(1f),
                )
                RecordWeightButton(onClick = onRecordWeight, emphasized = true)
            }
        } else {
            WeightEmptyState(onRecordWeight = onRecordWeight)
        }
    }
}

@Composable
private fun OnTrackStatusLabel(
    status: OnTrackStatus,
    daysRemaining: Long?,
    lateWeeks: Int,
    modifier: Modifier = Modifier,
) {
    val text = when (status) {
        OnTrackStatus.ON_TRACK -> stringResource(
            R.string.progress_on_track_fmt,
            daysRemaining ?: 0,
        )
        OnTrackStatus.BEHIND -> stringResource(R.string.progress_behind_fmt, lateWeeks)
        OnTrackStatus.AHEAD -> stringResource(R.string.progress_ahead)
        OnTrackStatus.INSUFFICIENT -> stringResource(R.string.progress_insufficient)
    }
    Text(
        text = text,
        fontSize = 10.sp,
        color = if (status == OnTrackStatus.BEHIND) chopCalColors.warning else chopCalColors.inkMuted,
        modifier = modifier,
    )
}

@Composable
private fun WeightEmptyState(onRecordWeight: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(chopCalColors.surfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.progress_no_weight_yet),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.inkMuted,
            )
            Text(
                text = stringResource(R.string.progress_first_weight_hint),
                fontSize = 9.sp,
                color = chopCalColors.inkFaint,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
    Spacer(Modifier.height(10.dp))
    RecordWeightButton(onClick = onRecordWeight, emphasized = true)
}

@Composable
internal fun RecordWeightButton(onClick: () -> Unit, emphasized: Boolean) {
    Text(
        text = stringResource(R.string.progress_record_weight),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (emphasized) Color.White else chopCalColors.forestGreen,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (emphasized) Color(0xFF244F3C) else chopCalColors.surfaceMint)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}

@Composable
private fun WeightLineChart(
    points: List<WeightPoint>,
    targetWeightKg: Double?,
    projectedDateEpochDay: Long?,
    unit: WeightUnit,
    modifier: Modifier = Modifier,
) {
    val lineColor = chopCalColors.forestGreen
    Canvas(modifier = modifier) {
        val leftPad = 34.dp.toPx()
        val rightPad = 12.dp.toPx()
        val topPad = 14.dp.toPx()
        val bottomPad = 20.dp.toPx()
        val plotWidth = size.width - leftPad - rightPad
        val plotHeight = size.height - topPad - bottomPad

        val weights = points.map { it.weightKg } + listOfNotNull(targetWeightKg)
        val yMin = (weights.minOrNull() ?: 0.0) - 1.0
        val yMax = (weights.maxOrNull() ?: 0.0) + 1.0
        val ySpan = (yMax - yMin).coerceAtLeast(0.1)

        val days = points.map { it.epochDay.toFloat() } + listOfNotNull(projectedDateEpochDay?.toFloat())
        val xMin = days.minOrNull() ?: 0f
        val xMax = days.maxOrNull() ?: 1f
        val xSpan = (xMax - xMin).coerceAtLeast(1f)

        fun xFor(epochDay: Long): Float = leftPad + ((epochDay - xMin) / xSpan) * plotWidth
        fun yFor(weight: Double): Float = (topPad + ((yMax - weight) / ySpan) * plotHeight).toFloat()

        // 网格线 + y 轴标签
        for (i in 0..3) {
            val y = topPad + plotHeight * i / 3f
            drawLine(Color(0xFFEEF1EC), Offset(leftPad, y), Offset(size.width - rightPad, y), 1f)
            val value = yMax - ySpan * i / 3f
            drawText(
                text = formatWeight(value, unit),
                x = leftPad - 6.dp.toPx(),
                y = y + 3.dp.toPx(),
                color = Color(0xFFA4ABA3),
                sizePx = 9.dp.toPx(),
                alignRight = true,
            )
        }

        // 目标虚线（水平）
        if (targetWeightKg != null && targetWeightKg in (yMin..yMax)) {
            val y = yFor(targetWeightKg)
            drawLine(
                color = TargetLineColor,
                start = Offset(leftPad, y),
                end = Offset(size.width - rightPad, y),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
            )
            drawText(
                text = "${weightUnitLabel(unit)} ${formatWeight(targetWeightKg, unit)}",
                x = leftPad + 4.dp.toPx(),
                y = y - 4.dp.toPx(),
                color = Color(0xFF6B865B),
                sizePx = 8.dp.toPx(),
            )
        }

        // 达成日竖线（垂直虚线）
        if (projectedDateEpochDay != null && projectedDateEpochDay in (xMin.toLong()..xMax.toLong())) {
            val x = xFor(projectedDateEpochDay)
            drawLine(
                color = ProjectionLineColor,
                start = Offset(x, topPad),
                end = Offset(x, topPad + plotHeight),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
            )
        }

        // 折线 + 数据点
        if (points.isNotEmpty()) {
            val linePath = Path()
            points.forEachIndexed { index, p ->
                val x = xFor(p.epochDay)
                val y = yFor(p.weightKg)
                if (index == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
            }
            drawPath(linePath, lineColor, style = Stroke(width = 5f))
            points.forEach { p ->
                drawCircle(lineColor, radius = 5f, center = Offset(xFor(p.epochDay), yFor(p.weightKg)))
                drawCircle(Color.White, radius = 2.5f, center = Offset(xFor(p.epochDay), yFor(p.weightKg)))
            }
        }

        // x 轴标签（首/末日期）
        drawText(formatShortDate(xMin.toLong()), leftPad, size.height - 5.dp.toPx(), Color(0xFFA1A9A0), 8.dp.toPx())
        drawText(
            formatShortDate(xMax.toLong()),
            size.width - rightPad,
            size.height - 5.dp.toPx(),
            Color(0xFFA1A9A0),
            8.dp.toPx(),
            alignRight = true,
        )
    }
}

private fun DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    sizePx: Float,
    alignRight: Boolean = false,
) {
    val paint = android.graphics.Paint().apply {
        this.color = color.toArgb()
        textSize = sizePx
        isAntiAlias = true
        if (alignRight) textAlign = android.graphics.Paint.Align.RIGHT
    }
    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}
