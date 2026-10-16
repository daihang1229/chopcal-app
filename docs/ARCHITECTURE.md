# ChopCal 项目目录结构设计

> 状态：已确认（三 Tab 结构）
> 架构：分层 + Compose MVI + 底部三 Tab

## 分层

```
com.chopcal.app/
├── core/          # 核心基础设施（应用级，不依赖业务）
├── data/          # 数据层（DTO、数据源、Repository 实现、数据库）
├── domain/        # 领域层（Entity、UseCase、Repository 接口）
└── page/          # 功能页面（三个 Tab，每个一个包，Compose MVI）
```

## 详细目录树

```
com.chopcal.app/
│
├── ChopCalApplication.kt              # @HiltAndroidApp
├── MainActivity.kt                    # 底部导航容器（3 Tab）
│
├── core/                              # ── 核心基础设施 ──
│   ├── design/
│   │   ├── theme/                     #   Theme.kt / Color.kt / Type.kt
│   │   └── component/                 #   ChopButton、StatusOverlay 等通用组件
│   ├── router/
│   │   └── RoutePathIndex.kt          #   路由常量
│   ├── util/
│   │   ├── Logger.kt                  #   统一日志
│   │   └── Extensions.kt
│   └── base/                          # 基础库未覆盖时兜底
│
├── data/                              # ── 数据层 ──
│   ├── dto/
│   │   ├── RecognizeResponse.kt       #   AI 识别响应
│   │   └── FoodDto.kt
│   ├── datasource/
│   │   ├── local/
│   │   │   ├── DishDataSource.kt      #   配方库（500 道 JSON）
│   │   │   ├── IngredientDataSource.kt#   食材库（145 种 JSON）
│   │   │   └── RecordDataSource.kt    #   饮食记录
│   │   └── remote/
│   │       └── RecognizeRemoteSource.kt # AI 识别 API
│   ├── repository/
│   │   ├── DishRepositoryImpl.kt
│   │   ├── IngredientRepositoryImpl.kt
│   │   └── RecordRepositoryImpl.kt
│   └── db/
│       ├── ChopDatabase.kt
│       ├── entity/MealRecordEntity.kt
│       └── dao/MealRecordDao.kt
│
├── domain/                            # ── 领域层 ──
│   ├── model/
│   │   ├── Dish.kt                    #   菜品（name + 食材 + 克数范围）
│   │   ├── Ingredient.kt              #   食材（kcal/100g）
│   │   ├── CalorieResult.kt           #   热量结果（典型值 + 范围）
│   │   ├── MealRecord.kt              #   饮食记录
│   │   └── DailyPlan.kt               #   每日摄入计划
│   ├── usecase/
│   │   ├── RecognizeDishUseCase.kt    #   拍照 → 识别菜品
│   │   ├── CalculateCalorieUseCase.kt #   配方 → 热量计算
│   │   ├── SearchDishUseCase.kt       #   查配方库
│   │   ├── SaveMealRecordUseCase.kt   #   保存记录
│   │   └── GetDailySummaryUseCase.kt  #   按天/周/月汇总
│   └── repository/
│       ├── DishRepository.kt
│       └── RecordRepository.kt
│
└── page/                              # ── 三个 Tab（Compose MVI）──
    ├── home/                          # Tab 1：拍照主流程
    │   ├── HomeContract.kt            #   Intent + Effect + UiState
    │   ├── HomeViewModel.kt
    │   ├── HomeScreen.kt              #   拍照 → 识别 → 结果（单流程）
    │   ├── HomePreview.kt
    │   └── mapper/HomeMapper.kt
    │
    ├── record/                        # Tab 2：记录 + 曲线图 + 计划
    │   ├── RecordContract.kt
    │   ├── RecordViewModel.kt
    │   ├── RecordScreen.kt            #   记录列表 + 曲线图 + 计划入口
    │   ├── RecordPreview.kt
    │   └── mapper/RecordMapper.kt
    │
    └── profile/                       # Tab 3：个人中心
        ├── ProfileContract.kt
        ├── ProfileViewModel.kt
        ├── ProfileScreen.kt           #   个人信息 + 设置
        ├── ProfilePreview.kt
        └── mapper/ProfileMapper.kt
```

## 设计说明

1. **三 Tab 结构**：`MainActivity` 承载底部导航，三个 Tab 分别是 `home`（拍照主流程）、`record`（记录+曲线图+计划）、`profile`（个人中心）。
2. **home 是单流程**：拍照 → 识别 → 结果展示，不拆独立结果页。
3. **record 包含三块**：记录列表（默认）、天/周/月曲线图、每日摄入计划制定。
4. **每个 page 6+1 文件**：Contract（Intent/Effect/UiState）+ ViewModel + Screen（Screen/Content）+ Preview + Mapper。
5. **数据层分离本地/远程**：本地 = 配方库 + 食材库 JSON；远程 = Qwen-VL 识别 API。
6. **domain 核心**：`CalorieResult`（典型值+范围）+ `DailyPlan`（每日计划），热量计算在 `CalculateCalorieUseCase`，汇总在 `GetDailySummaryUseCase`。

## 开发顺序

```
阶段1（MVP）：数据落地 → home Tab（拍照识别 + 热量结果）
阶段2：record Tab（记录 + 曲线图 + 计划）
阶段3：profile Tab（个人中心）
```
