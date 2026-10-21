package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * 单选行（运动量五选一）：圆点 + 标题 + 说明 + 尾部系数。
 */
@Composable
fun RadioRow(
    selected: Boolean,
    title: String,
    description: String,
    trailing: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) chopCalColors.forestGreen else chopCalColors.line
    val bg = if (selected) chopCalColors.surfaceMint else chopCalColors.card

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(13.dp))
            .border(1.dp, borderColor, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(17.dp)
                .border(1.dp, if (selected) chopCalColors.forestGreen else chopCalColors.inkMuted, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(modifier = Modifier.size(9.dp).background(chopCalColors.forestGreen, CircleShape))
            }
        }
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = chopCalColors.ink)
            Text(text = description, fontSize = 10.sp, color = chopCalColors.inkMuted, modifier = Modifier.padding(top = 3.dp))
        }
        Text(text = trailing, fontSize = 10.sp, color = chopCalColors.inkMuted)
    }
}
