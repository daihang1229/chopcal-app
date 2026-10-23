package com.chopcal.app.data.datasource.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 从 assets 读取 JSON 文本（真实现）。
 */
@Singleton
class AssetsJsonContentProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : JsonContentProvider {

    override fun read(path: String): String =
        context.assets.open(path).bufferedReader().use { it.readText() }
}
