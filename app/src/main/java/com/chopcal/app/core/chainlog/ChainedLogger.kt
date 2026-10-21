package com.chopcal.app.core.chainlog

/**
 * 链式日志工具接口（主 + 子 + 子 的 span 树）。
 *
 * 用法：
 * ```
 * val chain = logger.startChain(TraceKeys.PHOTO_RECOGNITION)
 * chain.setAttribute("camera", "back")
 * val upload = chain.child(TraceKeys.UPLOAD)
 * upload.log("uploading image")
 * upload.end()                    // 子 span 结束
 * val infer = chain.child(TraceKeys.LLM_INFER)
 * infer.end(TraceStatus.FAILED)   // 标记失败
 * chain.end()                     // 根 span 结束（自动串联整条链）
 * ```
 */
interface ChainedLogger {

    /** 开启一条链，返回根 span。 */
    fun startChain(key: String): TraceSpan
}

/** 链中的一个节点（span）。 */
interface TraceSpan {

    /** 当前节点的 key。 */
    val key: String

    /** 在当前 span 下开启子 span。 */
    fun child(key: String): TraceSpan

    /** 追加一条日志（面包屑）。 */
    fun log(message: String)

    /** 设置一个属性（字符串）。 */
    fun setAttribute(key: String, value: String)

    /** 结束当前 span，记录耗时与状态。 */
    fun end(status: TraceStatus = TraceStatus.SUCCESS)
}

enum class TraceStatus { SUCCESS, FAILED }
