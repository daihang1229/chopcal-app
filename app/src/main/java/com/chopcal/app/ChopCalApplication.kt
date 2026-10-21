package com.chopcal.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用入口，Hilt 注入根。
 */
@HiltAndroidApp
class ChopCalApplication : Application()
