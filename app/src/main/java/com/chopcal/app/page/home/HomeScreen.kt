package com.chopcal.app.page.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.chopcal.app.BuildConfig
import com.chopcal.app.R
import com.rainbow.compose.ui.theme.CommonTheme

/**
 * Tab 1：拍照主流程（拍照 → 识别 → 结果）。
 *
 * 当前为骨架版本：品牌展示 + 拍照入口按钮。
 */
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(56.dp))

        Text(
            text = "ChopCal",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Know every bite",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))

        // 拍照入口按钮
        FilledIconButton(
            onClick = { /* TODO: 拍照/选图 */ },
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "拍照识别",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.home_tagline),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // 仅 DEBUG 构建可见：崩溃测试入口（验证 Crashlytics 用）
        if (BuildConfig.DEBUG) {
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { throw RuntimeException("ChopCal test crash") }) {
                Text("Test crash")
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    CommonTheme(darkTheme = false) {
        HomeScreen()
    }
}
