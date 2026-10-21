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
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.core.design.theme.resolveDarkTheme
import com.chopcal.app.domain.model.ThemeMode
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.page.home.HomeScreen
import com.chopcal.app.page.home.HomeViewModel
import com.chopcal.app.page.pro.ProScreen
import com.chopcal.app.page.profile.ProfileScreen
import com.chopcal.app.page.record.RecordScreen
import com.rainbow.compose.core.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val EXIT_DOUBLE_PRESS_INTERVAL_MS = 2000L

/**
 * ChopCal 主页 Activity。
 *
 * 单页结构：默认首页（Home），个人中心 / 记录 / Pro 通过顶部 icon 进入二级页。
 */
@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    private val homeViewModel: HomeViewModel by viewModels()

    private var lastBackPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 双击返回退出：第一次提示，短时间再次点击退出
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastBackPressTime <= EXIT_DOUBLE_PRESS_INTERVAL_MS) {
                        finish()
                    } else {
                        lastBackPressTime = now
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.exit_hint),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            },
        )

        setComposeContent {
            val themeMode by userProfileRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            ChopCalTheme(darkTheme = resolveDarkTheme(themeMode)) {
                MainScreen(homeViewModel)
            }
        }
    }
}

private enum class Destination { HOME, RECORD, PROFILE, PRO }

@Composable
private fun MainScreen(homeViewModel: HomeViewModel) {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }

    // 二级页返回首页
    BackHandler(enabled = destination != Destination.HOME) {
        destination = Destination.HOME
    }

    when (destination) {
        Destination.HOME -> HomeScreen(
            viewModel = homeViewModel,
            onOpenProfile = { destination = Destination.PROFILE },
            onOpenPro = { destination = Destination.PRO },
            onOpenRecord = { destination = Destination.RECORD },
        )
        Destination.RECORD -> RecordScreen(onBack = { destination = Destination.HOME })
        Destination.PROFILE -> ProfileScreen(onBack = { destination = Destination.HOME })
        Destination.PRO -> ProScreen(onBack = { destination = Destination.HOME })
    }
}
