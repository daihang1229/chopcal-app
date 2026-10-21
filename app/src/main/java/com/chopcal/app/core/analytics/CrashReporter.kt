package com.chopcal.app.core.analytics

/**
 * 崩溃上报抽象。
 */
interface CrashReporter {

    /** 设置用户身份（device UUID），便于后台定位到具体用户。 */
    fun setUserId(userId: String)

    /** 追加一条自定义日志（崩溃时随报告上传）。 */
    fun log(message: String)

    /** 记录一个非致命异常（不崩溃，但上报）。 */
    fun recordException(throwable: Throwable)
}
