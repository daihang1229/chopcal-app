package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * 分段选择（性别三选一）。
 */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            val on = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (index == 0) 0.dp else 4.dp)
                    .background(if (on) chopCalColors.surfaceMint else chopCalColors.card, RoundedCornerShape(12.dp))
                    .border(1.dp, if (on) chopCalColors.forestGreen else chopCalColors.line, RoundedCornerShape(12.dp))
                    .clickable { onSelect(index) }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option,
                    fontSize = 13.sp,
                    fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (on) chopCalColors.forestGreen else chopCalColors.inkMuted,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
