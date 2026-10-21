package com.chopcal.app.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.BigNumberStyle
import com.chopcal.app.core.design.theme.CardWhite
import com.chopcal.app.core.design.theme.ForestGreen
import com.chopcal.app.core.design.theme.Ink
import com.chopcal.app.core.design.theme.InkFaint
import com.chopcal.app.core.design.theme.Line
import com.chopcal.app.core.design.theme.WarningOrange

/**
 * 大号数字输入框（可见输入框样式：白底 + 边框 + 圆角）。
 */
@Composable
fun BigNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    decimal: Boolean = false,
    error: Int? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(14.dp))
                .border(1.dp, if (error != null) WarningOrange else Line, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = BigNumberStyle,
                        color = InkFaint,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = BigNumberStyle.copy(color = Ink, textAlign = TextAlign.Start),
                    singleLine = true,
                    cursorBrush = SolidColor(ForestGreen),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
                    ),
                )
            }
            Text(
                text = unit,
                fontSize = 13.sp,
                color = InkFaint,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        if (error != null) {
            Text(
                text = stringResource(error),
                fontSize = 11.sp,
                color = WarningOrange,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
