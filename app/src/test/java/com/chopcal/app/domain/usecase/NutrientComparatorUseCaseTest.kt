package com.chopcal.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class NutrientComparatorUseCaseTest {

    private val useCase = NutrientComparatorUseCase()

    @Test
    fun `percent and remaining`() {
        val c = useCase(650, 1650)
        assertEquals(39, c.percent)          // 650 * 100 / 1650 = 39.4 → 39
        assertEquals(1000, c.remainingKcal)
    }

    @Test
    fun `zero daily target`() {
        val c = useCase(650, 0)
        assertEquals(0, c.percent)
        assertEquals(0, c.remainingKcal)
    }

    @Test
    fun `over target remaining is zero`() {
        val c = useCase(2000, 1650)
        assertEquals(0, c.remainingKcal)
    }
}
