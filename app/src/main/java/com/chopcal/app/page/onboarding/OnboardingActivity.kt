package com.chopcal.app.page.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.chopcal.app.MainActivity
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.chopcal.app.core.design.theme.resolveDarkTheme
import com.chopcal.app.domain.model.ThemeMode
import com.chopcal.app.domain.repository.UserProfileRepository
import com.rainbow.compose.core.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 引导页 Activity（LAUNCHER）。
 *
 * 启动后由 ViewModel 判断 onboarded：已引导 → 直达 MainActivity，否则进入引导流程。
 */
@AndroidEntryPoint
class OnboardingActivity : BaseComposeActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setComposeContent {
            val themeMode by userProfileRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            ChopCalTheme(darkTheme = resolveDarkTheme(themeMode)) {
                OnboardingScreen(
                    viewModel = viewModel,
                    onGoHome = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                )
            }
        }
    }
}
