package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
/**
 * 大写小标（kicker），如「01 / 你的方向」。
 */
@Composable
fun KickerText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 2.sp,
    ),
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = chopCalColors.inkMuted,
    )
}
