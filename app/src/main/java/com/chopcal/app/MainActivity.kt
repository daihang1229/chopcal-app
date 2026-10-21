package com.chopcal.app

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.core.design.theme.resolveDarkTheme
import com.chopcal.app.domain.model.ThemeMode
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.page.home.HomeScreen
import com.chopcal.app.page.profile.ProfileScreen
import com.chopcal.app.page.record.RecordScreen
import com.rainbow.compose.core.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val EXIT_DOUBLE_PRESS_INTERVAL_MS = 300L

/**
 * ChopCal 主页 Activity，承载底部三 Tab（预埋）。
 */
@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    private var lastBackPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 双击返回退出：第一次提示，300ms 内再次点击退出
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
                MainScreen()
            }
        }
    }
}

private data class TabItem(
    val titleRes: Int,
    val icon: ImageVector,
)

private val tabs = listOf(
    TabItem(R.string.tab_camera, Icons.Filled.PhotoCamera),
    TabItem(R.string.tab_history, Icons.AutoMirrored.Filled.List),
    TabItem(R.string.tab_profile, Icons.Filled.Person),
)

@Composable
private fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = stringResource(tab.titleRes)) },
                        label = { Text(stringResource(tab.titleRes)) },
                    )
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> RecordScreen()
                2 -> ProfileScreen()
            }
        }
    }
}
