package com.chopcal.app.data.repository

import com.chopcal.app.data.TestAssets
import com.chopcal.app.data.datasource.local.IngredientJsonDataSource
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IngredientRepositoryTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val repo = IngredientRepositoryImpl(IngredientJsonDataSource(TestAssets, json))

    @Test
    fun `parses 145 ingredients`() {
        assertEquals(145, repo.all().size)
    }

    @Test
    fun `find chicken breast nutrition`() {
        val chicken = repo.find("鸡胸肉")
        assertNotNull(chicken)
        assertEquals(133.0, chicken!!.kcalPer100g, 0.01)
        assertEquals(24.0, chicken.proteinGPer100g, 0.01)
    }

    @Test
    fun `search by substring`() {
        assertTrue(repo.search("鸡").isNotEmpty())
    }
}
