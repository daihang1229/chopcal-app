package com.chopcal.app.core.chainlog

/**
 * 链式日志 key 常量。每条链一个 key，新增链路只需加一个常量。
 *
 * 顶层链用「流程名」，子节点用「步骤名」，层级关系由父 span 的 child() 表达。
 */
object TraceKeys {

    // ── 引导页 ────────────────────────────────────────────────
    const val ONBOARDING = "onboarding"          // 根：整个引导流程
    const val ONBOARDING_STEP = "step"           // 子：某一步

    // ── 拍照识别 ──────────────────────────────────────────────
    const val PHOTO_RECOGNITION = "photo_recognition"  // 根：拍照 → 识别 → 结果
    const val UPLOAD = "upload"                        // 子：上传图片
    const val LLM_INFER = "llm_infer"                  // 子：LLM 推理
    const val MATCH_DISH = "match_dish"                // 子：匹配菜品库

    // ── 记录 ────────────────────────────────────────────────
    const val MEAL_SAVE = "meal_save"           // 根：保存饮食记录
}
