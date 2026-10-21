package com.chopcal.app.data.remote

import com.chopcal.app.domain.model.DishCandidate
import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.repository.DishRecognizer
import com.chopcal.app.domain.repository.DishRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 识别源 Fake 实现（MVP 占位）。
 *
 * 不调真实多模态 LLM，而是从菜品库取前 3 道作为候选，模拟置信度降序。
 * 后续替换为 Qwen-VL 识别（RecognizeRemoteSource 真实现）。
 */
@Singleton
class FakeDishRecognizer @Inject constructor(
    private val dishRepository: DishRepository,
) : DishRecognizer {

    override suspend fun recognize(imageUri: String?): List<DishCandidate> {
        // 模拟网络/模型推理耗时
        delay(900L)

        val pool = dishRepository.all()
        if (pool.isEmpty()) return emptyList()

        // 用 imageUri 的 hashCode 做伪随机，让每次拍照得到不同候选顺序
        val offset = (imageUri?.hashCode() ?: 0).mod(pool.size)
        val rotated = pool.drop(offset) + pool.take(offset)

        return rotated
            .take(3)
            .mapIndexed { index, dish -> dish.toCandidate(confidence(index)) }
    }

    private fun confidence(index: Int): Int = when (index) {
        0 -> 82
        1 -> 15
        else -> 3
    }

    private fun Dish.toCandidate(confidence: Int): DishCandidate = DishCandidate(
        name = name,
        confidence = confidence,
        baseKcal = baseKcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        ingredients = ingredients,
    )
}
