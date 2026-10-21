package com.chopcal.app.core.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chopcal.app.core.design.theme.chopCalColors

/**
 * 品牌化加载动画（Google 官方 Material3 CircularProgressIndicator）。
 */
@Composable
fun ChopLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = chopCalColors.forestGreen)
    }
}
