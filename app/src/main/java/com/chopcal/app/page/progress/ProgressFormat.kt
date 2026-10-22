package com.chopcal.app.page.progress

import com.chopcal.app.domain.model.WeightUnit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ShortDateFormatter = DateTimeFormatter.ofPattern("M/d", Locale.getDefault())
private val FullDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.getDefault())

/** 与 [com.chopcal.app.domain.usecase.ConvertUnitUseCase] 保持一致的换算系数。 */
private const val KG_PER_LB = 0.4536

/** 按全局单位展示体重（存储为 kg）。 */
fun formatWeight(kg: Double, unit: WeightUnit): String {
    val value = if (unit == WeightUnit.IMPERIAL) kg / KG_PER_LB else kg
    return "%.1f".format(Locale.US, value)
}

/** 把用户输入的值（以当前单位为基准）换算成 kg 存储。 */
fun weightToKg(value: Double, unit: WeightUnit): Double =
    if (unit == WeightUnit.IMPERIAL) value * KG_PER_LB else value

fun weightUnitLabel(unit: WeightUnit): String = if (unit == WeightUnit.IMPERIAL) "lb" else "kg"

fun formatShortDate(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(ShortDateFormatter)

fun formatFullDate(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(FullDateFormatter)

fun formatMonthDay(epochDay: Long): String {
    val date = LocalDate.ofEpochDay(epochDay)
    return "${date.monthValue}/${date.dayOfMonth}"
}
