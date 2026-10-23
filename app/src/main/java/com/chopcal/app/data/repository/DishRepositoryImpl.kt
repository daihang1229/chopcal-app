package com.chopcal.app.data.repository

import com.chopcal.app.data.datasource.local.DishJsonDataSource
import com.chopcal.app.data.dto.AliasDto
import com.chopcal.app.data.dto.DishDto
import com.chopcal.app.data.dto.RecipeIngredientDto
import com.chopcal.app.data.dto.SeasoningDto
import com.chopcal.app.domain.model.Alias
import com.chopcal.app.domain.model.Dish
import com.chopcal.app.domain.model.RecipeIngredient
import com.chopcal.app.domain.model.Seasoning
import com.chopcal.app.domain.repository.DishRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 菜品库 JSON 实现（dishes_standard.json，500 道）。
 *
 * 构造时同步加载并建 name → Dish 索引（500 道菜 JSON 解析毫秒级，可接受）。
 * 模糊搜索：归一化（小写 + 去空格）后对 name / aliases 打分排序。
 */
@Singleton
class DishRepositoryImpl @Inject constructor(
    private val dataSource: DishJsonDataSource,
) : DishRepository {

    private val byName: Map<String, Dish> = dataSource.load()
        .map { it.toDomain() }
        .associateBy { it.name }

    override fun all(): List<Dish> = byName.values.toList()

    override fun find(name: String): Dish? = byName[name]

    override fun search(query: String): List<Dish> {
        val q = normalize(query)
        if (q.isEmpty()) return emptyList()
        return all()
            .mapNotNull { dish -> matchScore(dish, q)?.let { dish to it } }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    override fun matches(name: String): Dish? =
        find(name) ?: search(name).firstOrNull()

    /** 归一化：小写 + 去空格（中文无影响，拼音/英文去空格便于全拼/首字母匹配）。 */
    private fun normalize(s: String): String =
        s.lowercase().filter { !it.isWhitespace() }

    /** 打分：精确名 100 > 名包含 80 > 别名/拼音包含 60；无命中返回 null。 */
    private fun matchScore(dish: Dish, q: String): Int? {
        val name = normalize(dish.name)
        return when {
            name == q -> 100
            name.contains(q) -> 80
            dish.aliases.any { normalize(it.value).contains(q) } -> 60
            else -> null
        }
    }

    private fun DishDto.toDomain(): Dish = Dish(
        name = name,
        category = category,
        method = method,
        aliases = aliases.map { it.toDomain() },
        ingredients = ingredients.map { it.toDomain() },
        seasonings = seasonings.map { it.toDomain() },
    )

    private fun AliasDto.toDomain(): Alias = Alias(type = type, value = value)

    private fun RecipeIngredientDto.toDomain(): RecipeIngredient = RecipeIngredient(
        name = name,
        role = role,
        amountTypical = amountTypical,
        amountMin = amountMin,
        amountMax = amountMax,
    )

    private fun SeasoningDto.toDomain(): Seasoning = Seasoning(
        name = name,
        amountTypical = amountTypical,
        amountMin = amountMin,
        amountMax = amountMax,
    )
}
