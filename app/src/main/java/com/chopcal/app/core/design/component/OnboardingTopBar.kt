package com.chopcal.app.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.ForestGreen
import com.chopcal.app.core.design.theme.Ink
import com.chopcal.app.core.design.theme.InkMuted
import com.chopcal.app.core.design.theme.Lime

/**
 * 顶部栏：品牌 + 右上角动作（跳过/返回，可为 null 表示隐藏）。
 */
@Composable
fun OnboardingTopBar(
    actionLabel: String?,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(23.dp)
                .background(ForestGreen, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "✳", fontSize = 14.sp, color = Lime)
        }
        Text(
            text = "chopcal",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
            modifier = Modifier.padding(start = 8.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        if (actionLabel != null) {
            val interactionSource = remember { MutableInteractionSource() }
            Text(
                text = actionLabel,
                fontSize = 12.sp,
                color = InkMuted,
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onAction,
                    )
                    .padding(8.dp),
            )
        }
    }
}
