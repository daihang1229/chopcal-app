package com.chopcal.app.page.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.chopcal.app.MainActivity
import com.chopcal.app.core.analytics.AnalyticsEvents
import com.chopcal.app.core.analytics.ScreenTracker
import com.chopcal.app.core.design.component.TrackedScreen
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.core.design.theme.resolveDarkTheme
import com.chopcal.app.domain.model.ThemeMode
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.page.privacy.PrivacyScreen
import com.rainbow.compose.core.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

private const val SPLASH_DURATION_MS = 1200L

/**
 * 引导页 Activity（LAUNCHER）。
 *
 * 启动后先展示品牌闪屏（icon + slogan），再交给 ViewModel 判断：
 * 已引导 → 直达 MainActivity，否则进入引导流程。
 */
@AndroidEntryPoint
class OnboardingActivity : BaseComposeActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setComposeContent {
            val themeMode by userProfileRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            ChopCalTheme(darkTheme = resolveDarkTheme(themeMode)) {
                var showSplash by remember { mutableStateOf(true) }
                var showPrivacy by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(SPLASH_DURATION_MS)
                    showSplash = false
                }

                if (showPrivacy) {
                    PrivacyScreen(onBack = { showPrivacy = false })
                } else {
                    Crossfade(targetState = showSplash, label = "splash") { splash ->
                        if (splash) {
                            TrackedScreen(AnalyticsEvents.Screen.SPLASH, AnalyticsEvents.SCREEN_CLASS_ONBOARDING, screenTracker) {
                                SplashScreen()
                            }
                        } else {
                            TrackedScreen(AnalyticsEvents.Screen.ONBOARDING, AnalyticsEvents.SCREEN_CLASS_ONBOARDING, screenTracker) {
                                OnboardingScreen(
                                    viewModel = viewModel,
                                    onGoHome = {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    },
                                    onOpenPrivacy = { showPrivacy = true },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
