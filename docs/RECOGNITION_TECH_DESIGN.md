# ChopCal — AI 识别主流程 技术设计方案（终版）

> 状态：已确认（v1.0）
> 需求依据：`bitefit/docs/RECOGNITION.md` + `recognition-design.svg` + `recognition-dialogs.svg`
> 数据资产：`bitefit/data/*.json`
> 本文是 AI 识别主流程的**技术落地唯一依据**，研发照此实现。

---

## 0. 定位与架构（本地 / 联网划分）

| 环节 | 位置 | 说明 |
|---|---|---|
| 图片识别 | **联网（云）** | Qwen-VL（qwen3-vl-plus），返回候选菜名 + 食材 + 置信度 |
| 菜品库配方匹配 | 本地 | `DishRepository`（`dishes_standard.json`） |
| 食材营养查询 | 本地 | `IngredientRepository`（`ingredients.json`，145 种） |
| 热量/营养计算 | 本地 | `CalculateCalorieUseCase`（配方求和） |
| 做法系数 | 本地 | `CookingMethodRepository`（`cooking_methods.json`，15 种） |
| 营养对照 | 本地 | `这一餐 ÷ daily_target`，纯除法 |
| 记录落库 | 本地 | Room（`meal_records`） |

> **核心原则**：AI 只负责「认菜名 + 拆食材」，热量/营养**一律由菜品库配方计算**（可解释），AI 直接报的热量仅作兜底/交叉校验。

---

## 1. 功能点清单

| # | 功能点 | 类型 | 优先级 |
|---|---|---|---|
| F1 | 首页拍摄弹窗：餐次/仅识别 + 拍照/相册（扫码/文字语音二期置灰） | UI | P0 |
| F2 | 拍摄参照物引导（"放筷子或手掌"）+ 示例图放大 | UI | P0 |
| F3 | 一图多菜（主菜+主食+汤），AI 全部识别 | 识别 | P0 |
| F4 | Qwen-VL 远程识别（候选菜名+食材+置信度，不自由发挥） | 远程 | P0（先 Fake 预留） |
| F5 | 识别中动画 + "正在识别这顿中餐…" + 失败重试 | UI | P0 |
| F6 | 识别中滚动健康小提示（24 条，5s/条，随机起播，3 分类） | 本地 | P0 |
| F7 | 候选菜品：最多 3 个，置信度降序，含热量范围预览 | UI | P0 |
| F8 | "都不是，手动搜菜名" + "重拍" | UI | P0 |
| F9 | 模糊搜索菜品库（名称/别名/拼音），无结果引导拆食材 | 本地 | P0 |
| F10 | 拆食材兜底（AI 预填 + 增删 + 默认油糖） | 兜底 | P1 |
| F11 | 纯手动录入（空白起步，默认油 15g 糖 8g） | 兜底 | P1 |
| F12 | 食材明细：名称+克数+kcal/100g+三大营养，改克数/删(可撤销)/加食材 | UI | P0 |
| F13 | 动态重算（增删改/切分量/切做法 即时重算） | 计算 | P0 |
| F14 | 分量 小0.7/中1.0/大1.3 × 份数 1~10 | 计算 | P0 |
| F15 | 15 种做法（系数 0.85~1.30），默认取菜名做法，油炸走吸油表 | 计算 | P0 |
| F16 | 总热量/三大营养素 = Σ(克数×每100g营养÷100) | 计算 | P0 |
| F17 | 营养对照：这一餐÷每日目标 % + 还剩 kcal | 计算 | P0 |
| F18 | 保存到餐次 vs 仅查询（仅查询入记录列表带标记、不进四餐/当天摄入） | 落库 | P0 |
| F19 | 保存到已有餐次 → 弹窗「添加到该餐(累计)/替换该餐(仅保留本次)/取消」 | 落库 | P0 |
| F20 | 一餐多菜（每菜独立算、顺序排列、继续添加/完成） | 落库 | P0 |
| F21 | 主食/饮品补充（结果页 +添加主食/饮品） | UI | P1 |
| F22 | 菜品库 JSON 加载（dishes_standard.json，目标 100→1000→万级） | 数据 | P0 |
| F23 | 食材库 JSON 加载（ingredients.json 145 种） | 数据 | P0 |
| F24 | 图片本地存储 + 压缩 | 数据 | P0 |
| F25 | 个人中心「清除缓存」+ 自动清理（2 层策略） | 数据 | P1（TODO） |

