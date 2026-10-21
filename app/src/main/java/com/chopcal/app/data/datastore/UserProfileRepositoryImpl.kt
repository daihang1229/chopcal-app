package com.chopcal.app.data.datastore

import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.chopcal.app.domain.model.DailyTarget
import com.chopcal.app.domain.model.DeviceInfo
import com.chopcal.app.domain.model.NicknameGenerator
import com.chopcal.app.domain.model.ThemeMode
import com.chopcal.app.domain.model.UserProfile
import com.chopcal.app.domain.repository.UserProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : UserProfileRepository {

    private object Keys {
        val PROFILE = stringPreferencesKey("user_profile")
        val DAILY_TARGET = stringPreferencesKey("daily_target")
        val DEVICE = stringPreferencesKey("device")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    override val profile: Flow<UserProfile?> = dataStore.data.map { p ->
        p[Keys.PROFILE]?.let { raw -> runCatching { json.decodeFromString<UserProfile>(raw) }.getOrNull() }
    }

    override val device: Flow<DeviceInfo> = dataStore.data.map { p ->
        p[Keys.DEVICE]?.let { raw -> runCatching { json.decodeFromString<DeviceInfo>(raw) }.getOrNull() }
            ?: DeviceInfo()
    }

    override val dailyTarget: Flow<DailyTarget?> = dataStore.data.map { p ->
        p[Keys.DAILY_TARGET]?.let { raw -> runCatching { json.decodeFromString<DailyTarget>(raw) }.getOrNull() }
    }

    override val themeMode: Flow<ThemeMode> = dataStore.data.map { p ->
        p[Keys.THEME_MODE]
            ?.let { raw -> runCatching { ThemeMode.valueOf(raw) }.getOrNull() }
            ?: ThemeMode.SYSTEM
    }

    override suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { it[Keys.PROFILE] = json.encodeToString(profile) }
    }

    override suspend fun saveDailyTarget(target: DailyTarget) {
        dataStore.edit { it[Keys.DAILY_TARGET] = json.encodeToString(target) }
    }

    override suspend fun saveThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override suspend fun markOnboarded() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.DEVICE]
                ?.let { raw -> runCatching { json.decodeFromString<DeviceInfo>(raw) }.getOrNull() }
                ?: DeviceInfo()
            prefs[Keys.DEVICE] = json.encodeToString(current.copy(onboarded = true))
        }
    }

    override suspend fun recordSkip() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.DEVICE]
                ?.let { raw -> runCatching { json.decodeFromString<DeviceInfo>(raw) }.getOrNull() }
                ?: DeviceInfo()
            prefs[Keys.DEVICE] = json.encodeToString(
                current.copy(
                    skipCount = current.skipCount + 1,
                    lastSkipAt = System.currentTimeMillis(),
                )
            )
        }
    }

    override suspend fun ensureDevice(): DeviceInfo {
        val existing = device.first()
        if (existing.uuid.isNotEmpty()) return existing

        val uuid = resolveDeviceId()
        val info = DeviceInfo(
            uuid = uuid,
            nickname = NicknameGenerator.generate(uuid),
            avatarId = 0, // 默认头像，profile 后续再做
            onboarded = false,
        )
        dataStore.edit { it[Keys.DEVICE] = json.encodeToString(info) }
        return info
    }

    /**
     * 设备标识：优先用 ANDROID_ID（同签名包 + 同设备 + 同用户下卸载重装不变），
     * 异常/空值时兜底随机 UUID。
     */
    private fun resolveDeviceId(): String {
        val androidId = runCatching {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull()

        return if (androidId.isNullOrBlank() || androidId == "null") {
            UUID.randomUUID().toString()
        } else {
            androidId
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
