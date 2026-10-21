package com.chopcal.app.domain.repository

import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.DeviceInfo
import com.chopcal.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * 用户档案仓储。MVP 单设备单用户（device UUID），后续接登录可迁移到按 userId 的 Room。
 */
interface UserProfileRepository {
    val profile: Flow<UserProfile?>
    val device: Flow<DeviceInfo>
    val dailyTarget: Flow<DailyTarget?>

    suspend fun saveProfile(profile: UserProfile)
    suspend fun saveDailyTarget(target: DailyTarget)
    suspend fun markOnboarded()
    suspend fun recordSkip()
    suspend fun ensureDevice(): DeviceInfo
    suspend fun clear()
}
