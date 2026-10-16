# ChopCal 项目目录结构设计

> 状态：已确认（feature 改为 page）
> 架构：分层 + Compose MVI

## 分层

```
com.chopcal.app/
├── core/          # 核心基础设施（应用级，不依赖业务）
├── data/          # 数据层（DTO、数据源、Repository 实现、数据库）
├── domain/        # 领域层（Entity、UseCase、Repository 接口）
└── page/          # 功能页面（每个页面一个包，Compose MVI）
```

## 详细目录树

```
com.chopcal.app/
│
├── ChopCalApplication.kt              # @HiltAndroidApp
│
├── core/                              # ── 核心基础设施 ──
│   ├── design/                        # 设计系统
│   │   ├── theme/                     #   Theme.kt / Color.kt / Type.kt
│   │   └── component/                 #   ChopButton、StatusOverlay 等通用组件
│   ├── router/
│   │   └── RoutePathIndex.kt          #   路由常量统一注册
│   ├── util/
│   │   ├── Logger.kt                  #   统一日志封装
│   │   └── Extensions.kt              #   通用扩展
│   └── base/                          # 基础库未覆盖时，这里兜底
│
├── data/                              # ── 数据层 ──
│   ├── dto/                           # 网络 DTO（AI 识别响应等）
│   │   ├── RecognizeResponse.kt
│   │   └── FoodDto.kt
│   ├── datasource/
│   │   ├── local/                     # 本地数据源
│   │   │   ├── DishDataSource.kt      #   配方库（500 道 JSON）
│   │   │   ├── IngredientDataSource.kt#   食材库（145 种 JSON）
│   │   │   └── RecordDataSource.kt    #   饮食记录（Room/DataStore）
│   │   └── remote/
│   │       └── RecognizeRemoteSource.kt # AI 识别 API（Qwen-VL）
│   ├── repository/
│   │   ├── DishRepositoryImpl.kt
│   │   ├── IngredientRepositoryImpl.kt
│   │   └── RecordRepositoryImpl.kt
│   └── db/                            # Room 数据库
│       ├── ChopDatabase.kt
│       ├── entity/                    #   MealRecordEntity
│       └── dao/                       #   MealRecordDao
│
├── domain/                            # ── 领域层 ──
│   ├── model/                         # Entity（跨层用，DTO 不进来）
│   │   ├── Dish.kt                    #   菜品（name + 食材 + 克数范围）
│   │   ├── Ingredient.kt              #   食材（kcal/100g）
│   │   ├── CalorieResult.kt           #   热量结果（典型值 + 范围）
│   │   └── MealRecord.kt              #   饮食记录
│   ├── usecase/
│   │   ├── RecognizeDishUseCase.kt    #   拍照 → 识别菜品
│   │   ├── CalculateCalorieUseCase.kt #   配方 → 热量计算
│   │   ├── SearchDishUseCase.kt       #   查配方库
│   │   └── SaveMealRecordUseCase.kt   #   保存记录
│   └── repository/                    # Repository 接口
│       ├── DishRepository.kt
│       └── RecordRepository.kt
│
└── page/                              # ── 功能页面（Compose MVI）──
    ├── home/                          # ① 首页（拍照入口）
    │   ├── HomeActivity.kt
    │   ├── HomeContract.kt            #   Intent + Effect + UiState
    │   ├── HomeViewModel.kt
    │   ├── HomeScreen.kt              #   Screen + Content
    │   ├── HomePreview.kt
    │   └── mapper/HomeMapper.kt
    │
    ├── result/                        # ② 热量结果页
    │   ├── ResultActivity.kt
    │   ├── ResultContract.kt
    │   ├── ResultViewModel.kt
    │   ├── ResultScreen.kt
    │   ├── ResultPreview.kt
    │   └── mapper/ResultMapper.kt
    │
    ├── record/                        # ③ 饮食记录页
    │   ├── RecordActivity.kt
    │   ├── RecordContract.kt
    │   ├── RecordViewModel.kt
    │   ├── RecordScreen.kt
    │   ├── RecordPreview.kt
    │   └── mapper/RecordMapper.kt
    │
    ├── history/                       # ④ 历史/统计页
    │   ├── HistoryActivity.kt
    │   ├── HistoryContract.kt
    │   ├── HistoryViewModel.kt
    │   ├── HistoryScreen.kt
    │   ├── HistoryPreview.kt
    │   └── mapper/HistoryMapper.kt
    │
    └── setting/                       # ⑤ 设置页
        ├── SettingActivity.kt
        ├── SettingContract.kt
        ├── SettingViewModel.kt
        ├── SettingScreen.kt
        ├── SettingPreview.kt
        └── mapper/SettingMapper.kt
```

## 设计说明

1. **每个 page 6+1 文件**：Activity + Contract（Intent/Effect/UiState）+ ViewModel + Screen（Screen/Content 双函数）+ Preview + Mapper。
2. **Route 常量**统一放 `core/router/RoutePathIndex.kt`，Activity 注册在各自 AndroidManifest。
3. **数据层分离本地/远程**：本地 = 配方库（500 道）+ 食材库（145 种）JSON；远程 = Qwen-VL 识别 API。
4. **domain 层核心**：`CalorieResult`（典型值 + 范围），热量计算规则在 `CalculateCalorieUseCase`。
5. **开发优先级**：先 `home`（拍照）+ `result`（结果）两个 MVP 核心页，再 `record`/`history`/`setting`。
