package com.chopcal.app.data.repository

import com.chopcal.app.data.TestAssets
import com.chopcal.app.data.datasource.local.DishJsonDataSource
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DishRepositoryTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val repo = DishRepositoryImpl(DishJsonDataSource(TestAssets, json))

    @Test
    fun `parses 500 dishes`() {
        assertEquals(500, repo.all().size)
    }

    @Test
    fun `find by exact name returns full recipe`() {
        val dish = repo.find("宫保鸡丁")
        assertNotNull(dish)
        assertEquals("炒菜", dish!!.category)
        assertEquals("家常炒", dish.method)
        assertTrue(dish.ingredients.isNotEmpty())
        assertTrue(dish.seasonings.isNotEmpty())
    }

    @Test
    fun `search by partial name`() {
        val results = repo.search("宫保")
        assertTrue(results.any { it.name == "宫保鸡丁" })
    }

    @Test
    fun `search by pinyin full`() {
        val results = repo.search("gongbao")
        assertTrue(results.any { it.name == "宫保鸡丁" })
    }

    @Test
    fun `search by pinyin initials`() {
        val results = repo.search("gbjd")
        assertTrue(results.any { it.name == "宫保鸡丁" })
    }

    @Test
    fun `search by english name`() {
        val results = repo.search("kung pao")
        assertTrue(results.any { it.name == "宫保鸡丁" })
    }

    @Test
    fun `search returns exact match first`() {
        val results = repo.search("清炒虾仁")
        assertEquals("清炒虾仁", results.first().name)
    }

    @Test
    fun `matches resolves candidate name`() {
        assertNotNull(repo.matches("宫保鸡丁"))
    }

    @Test
    fun `every dish has ingredients or seasonings`() {
        assertTrue(repo.all().all { it.ingredients.isNotEmpty() || it.seasonings.isNotEmpty() })
    }

    @Test
    fun `method maps to cooking method enum`() {
        val dish = repo.find("清蒸鲈鱼")!!
        assertEquals("蒸", dish.cookingMethod.label)
    }
}
