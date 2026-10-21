package com.chopcal.app.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 步骤底部按钮区：主按钮 + 可选次要文字链。
 */
@Composable
fun StepActions(
    primaryText: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryEnabled: Boolean = true,
    trailingArrow: Boolean = false,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PrimaryButton(
            text = primaryText,
            onClick = onPrimary,
            enabled = primaryEnabled,
            trailingArrow = trailingArrow,
        )
        if (secondaryText != null && onSecondary != null) {
            Spacer(Modifier.height(12.dp))
            SecondaryButton(text = secondaryText, onClick = onSecondary)
        }
    }
}
