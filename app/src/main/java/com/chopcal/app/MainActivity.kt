package com.chopcal.app

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chopcal.app.R
import com.chopcal.app.core.analytics.AnalyticsEvents
import com.chopcal.app.core.analytics.ScreenTracker
import com.chopcal.app.core.design.component.TrackedScreen
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.core.design.theme.resolveDarkTheme
import com.chopcal.app.domain.model.CaptureMealTarget
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.model.ThemeMode
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.page.home.HomeScreen
import com.chopcal.app.page.home.HomeViewModel
import com.chopcal.app.page.privacy.PrivacyScreen
import com.chopcal.app.page.pro.ProScreen
import com.chopcal.app.page.profile.ProfileScreen
import com.chopcal.app.page.progress.ProgressScreen
import com.chopcal.app.page.progress.ProgressViewModel
import com.chopcal.app.page.recognize.RecognizeFlowScreen
import com.chopcal.app.page.recognize.RecognizeViewModel
import com.chopcal.app.page.record.RecordScreen
import com.rainbow.compose.core.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * ChopCal 主页 Activity（单 Activity + Navigation Compose）。
 */
@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var screenTracker: ScreenTracker

    private val homeViewModel: HomeViewModel by viewModels()
    private val progressViewModel: ProgressViewModel by viewModels()
    private val recognizeViewModel: RecognizeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setComposeContent {
            val themeMode by userProfileRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            ChopCalTheme(darkTheme = resolveDarkTheme(themeMode)) {
                MainScreen(homeViewModel, progressViewModel, recognizeViewModel, screenTracker)
            }
        }
    }
}

private const val EXIT_DOUBLE_PRESS_INTERVAL_MS = 2000L

/** 导航路由。 */
private object Route {
    const val HOME = "home"
    const val RECORD = "record"
    const val PROFILE = "profile"
    const val PRO = "pro"
    const val PROGRESS = "progress"
    const val PRIVACY = "privacy"
    const val RECOGNIZE = "recognize/{date}/{target}?uri={uri}"
    fun recognize(date: Long, target: CaptureMealTarget, uri: String): String =
        "recognize/$date/${target.encode()}?uri=${Uri.encode(uri)}"
}

/** 餐次目标编码到 route 参数。 */
private fun CaptureMealTarget.encode(): String = when (this) {
    is CaptureMealTarget.Meal -> mealType.name.lowercase()
    CaptureMealTarget.QueryOnly -> "query_only"
}

/** route 参数解码为餐次目标。 */
private fun String.decodeTarget(): CaptureMealTarget = when (this) {
    "query_only" -> CaptureMealTarget.QueryOnly
    else -> runCatching { CaptureMealTarget.Meal(MealType.valueOf(uppercase())) }
        .getOrDefault(CaptureMealTarget.QueryOnly)
}

@Composable
private fun MainScreen(
    homeViewModel: HomeViewModel,
    progressViewModel: ProgressViewModel,
    recognizeViewModel: RecognizeViewModel,
    screenTracker: ScreenTracker,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isHome = backStackEntry?.destination?.route == Route.HOME

    // 首页双击返回退出，二级页由 Navigation 处理返回
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableStateOf(0L) }
    BackHandler(enabled = isHome) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastBackPressTime <= EXIT_DOUBLE_PRESS_INTERVAL_MS) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = now
            Toast.makeText(context, context.getString(R.string.exit_hint), Toast.LENGTH_SHORT).show()
        }
    }

    NavHost(navController = navController, startDestination = Route.HOME) {
        composable(Route.HOME) {
            TrackedScreen(AnalyticsEvents.Screen.HOME, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onOpenRecognize = { target, dateEpochDay, uri ->
                        navController.navigate(Route.recognize(dateEpochDay, target, uri))
                    },
                    onOpenProfile = { navController.navigate(Route.PROFILE) },
                    onOpenPro = { navController.navigate(Route.PRO) },
                    onOpenRecord = { navController.navigate(Route.RECORD) },
                    onOpenProgress = { navController.navigate(Route.PROGRESS) },
                )
            }
        }
        composable(Route.RECORD) {
            TrackedScreen(AnalyticsEvents.Screen.RECORD, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
                RecordScreen(onBack = { navController.popBackStack() })
            }
        }
        composable(Route.PROFILE) {
            TrackedScreen(AnalyticsEvents.Screen.PROFILE, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onOpenPrivacy = { navController.navigate(Route.PRIVACY) },
                )
            }
        }
        composable(Route.PRO) {
            TrackedScreen(AnalyticsEvents.Screen.PRO, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
                ProScreen(onBack = { navController.popBackStack() })
            }
        }
        composable(Route.PROGRESS) {
            TrackedScreen(AnalyticsEvents.Screen.PROGRESS, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
                ProgressScreen(
                    viewModel = progressViewModel,
                    onBack = { navController.popBackStack() },
                    onOpenRecord = { navController.navigate(Route.RECORD) },
                )
            }
        }
        composable(Route.PRIVACY) {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Route.RECOGNIZE,
            arguments = listOf(
                navArgument("date") { type = NavType.LongType },
                navArgument("target") { type = NavType.StringType },
                navArgument("uri") { type = NavType.StringType; nullable = true; defaultValue = "" },
            ),
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getLong("date") ?: 0L
            val target = backStackEntry.arguments?.getString("target")?.decodeTarget()
                ?: CaptureMealTarget.QueryOnly
            val uri = backStackEntry.arguments?.getString("uri").orEmpty()
            TrackedScreen(AnalyticsEvents.Screen.RECOGNIZE, AnalyticsEvents.SCREEN_CLASS_MAIN, screenTracker) {
                RecognizeFlowScreen(
                    dateEpochDay = date,
                    target = target,
                    imageUri = uri.ifBlank { null },
                    viewModel = recognizeViewModel,
                    onDismiss = { navController.popBackStack() },
                )
            }
        }
    }
}
