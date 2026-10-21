package com.chopcal.app.core.design.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.InkMuted

/**
 * 文字链按钮（次要操作，如「返回上一步」「跳过，先体验」）。
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        fontSize = 11.sp,
        color = InkMuted,
        textAlign = TextAlign.Center,
    )
}
