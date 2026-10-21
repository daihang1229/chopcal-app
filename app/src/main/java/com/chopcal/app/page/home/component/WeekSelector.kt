package com.chopcal.app.page.home.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.page.home.DayItem

/**
 * 周条（7 天，卡片式）。今天及过去可点，未来置灰不可点。
 */
@Composable
fun WeekSelector(
    days: List<DayItem>,
    onSelect: (DayItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        days.forEach { day ->
            DayCell(
                day = day,
                onClick = { if (!day.isFuture) onSelect(day) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DayCell(
    day: DayItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = day.isSelected
    val enabled = !day.isFuture
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) chopCalColors.forestGreen else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (day.isToday) stringResource(R.string.home_today) else day.weekdayLabel,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                selected -> Color.White
                day.isFuture -> chopCalColors.inkFaint
                else -> chopCalColors.inkMuted
            },
        )
        Text(
            text = day.dayOfMonth.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                selected -> Color.White
                day.isFuture -> chopCalColors.inkFaint
                else -> chopCalColors.ink
            },
            modifier = Modifier.padding(top = 2.dp),
        )
        // 今天标记点
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(4.dp)
                .background(
                    color = when {
                        day.isToday && selected -> Color.White
                        day.isToday -> chopCalColors.forestGreen
                        else -> Color.Transparent
                    },
                    shape = CircleShape,
                ),
        )
    }
}
