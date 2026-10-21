package com.chopcal.app.core.design.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.chopcal.app.core.design.theme.ForestGreen

/**
 * 顶部进度条（3px，圆头）。
 *
 * @param progress 0f..1f
 */
@Composable
fun OnboardingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .padding(horizontal = 24.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth()) {
            drawRoundRect(
                color = ForestGreen.copy(alpha = 0.12f),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(size.height / 2),
            )
            val w = size.width * progress.coerceIn(0f, 1f)
            if (w > 0) {
                drawLine(
                    color = ForestGreen,
                    start = Offset(0f, size.height / 2),
                    end = Offset(w, size.height / 2),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
