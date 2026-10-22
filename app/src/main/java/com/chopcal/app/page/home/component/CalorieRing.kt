package com.chopcal.app.page.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import kotlin.math.roundToInt

/**
 * 环形进度：已摄入 ÷ 目标，超目标变橙。
 */
@Composable
fun CalorieRing(
    currentKcal: Int,
    targetKcal: Int,
    modifier: Modifier = Modifier,
) {
    val progress = if (targetKcal > 0) (currentKcal.toFloat() / targetKcal).coerceIn(0f, 1f) else 0f
    val over = currentKcal > targetKcal
    val ringColor = if (over) chopCalColors.warning else chopCalColors.forestGreen
    val trackColor = chopCalColors.line
    val percent = if (targetKcal > 0) (currentKcal.toFloat() / targetKcal * 100).roundToInt() else 0

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            val inset = 6.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            // 底环
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = stroke,
            )
            // 进度环
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = stroke,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentKcal.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.5).sp,
                color = chopCalColors.ink,
            )
            Text(
                text = stringResource(R.string.home_calorie_of_target, targetKcal),
                fontSize = 10.sp,
                color = chopCalColors.inkMuted,
            )
            Text(
                text = stringResource(R.string.home_calorie_complete, percent),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.forestGreen,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
