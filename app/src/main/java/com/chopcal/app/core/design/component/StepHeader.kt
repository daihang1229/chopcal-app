package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.TitleStyle

/**
 * 步骤标题区：kicker + 标题 + 副标题。
 */
@Composable
fun StepHeader(
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    sub: String? = null,
) {
    Column(modifier = modifier) {
        if (kicker != null) {
            KickerText(text = kicker)
            Spacer(Modifier.height(10.dp))
        }
        Text(text = title, style = TitleStyle, color = chopCalColors.ink)
        if (sub != null) {
            Spacer(Modifier.height(9.dp))
            Text(text = sub, fontSize = 13.sp, lineHeight = 21.sp, color = chopCalColors.inkMuted)
        }
    }
}
