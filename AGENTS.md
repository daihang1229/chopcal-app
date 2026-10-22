# ChopCal 开发规范（Agent 守则）

## 日志

- 所有日志输出必须使用 **`ComposeLog`** 工具类（`com.rainbow.compose.core.log.ComposeLog`）：
  `ComposeLog.v/d/i/w/e(...)`。
- **禁止直接使用 `android.util.Log.*`**。
- 需要崩溃上下文（面包屑 / 非致命异常上报）时，使用业务日志类
  `com.chopcal.app.core.log.Logger` 的 `w` / `e` 方法，它会同时写入 Logcat 并同步到 Crashlytics。

## 埋点

- 埋点统一走 `AnalyticsTracker` / `ScreenTracker` 抽象，不要直接调用 `FirebaseAnalytics`。
- 事件名与参数 key 集中在 `com.chopcal.app.core.analytics.AnalyticsEvents` 常量里，禁止散落硬编码。
- 页面级埋点用 `TrackedScreen` 包装页面根 Composable（自动上报 screen_view / screen_duration）。

## 主题颜色

- UI 颜色一律从 `chopCalColors`（`com.chopcal.app.core.design.theme`）取，禁止在组件里硬编码色值，
  以便深浅色与品牌色统一维护（品牌主绿 `#618A37`，浅鼠尾草绿 `#D5D8B2`）。
