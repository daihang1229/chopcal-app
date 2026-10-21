package com.chopcal.app.page.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.DailySummary
import com.chopcal.app.domain.model.MacroTargets

/**
 * 宏量三列：蛋白 / 碳水 / 脂肪，各用圆角白底卡片包起 + 图标。
 */
@Composable
fun MacroSection(
    summary: DailySummary,
    targets: MacroTargets,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MacroCell(
            label = "PROTEIN",
            icon = "🥩",
            accentColor = Color(0xFF608764),
            current = summary.proteinG,
            target = targets.proteinG,
            modifier = Modifier.weight(1f),
        )
        MacroCell(
            label = "CARBS",
            icon = "🍚",
            accentColor = Color(0xFFC88A56),
            current = summary.carbsG,
            target = targets.carbsG,
            modifier = Modifier.weight(1f),
        )
        MacroCell(
            label = "FAT",
            icon = "🥑",
            accentColor = Color(0xFF8690A9),
            current = summary.fatG,
            target = targets.fatG,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MacroCell(
    label: String,
    icon: String,
    accentColor: Color,
    current: Int,
    target: Int,
    modifier: Modifier = Modifier,
) {
    val ratio = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f
    Column(
        modifier = modifier
            .background(chopCalColors.card, RoundedCornerShape(16.dp))
            .border(1.dp, chopCalColors.line, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 13.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(accentColor.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = icon, fontSize = 12.sp)
            }
            Text(
                text = label,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.4.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = current.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
            )
            Text(
                text = "/${target}g",
                fontSize = 9.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier.padding(start = 2.dp, bottom = 2.dp),
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(3.dp)
                .background(chopCalColors.line, RoundedCornerShape(2.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .height(3.dp)
                    .background(accentColor, RoundedCornerShape(2.dp)),
            )
        }
    }
}
