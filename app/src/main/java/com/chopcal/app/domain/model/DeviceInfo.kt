package com.chopcal.app.domain.model

import kotlinx.serialization.Serializable

/**
 * 设备身份信息（匿名，无需登录）。首次启动生成 UUID。
 */
@Serializable
data class DeviceInfo(
    val uuid: String = "",
    val nickname: String = "",
    val avatarId: Int = 0,
    val onboarded: Boolean = false,
    val skipCount: Int = 0,
    val lastSkipAt: Long = 0,
)
