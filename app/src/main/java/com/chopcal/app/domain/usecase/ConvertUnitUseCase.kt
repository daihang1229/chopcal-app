package com.chopcal.app.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * 单位换算（存储统一 cm/kg，仅展示时换算）。
 */
@Singleton
class ConvertUnitUseCase @Inject constructor() {

    data class ImperialHeight(val feet: Int, val inches: Int)

    private fun cmToInches(cm: Double): Double = cm / 2.54
    private fun inchesToCm(inches: Double): Double = inches * 2.54
    private fun kgToLb(kg: Double): Double = kg / 0.4536
    private fun lbToKg(lb: Double): Double = lb * 0.4536

    fun cmToImperial(cm: Double): ImperialHeight {
        val total = cmToInches(cm)
        val feet = (total / 12).toInt()
        val inches = (total - feet * 12).roundToInt()
        return ImperialHeight(feet, inches)
    }

    fun imperialToCm(feet: Int, inches: Int): Double = inchesToCm((feet * 12 + inches).toDouble())

    fun kgToLbValue(kg: Double): Double = kgToLb(kg)
    fun lbToKgValue(lb: Double): Double = lbToKg(lb)
}
