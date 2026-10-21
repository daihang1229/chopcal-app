package com.chopcal.app.core.analytics

/**
 * 埋点分析抽象。业务层只依赖此接口，便于替换实现或在测试中注入 Fake。
 */
interface AnalyticsTracker {

    /** 设置匿名身份（device UUID），用于跨事件关联。 */
    fun setUserId(userId: String)

    /** 上报自定义事件。 */
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
}
