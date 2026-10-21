package com.chopcal.app.page.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.chopcal.app.MainActivity
import com.chopcal.app.core.design.theme.ChopCalTheme
import com.rainbow.compose.core.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * 引导页 Activity（LAUNCHER）。
 *
 * 启动后由 ViewModel 判断 onboarded：已引导 → 直达 MainActivity，否则进入引导流程。
 */
@AndroidEntryPoint
class OnboardingActivity : BaseComposeActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setComposeContent {
            ChopCalTheme {
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
