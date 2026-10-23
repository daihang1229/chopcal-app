package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.Dish

/**
 * 中餐菜品库（标准配方，dishes_standard.json）。目标 100 → 1000 → 万级。
 */
interface DishRepository {

    /** 全部菜品。 */
    fun all(): List<Dish>

    /** 按菜名精确查找。 */
    fun find(name: String): Dish?

    /** 模糊搜索（名称 / 别名 / 拼音），按匹配度降序。 */
    fun search(query: String): List<Dish>

    /** AI 候选菜名 → 配方匹配（先精确后模糊取最高分）。 */
    fun matches(name: String): Dish?
}
