package com.chopcal.app

import android.app.Application
import android.util.Log
import com.chopcal.app.core.analytics.AnalyticsTracker
import com.chopcal.app.core.analytics.CrashReporter
import com.chopcal.app.domain.repository.UserProfileRepository
import com.rainbow.compose.core.log.ComposeLog
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 应用入口，Hilt 注入根。
 * 在 onCreate 提前初始化设备信息（device UUID），尽早绑定到埋点与崩溃上报。
 */
@HiltAndroidApp
class ChopCalApplication : Application() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var crashReporter: CrashReporter

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        initUserInfo()
    }

    /** 提前确保 device UUID 生成，并同步到 Analytics / Crashlytics 的用户标识。 */
    private fun initUserInfo() {
        applicationScope.launch {
            runCatching {
                val device = userProfileRepository.ensureDevice()
                analyticsTracker.setUserId(device.uuid)
                crashReporter.setUserId(device.uuid)
                ComposeLog.e("uuid",device.uuid)
            }
        }
    }
}
