package com.chopcal.app.core.log

import android.util.Log
import com.chopcal.app.BuildConfig
import com.chopcal.app.core.analytics.CrashReporter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 统一日志工具。
 *
 * - d/i：仅写 Logcat（仅 DEBUG 构建）。
 * - w/e：写 Logcat + 同步到 Crashlytics 面包屑（崩溃时随报告上传）。
 * - e(message, throwable)：额外把异常记录为 Crashlytics 非致命异常。
 *
 * 业务代码统一从这里打日志，不要直接 Log.x。
 */
@Singleton
class Logger @Inject constructor(
    private val crashReporter: CrashReporter,
) {

    private val tag = "ChopCal"

    fun d(message: String) = logcat(Log.DEBUG, message)

    fun i(message: String) = logcat(Log.INFO, message)

    fun w(message: String) {
        logcat(Log.WARN, message)
        crashReporter.log(message)
    }

    fun e(message: String) {
        logcat(Log.ERROR, message)
        crashReporter.log(message)
    }

    fun e(message: String, throwable: Throwable) {
        logcat(Log.ERROR, "$message ${throwable.message.orEmpty()}")
        crashReporter.log(message)
        crashReporter.recordException(throwable)
    }

    private fun logcat(priority: Int, message: String) {
        if (BuildConfig.DEBUG) {
            Log.println(priority, tag, message)
        }
    }
}