---

## 2. 状态机与流程

```
INPUT（选餐次/仅识别 + 拍照/相册）
  → RECOGNIZING（识别中动画 + 健康小提示滚动，失败可重试）
  → CANDIDATES（3 候选 + 置信度 + 热量预览）
      ├─ 命中 → ADJUST（食材明细可增删改 + 分量/做法/份数 + 动态重算）
      │          → RESULT（营养预览 + 营养对照 + 保存/仅查询 + 添主食/饮品 + 继续添加）
      │          → （保存到已有餐次）CONFIRM（添加/替换弹窗）
      └─ 都不是 → SEARCH（模糊搜菜品库）→ 命中 → ADJUST
                              └─ 无结果 → MANUAL（拆食材兜底，AI 预填 / 空白起步）
```

**降级 4 层**：① AI 候选 → ② 模糊搜索菜品库 → ③ 拆食材兜底 → ④ 纯手动录入。③④ 共用「食材清单 + 动态重算」组件，区别是③有 AI 预填、④空白起步。

---

## 3. 模块划分

**新增独立模块 `page/recognize/`**（不塞进 Home）：

```
page/recognize/
├── RecognizeContract.kt        # Intent + Effect + UiState（含多菜 DishDraft 列表）
├── RecognizeViewModel.kt
├── RecognizeFlowScreen.kt      # 阶段分发
└── component/
    ├── InputStage.kt           # 餐次/仅识别 + 拍照/相册 + 参照物引导
    ├── RecognizingStage.kt     # 动画 + HealthTipCarousel
    ├── CandidatesStage.kt      # 3 候选 + 都不是/重拍
    ├── AdjustStage.kt          # 食材明细 + 分量/做法/份数
    ├── ResultStage.kt          # 营养预览 + 保存/仅查询 + 添主食/饮品
    ├── SearchDishSheet.kt      # 模糊搜索（弹窗 B）
    ├── EditIngredientDialog.kt # 编辑/添加食材（弹窗 C）
    ├── CookingMethodSheet.kt   # 15 种做法（弹窗 D）
    └── SaveConflictDialog.kt   # 添加/替换（弹窗 E）
```

Home 的 `CaptureSheet` 改为入口转发到本模块（全屏 dialog / route）。`docs/ARCHITECTURE.md` 目录树同步更新。

---

## 4. 领域模型（domain/model）

