package com.rainbow.compose.core.base

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.rainbow.compose.core.R

private const val EXIT_DOUBLE_PRESS_INTERVAL_MS = 2000L

/**
 * Compose 页面 Activity 基类。
 *
 * 仅做最小职责：基于 [ComponentActivity]，提供 [setComposeContent] 便捷入口，
 * 不耦合任何路由、事件总线、沉浸式或业务埋点逻辑。
 *
 * 统一开启 edge-to-edge（沉浸式状态栏/导航栏），由各页面主题适配系统栏图标颜色。
 *
 * 继承链：
 *   YourComposeActivity → BaseCommonComposeActivity → BaseComposeActivity → ComponentActivity
 */
abstract class BaseComposeActivity : ComponentActivity() {
    private var lastBackPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context = applicationContext
        // 双击返回退出：第一次提示，短时间再次点击退出
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastBackPressTime <= EXIT_DOUBLE_PRESS_INTERVAL_MS) {
                        finish()
                    } else {
                        lastBackPressTime = now
                        Toast.makeText(
                            context,
                            getString(R.string.exit_hint),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            },
        )

    }

    /**
     * 设置 Compose 内容。子类在 [onCreate] 中调用，例如：
     * ```
     * setComposeContent { MyScreen() }
     * ```
     */
    protected fun setComposeContent(content: @Composable () -> Unit) {
        setContent(content = content)
    }
}
