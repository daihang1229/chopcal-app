package com.chopcal.app.domain.model

/**
 * 体重趋势在轨道状态（PROGRESS.md §3，按最近 30 天平均速率推算）。
 */
enum class OnTrackStatus {
    /** 按时：按当前节奏预计按时达成。 */
    ON_TRACK,

    /** 落后：比计划晚达成。 */
    BEHIND,

    /** 超前：比计划更快，需提示别减太快。 */
    AHEAD,

    /** 数据不足：暂不估算。 */
    INSUFFICIENT,
}

/**
 * 体重进度（目标卡 + 体重图共用），由 [com.chopcal.app.domain.usecase.GetWeightProgressUseCase] 计算。
 *
 * @param changedKg    已减/已增量（正数，按目标方向）
 * @param remainingKg  还剩多少（正数）
 * @param projectedDateEpochDay 预计达成日（无目标/数据不足为 null）
 * @param daysRemaining 还剩天数（无目标/数据不足为 null）
 * @param lateWeeks    落后周数（仅 BEHIND 时有效）
 * @param fluctuationKg 保持体重的波动范围（±），仅 MAINTAIN 目标有效
 */
data class WeightProgress(
    val goalType: GoalType,
    val initialWeightKg: Double,
    val currentWeightKg: Double,
    val targetWeightKg: Double?,
    val entries: List<WeightEntry>,
    val changedKg: Double,
    val remainingKg: Double,
    val projectedDateEpochDay: Long?,
    val daysRemaining: Long?,
    val onTrack: OnTrackStatus,
    val lateWeeks: Int = 0,
    val fluctuationKg: Double? = null,
) {
    /** 是否有目标体重/达成日可视化（MAINTAIN 无）。 */
    val hasTarget: Boolean get() = goalType.needsTargetWeight && targetWeightKg != null

    /** 目标完成进度 0..1（无目标为 0）。 */
    val progressFraction: Float
        get() {
            val total = (targetWeightKg?.minus(initialWeightKg))?.let { kotlin.math.abs(it) } ?: 0.0
            if (total <= 0.0) return 0f
            return (changedKg / total).toFloat().coerceIn(0f, 1f)
        }
}
