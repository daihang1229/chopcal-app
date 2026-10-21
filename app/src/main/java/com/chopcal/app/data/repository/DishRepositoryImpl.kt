package com.chopcal.app.data.repository

import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.model.IngredientLine
import com.chopcal.app.domain.repository.DishRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 中餐菜品库（标准配方）MVP 实现。
 *
 * 目前内置少量样例，后续替换为 500 道配方 JSON（data/foods.json）。
 * 热量从「主料 + 辅料 + 调料」配方自动推导，符合热量计算规则.md。
 */
@Singleton
class DishRepositoryImpl @Inject constructor() : DishRepository {

    private val dishes: Map<String, Dish> = listOf(
        Dish(
            name = "宫保鸡丁",
            baseKcal = 520,
            proteinG = 38,
            carbsG = 22,
            fatG = 30,
            ingredients = listOf(
                IngredientLine("鸡胸肉", 250, 330),
                IngredientLine("花生米", 50, 280),
                IngredientLine("植物油", 25, 225),
            ),
        ),
        Dish(
            name = "麻婆豆腐",
            baseKcal = 410,
            proteinG = 24,
            carbsG = 16,
            fatG = 28,
            ingredients = listOf(
                IngredientLine("嫩豆腐", 300, 240),
                IngredientLine("猪肉末", 60, 90),
                IngredientLine("植物油", 20, 180),
            ),
        ),
        Dish(
            name = "清炒虾仁",
            baseKcal = 280,
            proteinG = 34,
            carbsG = 8,
            fatG = 12,
            ingredients = listOf(
                IngredientLine("虾仁", 200, 200),
                IngredientLine("植物油", 10, 90),
            ),
        ),
        Dish(
            name = "红烧排骨",
            baseKcal = 640,
            proteinG = 42,
            carbsG = 18,
            fatG = 44,
            ingredients = listOf(
                IngredientLine("猪排骨", 300, 830),
                IngredientLine("白糖", 20, 80),
                IngredientLine("植物油", 15, 135),
            ),
        ),
        Dish(
            name = "清蒸鲈鱼",
            baseKcal = 300,
            proteinG = 40,
            carbsG = 2,
            fatG = 14,
            ingredients = listOf(
                IngredientLine("鲈鱼", 350, 385),
                IngredientLine("植物油", 5, 45),
            ),
        ),
        Dish(
            name = "西红柿炒鸡蛋",
            baseKcal = 350,
            proteinG = 20,
            carbsG = 16,
            fatG = 22,
            ingredients = listOf(
                IngredientLine("鸡蛋", 150, 220),
                IngredientLine("西红柿", 200, 40),
                IngredientLine("植物油", 15, 135),
            ),
        ),
        Dish(
            name = "干煸四季豆",
            baseKcal = 360,
            proteinG = 8,
            carbsG = 20,
            fatG = 28,
            ingredients = listOf(
                IngredientLine("四季豆", 250, 75),
                IngredientLine("猪肉末", 50, 75),
                IngredientLine("植物油", 20, 180),
            ),
        ),
        Dish(
            name = "糖醋里脊",
            baseKcal = 580,
            proteinG = 34,
            carbsG = 38,
            fatG = 30,
            ingredients = listOf(
                IngredientLine("猪里脊", 250, 360),
                IngredientLine("白糖", 25, 100),
                IngredientLine("植物油", 20, 180),
            ),
        ),
        Dish(
            name = "蛋炒饭",
            baseKcal = 500,
            proteinG = 16,
            carbsG = 66,
            fatG = 20,
            ingredients = listOf(
                IngredientLine("米饭", 300, 350),
                IngredientLine("鸡蛋", 100, 150),
                IngredientLine("植物油", 10, 90),
            ),
        ),
        Dish(
            name = "口水鸡",
            baseKcal = 460,
            proteinG = 38,
            carbsG = 6,
            fatG = 32,
            ingredients = listOf(
                IngredientLine("鸡腿", 300, 540),
                IngredientLine("红油", 15, 135),
            ),
        ),
    ).associateBy { it.name }

    override fun all(): List<Dish> = dishes.values.toList()

    override fun find(name: String): Dish? = dishes[name]
}