```kotlin
// ── 食材库（ingredients.json） ──
data class Ingredient(
    val name: String, val category: String,
    val kcalPer100g: Double, val proteinGPer100g: Double,
    val carbsGPer100g: Double, val fatGPer100g: Double,
    val rawOrCooked: String,
)

// ── 菜品配方行（dishes_standard.json） ──
data class RecipeIngredient(val name: String, val role: String,
    val amountTypical: Double, val amountMin: Double, val amountMax: Double)
data class Seasoning(val name: String,
    val amountTypical: Double, val amountMin: Double, val amountMax: Double)

// ── 菜品（成品菜配方） ──
data class Dish(
    val name: String, val category: String, val method: String, // method=做法名
    val aliases: List<Alias> = emptyList(),   // 拼音/别名/俚语/外国名
    val ingredients: List<RecipeIngredient>, val seasonings: List<Seasoning>,
    val oilUptakePct: Double? = null,          // 油炸类才非空
)

// ── 别名（含类型，便于搜索打分） ──
data class Alias(val type: String, val value: String) // type: pinyin/alias/foreign/slang

// ── 已展开的"计算行"（配方行 × 食材库营养） ──
data class RecipeLine(
    val name: String, val role: String,
    val amountG: Double,                       // 用户可改
    val kcalPer100g: Double, val proteinGPer100g: Double,
    val carbsGPer100g: Double, val fatGPer100g: Double,
) { val kcal get() = amountG * kcalPer100g / 100; /* protein/carbs/fat 同理 */ }

// ── AI 候选 ──
data class DishCandidate(
    val name: String, val nameEn: String?, val confidence: Int,
    val matchedDish: Dish?,                    // 在库 → 直接拿配方
    val aiIngredients: List<String>,           // AI 拆的食材（兜底用）
)

// ── 做法（enum 固化，基准「家常炒」=1.0） ──
// 覆盖菜品库实际出现的 16 种做法 + 白灼；「烘焙/焗」菜品库出现但 cooking_methods.json 未收录，
// 系数暂定（1.15 / 1.00），需数据侧回填确认。
enum class CookingMethod(val label: String, val coefficient: Double) {
    BLANCH("白灼",0.85), COLD_DRESS("凉拌",0.85), STEAM("蒸",0.90), BOIL("煮",0.90),
    STEW("炖",0.95), STIR_FRY("清炒",0.95), HOME_STYLE("家常炒",1.00), BRAISE("焖",1.00),
    FRESH_MADE("现制",1.00), AU_GRATIN("焗",1.00), RED_BRAISE("红烧",1.10), SWEET_SOUR("糖醋",1.15),
    PANCAKE("烙",1.15), BAKED("烘焙",1.15), PAN_FRY("煎",1.15), DRY_FRY("干煸",1.20), DEEP_FRY("油炸",1.30);
}

// ── 一餐里的一道菜草稿（多菜容器单元） ──
data class DishDraft(
    val id: Long, val dishName: String, val confidence: Int?,
    val method: CookingMethod, val lines: List<RecipeLine>,
    val portion: Portion, val servings: Int, val thumbnailPath: String?,
) { fun nutrients(): NutrientTotal }

data class NutrientTotal(
    val kcalMin: Int, val kcalTypical: Int, val kcalMax: Int,
    val proteinG: Int, val carbsG: Int, val fatG: Int)

// ── 落库记录（扩字段，对齐需求 §11） ──
data class MealRecord(
    val id: Long = 0, val dateEpochDay: Long, val mealType: MealType,
    val dishName: String, val confidence: Int? = null, val method: String? = null,
    val portion: Portion, val cookingMethod: CookingMethod, val servings: Int,
    val ingredientsJson: String? = null,       // 食材明细 JSON（name+amount_g）
    val kcalMin: Int, val kcalMax: Int, val proteinG: Int, val carbsG: Int, val fatG: Int,
    val thumbnailPath: String? = null,         // 本地图片相对路径
    val createdAt: Long = System.currentTimeMillis(),
)

// ── 餐次（新增 QUERY_ONLY） ──
enum class MealType(@StringRes val labelRes: Int) {
    BREAKFAST, LUNCH, SNACK, DINNER,
    QUERY_ONLY(R.string.capture_query_only);

    val isRealMeal: Boolean get() = this != QUERY_ONLY
    val isQueryOnly: Boolean get() = this == QUERY_ONLY
    companion object {
        val REAL_MEALS: List<MealType> = entries.filter { it != QUERY_ONLY }
        fun fromTime(hour: Int, minute: Int): MealType { /* 仍只映射到四餐 */ }
    }
}
```

> **`QUERY_ONLY` 是「仅查询」的唯一事实来源**，不额外冗余 `isQueryOnly` 字段，避免两处不一致。
> 需排查所有 `MealType.entries` 遍历处（四餐圈、餐次 tab、`fromTime`），统一改用 `REAL_MEALS` 过滤。

---

## 5. 数据层

### 5.1 JSON 资源落地
`bitefit/data/` 下 5 个文件拷贝到 `app/src/main/assets/data/`（`dishes_standard.json` / `ingredients.json` / `cooking_methods.json` / `health_tips.json` / `oil_uptake.json`），用 **kotlinx.serialization** 解析。

```
data/dto/                    DishesStandardDto.kt / IngredientsDto.kt /
                             CookingMethodsDto.kt / HealthTipsDto.kt / OilUptakeDto.kt
data/datasource/local/       DishJsonDataSource.kt / IngredientJsonDataSource.kt /
                             CookingMethodJsonDataSource.kt / HealthTipJsonDataSource.kt
data/repository/             DishRepositoryImpl(重写) / IngredientRepositoryImpl /
                             CookingMethodRepositoryImpl / HealthTipRepositoryImpl
```

`DishRepository` 接口（支持搜索 + 匹配）：
```kotlin
interface DishRepository {
    fun all(): List<Dish>
    fun find(name: String): Dish?                        // 精确
    fun search(query: String): List<Dish>                // 模糊（name + aliases）
    fun matches(name: String): Dish?                     // AI 候选名 → 配方（先精确后模糊取最高分）
}
```

