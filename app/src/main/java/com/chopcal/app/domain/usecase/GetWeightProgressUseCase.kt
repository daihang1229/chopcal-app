package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.GoalType
import com.chopcal.app.domain.model.OnTrackStatus
import com.chopcal.app.domain.model.UserProfile
import com.chopcal.app.domain.model.WeightEntry
import com.chopcal.app.domain.model.WeightProgress
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 体重进度计算（目标卡 + 体重图），对齐 PROGRESS.md §2/§3。
 *
 * - 当前体重 = 最新一条 weight_entries，无则用初始体重
 * - 已减/已增 = 按目标方向计算
 * - 预计达成日 = 按最近 30 天平均速率推算
 * - 保持体重 = 当前体重 + 波动范围，无目标/日期
 */
@Singleton
class GetWeightProgressUseCase @Inject constructor() {

    operator fun invoke(
        profile: UserProfile?,
        target: DailyTarget?,
        weights: List<WeightEntry>,
        today: LocalDate = LocalDate.now(),
    ): WeightProgress {
        val goalType = profile?.goalType ?: GoalType.MAINTAIN
        val initial = profile?.weightKg ?: weights.firstOrNull()?.weightKg ?: 0.0
        val sorted = weights.sortedBy { it.dateEpochDay }
        val current = sorted.lastOrNull()?.weightKg ?: initial
        val targetWeight = if (goalType.needsTargetWeight) profile?.targetWeightKg else null

        val direction = goalType.direction
        val changedKg = when (direction) {
            -1 -> (initial - current).coerceAtLeast(0.0)
            1 -> (current - initial).coerceAtLeast(0.0)
            else -> 0.0
        }
        val remainingKg = targetWeight?.let { abs(it - current) } ?: 0.0

        var projected: Long? = null
        var daysRemaining: Long? = null
        var onTrack = OnTrackStatus.INSUFFICIENT
        var lateWeeks = 0

        if (goalType.needsTargetWeight && targetWeight != null && sorted.size >= 2) {
            val rate = recentRate(sorted, today) // kg/day，减重为负
            if (abs(rate) > 1e-9) {
                val days = (targetWeight - current) / rate
                if (days.isFinite()) {
                    val rounded = days.toLong().coerceAtLeast(0)
                    daysRemaining = rounded
                    projected = today.toEpochDay() + rounded
                    val planned = target?.projectedDateEpochDay
                    onTrack = when {
                        planned == null -> OnTrackStatus.ON_TRACK
                        rounded <= 0L -> OnTrackStatus.AHEAD
                        projected!! <= planned + 14 -> OnTrackStatus.ON_TRACK
                        else -> {
                            lateWeeks = ((projected!! - planned) / 7).toInt().coerceAtLeast(1)
                            OnTrackStatus.BEHIND
                        }
                    }
                }
            }
        }

        val fluctuation = if (goalType == GoalType.MAINTAIN && sorted.isNotEmpty()) {
            computeFluctuation(sorted)
        } else null

        return WeightProgress(
            goalType = goalType,
            initialWeightKg = initial,
            currentWeightKg = current,
            targetWeightKg = targetWeight,
            entries = sorted,
            changedKg = changedKg,
            remainingKg = remainingKg,
            projectedDateEpochDay = projected,
            daysRemaining = daysRemaining,
            onTrack = onTrack,
            lateWeeks = lateWeeks,
            fluctuationKg = fluctuation,
        )
    }

    /** 最近 30 天平均速率（kg/day），数据不足则回退到全部点。 */
    private fun recentRate(entries: List<WeightEntry>, today: LocalDate): Double {
        val cutoff = today.minusDays(30).toEpochDay()
        val window = entries.filter { it.dateEpochDay >= cutoff }
        val series = window.ifEmpty { entries }
        if (series.size < 2) return 0.0
        val first = series.first()
        val last = series.last()
        val days = (last.dateEpochDay - first.dateEpochDay).coerceAtLeast(1)
        return (last.weightKg - first.weightKg) / days
    }

    /** 保持体重：平均绝对偏离（波动范围），取 1 位小数。 */
    private fun computeFluctuation(entries: List<WeightEntry>): Double {
        val mean = entries.map { it.weightKg }.average()
        val deviation = entries.map { abs(it.weightKg - mean) }.average()
        return (deviation * 10).roundToInt() / 10.0
    }
}
