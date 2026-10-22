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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.page.progress.CaloriesChartUi
import com.chopcal.app.page.progress.ShortRange

private val BarOkColor = Color(0xFF83A875)
private val BarOverColor = Color(0xFFE2A363)
private val KcalTargetLineColor = Color(0xFFD7B883)

/**
 * 卡路里摄入卡片（柱状图 + 目标线）。
 */
@Composable
fun CaloriesChart(
    data: CaloriesChartUi,
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
                    text = stringResource(R.string.progress_calories_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = chopCalColors.ink,
                )
                Text(
                    text = stringResource(
                        if (data.hasData) R.string.progress_calories_sub else R.string.progress_calories_sub_empty
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

        // 目标图例
        if (data.targetKcal > 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .height(1.dp)
                        .width(10.dp)
                        .background(KcalTargetLineColor),
                )
                Text(
                    text = stringResource(R.string.progress_calorie_target, data.targetKcal),
                    fontSize = 9.sp,
                    color = chopCalColors.inkMuted,
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        if (data.hasData) {
            CalorieBarChart(
                bars = data.bars,
                targetKcal = data.targetKcal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            )
        } else {
            CaloriesEmptyState()
        }
    }
}

@Composable
private fun CaloriesEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(chopCalColors.surfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            fontSize = 20.sp,
            color = chopCalColors.inkFaint,
        )
    }
}

@Composable
private fun CalorieBarChart(
    bars: List<com.chopcal.app.page.progress.CalorieBar>,
    targetKcal: Int,
    modifier: Modifier = Modifier,
) {
    val todayColor = chopCalColors.forestGreen
    Canvas(modifier = modifier) {
        val leftPad = 6.dp.toPx()
        val rightPad = 6.dp.toPx()
        val topPad = 14.dp.toPx()
        val bottomPad = 20.dp.toPx()
        val plotWidth = size.width - leftPad - rightPad
        val plotHeight = size.height - topPad - bottomPad

        val maxKcal = (bars.maxOfOrNull { it.kcal } ?: 0f).coerceAtLeast(targetKcal.toFloat())
        val yMax = (maxKcal * 1.15f).coerceAtLeast(1f)

        fun yFor(kcal: Float) = topPad + (1f - kcal / yMax) * plotHeight

        // 目标线
        if (targetKcal > 0) {
            val y = yFor(targetKcal.toFloat())
            drawLine(
                color = KcalTargetLineColor,
                start = Offset(leftPad, y),
                end = Offset(size.width - rightPad, y),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
            )
        }

        if (bars.isNotEmpty()) {
            val slot = plotWidth / bars.size
            val barWidth = slot * 0.45f
            bars.forEachIndexed { index, bar ->
                val cx = leftPad + slot * index + slot / 2f
                val barHeight = (bar.kcal / yMax).coerceIn(0f, 1f) * plotHeight
                val color = if (targetKcal > 0 && bar.kcal > targetKcal) BarOverColor else BarOkColor
                drawRoundRect(
                    color = color,
                    topLeft = Offset(cx - barWidth / 2f, yFor(bar.kcal)),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                )
                // 星期标签
                drawText(
                    text = bar.weekdayLabel,
                    x = cx,
                    y = size.height - 5.dp.toPx(),
                    color = if (bar.isToday) todayColor else Color(0xFF9AA39A),
                    sizePx = 9.dp.toPx(),
                    alignCenter = true,
                    bold = bar.isToday,
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    sizePx: Float,
    alignCenter: Boolean = false,
    bold: Boolean = false,
) {
    val paint = android.graphics.Paint().apply {
        this.color = color.toArgb()
        textSize = sizePx
        isAntiAlias = true
        if (alignCenter) textAlign = android.graphics.Paint.Align.CENTER
        if (bold) typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}
