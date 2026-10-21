package com.chopcal.app.domain.model

import androidx.annotation.StringRes
import com.chopcal.app.R
import kotlinx.serialization.Serializable

/**
 * 目标类型。枚举携带所有计算系数，新增目标只需加一个枚举值。
 *
 * @param direction -1 减 / 0 保持 / +1 增
 * @param proteinFactor 蛋白系数（g/kg）
 * @param calorieCap 缺口/盈余上限（kcal）
 * @param needsTargetWeight 是否需要走「目标体重 + 达成时间」步骤
 */
@Serializable
enum class GoalType(
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int,
    val direction: Int,
    val proteinFactor: Double,
    val calorieCap: Int,
    val needsTargetWeight: Boolean,
) {
    LOSE(R.string.goal_lose, R.string.goal_lose_desc, -1, 1.8, 1000, true),
    MAINTAIN(R.string.goal_maintain, R.string.goal_maintain_desc, 0, 1.4, 0, false),
    GAIN(R.string.goal_gain, R.string.goal_gain_desc, 1, 1.5, 500, true),
    MUSCLE(R.string.goal_muscle, R.string.goal_muscle_desc, 1, 1.9, 400, true),
}
