package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
/**
 * 表单字段标签。
 */
@Composable
fun FieldLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = chopCalColors.ink,
        modifier = modifier,
    )
}