- **aliases 结构**：`dishes_standard.json` 补充 `aliases` 字段，为**带类型的对象数组**（多值，含拼音/俗语/俚语/别名/外国名）：
  ```json
  "aliases": [
    { "type": "pinyin",  "value": "gong bao ji ding" },
    { "type": "pinyin",  "value": "gbjd" },
    { "type": "alias",   "value": "宫保鸡" },
    { "type": "foreign", "value": "Kung Pao Chicken" }
  ]
  ```
- **模糊匹配**：启动时构建倒排索引（name + aliases 全量），搜索时归一化（小写、去空格、全角转半角）后「包含匹配 + 编辑距离打分」；`type` 用于打分权重（精确 name 最高，pinyin/slang 次之）。

### 5.2 图片本地存储（含压缩）
- 识别前：拍照/相册 URI → `ImageProcessor.compress()`（长边 1280px、JPEG q80、EXIF 归一化）→ 写入 `filesDir/meal_images/` → 拿**相对路径**走识别与落库。
- 落库存 `thumbnailPath`（相对路径，不存绝对路径/URI，避免目录迁移失效）。
- 相机/相册抽象层（上层不变、底层可换）：
  ```kotlin
  interface ImageSource {
      suspend fun capture(): ImageCaptureResult   // 拍照
      suspend fun pick(): ImageCaptureResult      // 相册
  }
  data class ImageCaptureResult(val uri: String, val mimeType: String,
      val sizeBytes: Long, val width: Int, val height: Int)
  // impl：CameraXImageSource（CameraX）/ PhotoPickerImageSource（系统 Photo Picker）
  // 换 SDK 只换 impl + DI 绑定，ViewModel/UI 零改动
  ```

### 5.3 Room 迁移
`MealRecordEntity` 扩字段（confidence/method/portion/cookingMethod/servings/ingredientsJson/kcalMin/kcalMax/thumbnailPath），`ChopDatabase.version 2→3` + `Migration_2_3`（新增列带默认值）。`ingredientsJson` 存纯文本 JSON。

---

## 6. 识别链路（AI 预留，逐步接入）

```kotlin
// domain（业务层只依赖接口）
interface DishRecognizer {
    suspend fun recognize(imageUri: String?): List<DishCandidate>
}
```

**接入策略（确认）**：**先用 `FakeDishRecognizer` 把整个流程跑通、端到端测试**，再逐步接入真实 Qwen-VL。

- **阶段 A（当前）**：`FakeDishRecognizer` 从菜品库返回候选，保证 INPUT→RECOGNIZING→CANDIDATES→ADJUST→RESULT 全链路可测。
- **阶段 B（预留）**：`RecognizeRemoteSource` 真实现（Retrofit + kotlinx-serialization converter + `QwenApi`），`RepositoryModule` 里 `@Binds` 一行切换。
- **接入顺序**（一步步）：① 候选菜名识别（base64 内联）→ ② 匹配菜品库拿配方 → ③ AI 拆食材（兜底预填）→ ④ 与 Fake 双跑交叉校验 → ⑤ 灰度切换。
- 图片以 **base64 内联**方式喂给 Qwen（识别链路不依赖 OSS），先压缩到 150~250KB 再 base64。

---

## 7. 计算引擎（配方求和，可解释）

```kotlin
fun calculate(draft: DishDraft): NutrientTotal {
    // 1) 逐行求和：kcal/protein/carbs/fat = Σ(amountG × per100g / 100)
    // 2) 范围：min 用 amountMin、typical 用 amountTypical、max 用 amountMax
    // 3) 做法系数：method.coefficient（家常炒=1.0）
    // 4) 油炸：method==油炸 且 oilUptakePct!=null → 额外油 kcal = Σ主辅料生重 × oilUptakePct% × 9 kcal/g（计入脂肪）
    // 5) 分量 × 份数：× portion.factor × servings
}
```

- **min/max 方案（确认替换）**：范围来自配方克数 `amount_min/amount_max` 求和，**不再用 ×0.8/×1.35**。
- **动态重算**：`RecipeLine.amountG` 可变，任何改动即时重跑；UI 数字变化加"已更新"高亮。
- **营养对照**：`NutrientComparatorUseCase`：`这一餐 kcal ÷ daily_target.daily_kcal`（纯本地除法）。

