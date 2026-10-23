package com.chopcal.app.domain.usecase

import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.repository.DishRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 模糊搜索菜品库（名称/别名/拼音），并支持候选名解析为配方。
 */
@Singleton
class SearchDishUseCase @Inject constructor(
    private val dishRepository: DishRepository,
) {

    /** 模糊搜索，按匹配度降序。 */
    operator fun invoke(query: String): List<Dish> =
        dishRepository.search(query)

    /** AI 候选名 → 配方（先精确后模糊）。 */
    fun resolve(name: String): Dish? =
        dishRepository.matches(name)
}
