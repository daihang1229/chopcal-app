package com.chopcal.app.domain.model

import java.lang.Math.floorMod

/**
 * 默认昵称生成器。
 *
 * 规则：形容词 + 名词 + 4 位数字（1000–9999），由设备 ID 确定性派生。
 * 组合空间 ≈ 20×20×9000 = 360 万，卸载重装（同设备）昵称保持一致，且基本不重复。
 */
object NicknameGenerator {

    private val adjectives = listOf(
        "Sunny", "Fresh", "Brave", "Calm", "Golden", "Lucky", "Zesty", "Bold",
        "Kind", "Bright", "Wild", "Cosy", "Swift", "Happy", "Mellow", "Crisp",
        "Gentle", "Spicy", "Jolly", "Eager",
    )

    private val nouns = listOf(
        "Panda", "Mango", "Peach", "Basil", "Lemon", "Ginger", "Olive", "Kiwi",
        "Lotus", "Cashew", "Sesame", "Coconut", "Berry", "Miso", "Noodle", "Papaya",
        "Chestnut", "Clove", "Barley", "Fig",
    )

    fun generate(deviceId: String): String {
        val seed = deviceId.hashCode()
        val adjective = adjectives[floorMod(seed, adjectives.size)]
        val noun = nouns[floorMod(seed / adjectives.size, nouns.size)]
        val number = floorMod(seed, 9000) + 1000
        return "$adjective$noun$number"
    }
}