---

## 8. 落库（多菜 / 仅查询 / 添加替换）

`SaveMealRecordsUseCase`（替代单条 `SaveMealRecordUseCase`）：

```
saveDrafts(dateEpochDay, mealType, drafts: List<DishDraft>, mode: AddOrReplace)
  1. mealType==QUERY_ONLY → 逐条 insert（mealType=QUERY_ONLY），不触发冲突判断
  2. 真实餐次：
     - 查该(date, mealType)现有记录
     - 无 → 逐条 insert
     - 有 → SaveConflictDialog：
         添加到餐：逐条 insert（累计）
         替换餐：先 delete 该餐全部再 insert（仅本次）
         取消：不保存
  3. 保存后四餐圈/环形/营养/周小结 依赖 Flow 自动刷新
```

- **汇总过滤**：`GetDailySummaryUseCase` 汇总时 `filter { it.mealType.isRealMeal }`，仅查询记录进记录列表但不进四餐/环形/当天摄入。
- **多菜**：`drafts` 为容器，`继续添加` → 追加 draft → 合计 Σ。
- **删除不弹窗**：删食材仅从 draft.lines 移除 + undo 栈。

---

## 9. UI 组件清单（对照设计稿）

| 组件 | 来源 | 说明 |
|---|---|---|
| `InputStage` | 设计屏 1 | 餐次/仅识别 + 拍照/相册 + 参照物引导 + 示例图放大 |
| `RecognizingStage` + `HealthTipCarousel` | 屏 2 | 5s 循环 + 随机起播（`LaunchedEffect`+`delay(5000)`） |
| `CandidatesStage` | 屏 3 | 3 候选 + 置信度 + 热量范围预览 + 都不是/重拍 |
| `SearchDishSheet` | 弹窗 B | 模糊搜索 + "没找到→手动拆食材" |
| `AdjustStage` | 屏 4 | 食材明细行（名称/克数/kcal+三大营养/编辑/删除）+ 加食材 + 分量 + 做法 + 份数 |
| `EditIngredientDialog` | 弹窗 C | 换食材(搜食材库 145) + 改克数 + 营养参考 + 保存并重算 |
| `CookingMethodSheet` | 弹窗 D | 15 种做法 grid + 系数提示，选中即重算 |
| `ResultStage` | 屏 5 | 范围热量 + 三大营养 + 占目标% + 剩余 + 食材依据 + 添主食/饮品 + 继续添加 + 保存/仅查询 |
| `SaveConflictDialog` | 弹窗 E | 添加到餐/替换餐/取消 + 合计预览 |
| 删除撤销 Snackbar | §5.2 | "已删除，点此撤销" |

---

## 10. 图片存储与清理策略（TODO）

### 10.1 本地存储
- 独立文件夹 `filesDir/meal_images/`（非 cacheDir，避免被系统自动回收）。
- 识别前先压缩（见 §5.2），一张照片 ~150~250KB。
- `MealRecord.thumbnailPath` 存相对路径。

### 10.2 个人中心「清除缓存」（TODO，P1）
- 入口：个人中心新增「清除缓存」按钮，展示当前图片占用大小。
- **告知用户**：明确说明会清理哪些图片 + 对应数据库内容。
- 用户选择清理范围（阈值）：**7 天前 / 1 个月前 / 半年前** 的图片 + 数据库记录（删除记录连带删图）。

### 10.3 自动清理（2 层策略，TODO，P1）
| 层 | 触发时机 | 判定 |
|---|---|---|
| 层 1：内存剩余量 | 本地拍照 / AI 识别时（识别前已落盘一份图，此处顺带检测） | 剩余存储 < 用户设定阈值（默认如 20%）→ 触发清理 |
| 层 2：存入时间 | 每次打开 App | 清理「两年前」的数据（图片 + 记录） |

- **用户可配置**（DataStore 存）：内存剩余百分比阈值 + 自动清理间隔（"每隔多久清理一次"）。
- 实现要点：
  1. `StorageCleaner`（domain）统一清理逻辑：按 `dateEpochDay` 阈值删记录 + 按路径删图 + 事务保证一致。
  2. **孤儿文件回收**：定期比对 `meal_images/` 与 DB 引用，清理无引用的图片（防止记录删了图还在）。
  3. 清理前提示 + 可撤销（MVP 可先硬删 + 确认弹窗，不做回收站）。
  4. 自动清理仅动"超阈值"数据，不影响近 7 天/当前周统计展示。

