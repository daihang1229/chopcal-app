package com.chopcal.app.page.privacy

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors

/**
 * 隐私政策 / 权限说明 URL（远程托管，可随时更新，无需发版）。
 */
private const val PRIVACY_URL = "https://chopcal-c5399.web.app/privacy.html"

/**
 * 隐私政策 + 权限说明页（引导页 / 个人中心共用）。
 *
 * 用 WebView 加载远程页面，方便运营随时更新合规文案而不用发版。
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var failed by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(chopCalColors.paper)
            .statusBarsPadding(),
    ) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .background(chopCalColors.paper)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(chopCalColors.surfaceMuted)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_back),
                    tint = chopCalColors.forestGreen,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = stringResource(R.string.privacy_title),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(start = 12.dp),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webView = this
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        // 移动端 viewport：按设备宽度渲染，避免页面按桌面宽度缩得很小
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                                loading = true
                                failed = false
                            }

                            override fun onPageFinished(view: WebView, url: String?) {
                                loading = false
                            }

                            override fun onReceivedError(
                                view: WebView,
                                request: WebResourceRequest,
                                error: WebResourceError,
                            ) {
                                if (request.isForMainFrame) {
                                    failed = true
                                    loading = false
                                }
                            }
                        }
                        loadUrl(PRIVACY_URL)
                    }
                },
                onRelease = { it.destroy() },
                modifier = Modifier.fillMaxSize(),
            )

            if (loading) {
                CircularProgressIndicator(
                    color = chopCalColors.forestGreen,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            if (failed) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.privacy_load_failed),
                        fontSize = 13.sp,
                        color = chopCalColors.inkMuted,
                    )
                    TextButton(onClick = { webView?.reload() }) {
                        Text(
                            text = stringResource(R.string.privacy_retry),
                            color = chopCalColors.forestGreen,
                        )
                    }
                }
            }
        }
    }
}
