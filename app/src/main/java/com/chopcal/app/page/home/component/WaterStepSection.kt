package com.chopcal.app.page.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import java.util.Locale

/**
 * 水 / 步数两卡（无目标，纯手动记录），点击弹出滚轮选择器。
 */
@Composable
fun WaterStepSection(
    waterMl: Int,
    stepCount: Int,
    onWaterClick: () -> Unit,
    onStepClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MetricCard(
            label = stringResource(R.string.home_water),
            valueText = waterMl.toString(),
            unit = stringResource(R.string.home_water_unit),
            icon = "💧",
            iconBg = Color(0xFFEEF5F5),
            onClick = onWaterClick,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = stringResource(R.string.home_step),
            valueText = String.format(Locale.US, "%,d", stepCount),
            unit = stringResource(R.string.home_step_unit),
            icon = "👟",
            iconBg = Color(0xFFF5F1E8),
            onClick = onStepClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MetricCard(
    label: String,
    valueText: String,
    unit: String,
    icon: String,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(chopCalColors.card, RoundedCornerShape(17.dp))
            .border(1.dp, chopCalColors.line, RoundedCornerShape(17.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, fontSize = 14.sp)
        }
        Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
            Text(text = label, fontSize = 8.5.sp, color = chopCalColors.inkMuted)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = valueText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = chopCalColors.ink,
                )
                Text(
                    text = unit,
                    fontSize = 8.5.sp,
                    color = chopCalColors.inkMuted,
                    modifier = Modifier.padding(start = 2.dp, bottom = 1.dp),
                )
            }
        }
        Text(text = "›", fontSize = 14.sp, color = chopCalColors.inkFaint)
    }
}
