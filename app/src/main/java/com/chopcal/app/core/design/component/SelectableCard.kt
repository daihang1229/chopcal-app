package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * 大选择卡片（目标四选一）：图标 + 标题 + 说明 + 勾选。
 */
@Composable
fun SelectableCard(
    selected: Boolean,
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) chopCalColors.forestGreen else chopCalColors.line
    val bg = if (selected) chopCalColors.surfaceMint else chopCalColors.card

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(if (selected) chopCalColors.lime else chopCalColors.surfaceMuted, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, fontSize = 18.sp, color = chopCalColors.forestGreen)
        }
        Column(modifier = Modifier.padding(start = 13.dp).weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = chopCalColors.ink)
            Text(text = description, fontSize = 11.sp, color = chopCalColors.inkMuted, modifier = Modifier.padding(top = 3.dp))
        }
        if (selected) {
            Text(text = "✓", fontSize = 15.sp, color = chopCalColors.forestGreen)
        }
    }
}
