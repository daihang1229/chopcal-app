package com.chopcal.app.domain.usecase

import com.chopcal.app.data.TestAssets
import com.chopcal.app.data.datasource.local.DishJsonDataSource
import com.chopcal.app.data.repository.DishRepositoryImpl
import kotlinx.serialization.json.Json
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchDishUseCaseTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val useCase = SearchDishUseCase(DishRepositoryImpl(DishJsonDataSource(TestAssets, json)))

    @Test
    fun `search by name`() {
        assertTrue(useCase("宫保").any { it.name == "宫保鸡丁" })
    }

    @Test
    fun `search by pinyin`() {
        assertTrue(useCase("gongbao").any { it.name == "宫保鸡丁" })
    }

    @Test
    fun `search by english`() {
        assertTrue(useCase("kung pao").any { it.name == "宫保鸡丁" })
    }

    @Test
    fun `resolve candidate name`() {
        assertNotNull(useCase.resolve("宫保鸡丁"))
    }
}
