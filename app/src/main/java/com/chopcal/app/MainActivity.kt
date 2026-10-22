package com.chopcal.app

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.chopcal.app.core.analytics.AnalyticsEvents
import com.chopcal.app.core.analytics.ScreenTracker
import com.chopcal.app.core.design.component.TrackedScreen
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.core.design.theme.resolveDarkTheme
import com.chopcal.app.domain.model.ThemeMode
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.page.home.HomeScreen
import com.chopcal.app.page.home.HomeViewModel
import com.chopcal.app.page.privacy.PrivacyScreen
import com.chopcal.app.page.pro.ProScreen
import com.chopcal.app.page.profile.ProfileScreen
import com.chopcal.app.page.progress.ProgressScreen
import com.chopcal.app.page.progress.ProgressViewModel
import com.chopcal.app.page.record.RecordScreen
import com.rainbow.compose.core.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * ChopCal 主页 Activity。
 *
 * 单页结构：默认首页（Home），个人中心 / 记录 / Pro 通过顶部 icon 进入二级页。
 */
@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var screenTracker: ScreenTracker

    private val homeViewModel: HomeViewModel by viewModels()
    private val progressViewModel: ProgressViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setComposeContent {
            val themeMode by userProfileRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            ChopCalTheme(darkTheme = resolveDarkTheme(themeMode)) {
                MainScreen(homeViewModel, progressViewModel, screenTracker)
            }
        }
    }
}

private enum class Destination { HOME, RECORD, PROFILE, PRO, PROGRESS, PRIVACY }

@Composable
private fun MainScreen(
    homeViewModel: HomeViewModel,
    progressViewModel: ProgressViewModel,
    screenTracker: ScreenTracker,
) {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }

    // 二级页返回首页
    BackHandler(enabled = destination != Destination.HOME) {
        destination = Destination.HOME
    }

    when (destination) {
        Destination.HOME -> TrackedScreen(AnalyticsEvents.Screen.HOME, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
            HomeScreen(
                viewModel = homeViewModel,
                onOpenProfile = { destination = Destination.PROFILE },
                onOpenPro = { destination = Destination.PRO },
                onOpenRecord = { destination = Destination.RECORD },
                onOpenProgress = { destination = Destination.PROGRESS },
            )
        }
        Destination.RECORD -> TrackedScreen(AnalyticsEvents.Screen.RECORD, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
            RecordScreen(onBack = { destination = Destination.HOME })
        }
        Destination.PROFILE -> TrackedScreen(AnalyticsEvents.Screen.PROFILE, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
            ProfileScreen(
                onBack = { destination = Destination.HOME },
                onOpenPrivacy = { destination = Destination.PRIVACY },
            )
        }
        Destination.PRO -> TrackedScreen(AnalyticsEvents.Screen.PRO, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
            ProScreen(onBack = { destination = Destination.HOME })
        }
        Destination.PROGRESS -> TrackedScreen(AnalyticsEvents.Screen.PROGRESS, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
            ProgressScreen(
                viewModel = progressViewModel,
                onBack = { destination = Destination.HOME },
                onOpenRecord = { destination = Destination.RECORD },
            )
        }
        Destination.PRIVACY -> PrivacyScreen(onBack = { destination = Destination.PROFILE })
    }
}
