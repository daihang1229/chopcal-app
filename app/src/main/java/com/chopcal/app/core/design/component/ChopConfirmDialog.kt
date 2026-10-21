package com.chopcal.app.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.chopcal.app.core.design.theme.Ink
import com.chopcal.app.core.design.theme.InkMuted
import com.chopcal.app.core.design.theme.ScreenBg
import com.chopcal.app.core.design.theme.TitleStyle

/**
 * 品牌化确认弹窗（对齐 ChopCal 设计语言）。
 */
@Composable
fun ChopConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelText: String? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ScreenBg,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = TitleStyle,
                    color = Ink,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = InkMuted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                PrimaryButton(text = confirmText, onClick = onConfirm)
                if (cancelText != null) {
                    Spacer(Modifier.height(12.dp))
                    SecondaryButton(text = cancelText, onClick = onDismiss)
                }
            }
        }
    }
}
