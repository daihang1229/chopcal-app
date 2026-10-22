package com.chopcal.app.page.progress.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.chopCalColors

/**
 * 行内时间范围分段选择（自开始 / 1月 / 7日 或 7日 / 1月）。
 */
@Composable
fun TimeRangeSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(chopCalColors.surfaceMuted)
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Text(
                text = option,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) chopCalColors.forestGreen else chopCalColors.inkMuted,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) chopCalColors.card else chopCalColors.surfaceMuted)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}
