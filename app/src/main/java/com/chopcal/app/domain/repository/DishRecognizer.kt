package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.DishCandidate

/**
 * 菜品识别源（多模态 LLM）。业务层只依赖此接口，data 层提供实现（当前为 Fake）。
 */
interface DishRecognizer {

    /**
     * 识别一道菜，返回候选（菜名 + 置信度 + 基准热量）。
     * [imageUri] 为拍照/相册得到的图片（MVP 的 Fake 实现暂不消费）。
     */
    suspend fun recognize(imageUri: String?): List<DishCandidate>
}
