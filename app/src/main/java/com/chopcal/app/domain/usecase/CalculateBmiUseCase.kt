package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.BmiBand
import com.chopcal.app.domain.model.BmiResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 计算 BMI 并分档（中国标准）。
 */
@Singleton
class CalculateBmiUseCase @Inject constructor() {

    operator fun invoke(heightCm: Double, weightKg: Double): BmiResult {
        val heightM = heightCm / 100.0
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0.0
        val band = when {
            bmi < 18.5 -> BmiBand.UNDERWEIGHT
            bmi < 24.0 -> BmiBand.NORMAL
            else -> BmiBand.OVERWEIGHT
        }
        return BmiResult(bmi, band)
    }
}