---

## 11. Key 保护（TODO，走完流程后完整讨论）

**已定方向（确认）**：key **绝不下发客户端**，走自有后端（BFF）代理；客户端只持有自家后端的短期凭证。

```
App ──(JWT/登录态)──▶ 自有后端(BFF) ──(真 key, 仅服务端)──▶ Qwen-VL
```

**预留待办（完整方案走完主流程后细化）**：设备绑定（Firebase App Check / Play Integrity）、请求签名 + 防重放、uid/device/IP 三级配额、WAF 风控、多 key 池轮换、KMS 注入、账单异常告警、审计日志定位、泄漏后轮换预案等。

> MVP 阶段先不实现，仅在后端接口处预留「鉴权中间件 + 配额字段」两个扩展点。

---

## 12. 日志 / 埋点 / 主题（对齐 AGENTS.md）

- **日志**：`ComposeLog.v/d/i/w/e`（框架）或业务 `Logger.w/e`（需 Crashlytics 面包屑/非致命时，如识别失败、JSON 解析异常）；禁止 `android.util.Log.*`。
- **埋点**：事件名集中在 `AnalyticsEvents` 新增 `Recognize` 段（`recognize_start/success/fail`、`candidate_selected`、`dish_searched`、`ingredient_edited`、`dish_saved` 等），参数 key 集中（`confidence`/`candidate_index`/`dish_name`/`meal_type`/`is_query_only`/`add_or_replace`）；页面级 `TrackedScreen` 包装。
- **主题**：颜色全走 `chopCalColors`（主绿 `#618A37`、浅鼠尾草 `#D5D8B2`），禁止硬编码。

---

## 13. 依赖与配置

- 网络（阶段 B 才引入）：`retrofit 2.11.0` + `okhttp 4.12.0` + **kotlinx-serialization converter**（不用 Gson）。
- 图片：`coil3` 加载缩略图；相机用 CameraX、相册用系统 Photo Picker。
- 序列化：`kotlinx.serialization.json`（已有）。
- Room：版本 2→3 迁移。

---

## 14. 实施里程碑（AI 逐步接入）

1. **M1 数据地基**：JSON 拷贝 → DTO/DataSource/Repository（菜品+食材+做法+提示）→ 重写 `DishRepositoryImpl`（JSON + search + aliases）→ 单测解析/匹配。
2. **M2 计算引擎**：重写 `CalculateCalorieUseCase`（配方求和 + 15 做法 + 油炸吸油 + 分量/份数 + min/max 范围）→ 单测。
3. **M3 状态机 + UI**：新增 `page/recognize/` 模块 → 各阶段组件 + 健康提示 + 搜索/编辑/做法/冲突弹窗。
4. **M4 落库**：`MealRecordEntity` 迁移 v3 → `SaveMealRecordsUseCase`（多菜/仅查询 QUERY_ONLY/添加替换）→ 记录列表仅查询标记。
5. **M5 图片存储**：压缩落盘 + 缩略图展示（清除缓存/自动清理按 TODO P1 延后）。
6. **M6 端到端测试**：Fake 跑通全流程（含降级 4 层）。
7. **M7 接入 AI（逐步）**：后端代理 + `RecognizeRemoteSource` → 候选识别 → 配方匹配 → 拆食材 → 双跑校验 → 灰度。
8. **M8 打磨**：埋点/日志/主题、撤销、参照物引导、主食/饮品补充、清除缓存、Key 保护（完整讨论后）。

---

## 15. TODO 清单（汇总）

- [ ] **TODO-AI**：AI 识别预留，Fake 先行 + `RecognizeRemoteSource` 逐步接入（§6）。
- [ ] **TODO-IMG-CLEAN**：个人中心「清除缓存」+ 自动清理 2 层策略（§10.2 / §10.3）。
- [ ] **TODO-KEY**：Key 保护完整方案，走完主流程后讨论（§11）。
- [ ] **TODO-DATA**：`dishes_standard.json` 补充 `aliases` 字段（拼音/别名/俚语/外国名，§5.1）。
