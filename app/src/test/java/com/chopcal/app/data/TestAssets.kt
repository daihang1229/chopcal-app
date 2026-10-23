package com.chopcal.app.data

import com.chopcal.app.data.datasource.local.JsonContentProvider
import java.io.File

/**
 * 单元测试用的 JSON 读取器：从 app module 的 assets 目录读文件。
 *
 * Gradle 单测 working directory = app module 根目录，故相对路径 `src/main/assets` 可访问。
 */
object TestAssets : JsonContentProvider {
    override fun read(path: String): String =
        File("src/main/assets", path).readText()
}
