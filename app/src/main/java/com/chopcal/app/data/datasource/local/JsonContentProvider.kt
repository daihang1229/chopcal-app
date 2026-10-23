package com.chopcal.app.data.datasource.local

/**
 * 读取本地 JSON 文本。抽象出来便于单元测试替换（真实现读 assets，测试读文件系统）。
 */
interface JsonContentProvider {
    fun read(path: String): String
}
