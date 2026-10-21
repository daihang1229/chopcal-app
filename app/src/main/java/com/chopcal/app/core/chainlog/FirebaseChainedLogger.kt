package com.chopcal.app.core.chainlog

import android.os.SystemClock
import com.chopcal.app.core.analytics.AnalyticsTracker
import com.chopcal.app.core.analytics.CrashReporter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val EVENT_CHAIN_SPAN = "chain_span"

/**
 * 链式日志的 Firebase 实现。
 *
 * 每个 span 结束时会往 Analytics 上报一条 `chain_span` 事件，携带
 * trace_id / span_id / parent_id / key / duration_ms / status，
 * 后台（BigQuery）可据此还原出完整的主+子+子树。
 */
@Singleton
class FirebaseChainedLogger @Inject constructor(
    private val analytics: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : ChainedLogger {

    override fun startChain(key: String): TraceSpan =
        FirebaseTraceSpan(
            key = key,
            traceId = UUID.randomUUID().toString(),
            parentId = null,
            analytics = analytics,
            crashReporter = crashReporter,
        )
}

private class FirebaseTraceSpan(
    override val key: String,
    private val traceId: String,
    private val parentId: String?,
    private val analytics: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : TraceSpan {

    private val spanId = UUID.randomUUID().toString()
    private val startedAt = SystemClock.elapsedRealtime()
    private val attributes = linkedMapOf<String, String>()
    private var ended = false

    override fun child(key: String): TraceSpan =
        FirebaseTraceSpan(key, traceId, parentId = spanId, analytics, crashReporter)

    override fun log(message: String) {
        crashReporter.log("[$key] $message")
    }

    override fun setAttribute(key: String, value: String) {
        attributes[key] = value
    }

    override fun end(status: TraceStatus) {
        if (ended) return
        ended = true

        val durationMs = SystemClock.elapsedRealtime() - startedAt
        val params = mutableMapOf<String, Any>(
            "trace_id" to traceId,
            "span_id" to spanId,
            "parent_id" to (parentId ?: ""),
            "key" to key,
            "duration_ms" to durationMs,
            "status" to status.name.lowercase(),
        )
        attributes.forEach { (k, v) -> params["attr_$k"] = v }
        analytics.logEvent(EVENT_CHAIN_SPAN, params)
    }
}
