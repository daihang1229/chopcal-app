package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.Dish

/**
 * 中餐菜品库（标准配方）。MVP 先内置少量样例，后续换成 500 道 JSON。
 */
interface DishRepository {

    /** 全部菜品。 */
    fun all(): List<Dish>

    /** 按菜名精确查找。 */
    fun find(name: String): Dish?
}
