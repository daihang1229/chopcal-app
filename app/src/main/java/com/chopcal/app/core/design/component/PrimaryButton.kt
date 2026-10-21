package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * 主按钮（胶囊绿按钮）。
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingArrow: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(51.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = chopCalColors.forestGreen,
            contentColor = chopCalColors.inkOnGreen,
            disabledContainerColor = Color(0xFFCBD1C9),
            disabledContentColor = Color.White,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            if (trailingArrow) {
                Text(text = "  →", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
