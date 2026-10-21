package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.repository.DishRecognizer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 拍照 → 识别菜品候选（带置信度）。
 */
@Singleton
class RecognizeDishUseCase @Inject constructor(
    private val dishRecognizer: DishRecognizer,
) {

    suspend operator fun invoke(imageUri: String?): List<DishCandidate> =
        dishRecognizer.recognize(imageUri)
}
