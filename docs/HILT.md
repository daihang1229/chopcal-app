# 依赖注入与 Hilt

> 状态：已确认
> 技术栈：Hilt 2.55（基于 Dagger，编译期代码生成，KSP）

本文说明 ChopCal 项目中的依赖注入（DI）与 Hilt 用法，涵盖概念、注解、`@Provides` / `@Binds` 的区别、作用域，以及完整的注入链路。

---

## 1. 什么是依赖注入（Dependency Injection, DI）

**核心思想：一个类不自己创建它依赖的对象，而是由外部"注入"进来。**

### 不用 DI 的问题

```kotlin
class OnboardingViewModel {
    // 自己 new，硬编码依赖
    private val repository = UserProfileRepositoryImpl(dataStore = ..., json = ...)
    private val tracker = FirebaseAnalyticsTracker(...)
}
```

问题：

1. **强耦合**：直接依赖具体实现，想换实现（如测试时换假数据）就得改代码。
2. **难以测试**：无法把依赖替换成 mock。
3. **创建逻辑重复**：每个用 `DataStore` 的地方都要自己写初始化。
4. **生命周期难管理**：单例创建、销毁散落各处。

### DI 的解法

类只声明"我需要什么"，创建和组装交给专门的容器（Injector / Container）。

```kotlin
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository,   // 接口，不是实现
    private val calculateBmi: CalculateBmiUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : BaseMviViewModel<...>()
```

好处：控制反转（IoC）、解耦、可测试、单一职责、对象创建集中管理。

---

## 2. Hilt 是什么

Hilt 是 Google 为 **Android 专门封装的依赖注入框架**，底层基于 **Dagger**。

- **Dagger**：Java 编译期 DI 框架，功能强但配置繁琐（手写 Component、管理生命周期）。
- **Hilt**：把 Dagger 标准化，预定义 Android 组件（Application/Activity/Fragment/ViewModel/Service）的生命周期容器，只需几个注解。

Hilt 用 **编译期代码生成（KSP/APT）**，不是反射。注入代码编译时生成，**运行时零反射开销，依赖缺失编译期直接报错**。

---

## 3. 核心注解

| 注解 | 作用 | 项目位置 |
|------|------|----------|
| `@HiltAndroidApp` | 应用级容器根，触发代码生成，每个 app 唯一 | `ChopCalApplication.kt` |
| `@AndroidEntryPoint` | 标记 Activity/Fragment/Service/View 为注入目标 | `OnboardingActivity.kt` |
| `@HiltViewModel` | ViewModel 注入 + 自动工厂 | `OnboardingViewModel.kt` |
| `@Inject constructor` | 构造注入：声明依赖，由 Hilt 创建传入 | 各 ViewModel / UseCase / 实现类 |
| `@Inject lateinit var` | 字段注入：系统创建的类用 | Activity/Fragment 字段 |
| `@Module` | 声明"怎么创建对象"的模块 | `DataStoreModule` / `RepositoryModule` |
| `@Provides` | 手写创建逻辑 | `DataStoreModule` |
| `@Binds` | 接口 → 实现 的映射 | `RepositoryModule` |
| `@InstallIn` | 绑定放进哪个容器（决定生命周期） | 所有 Module |
| `@Singleton` | 应用级单例作用域 | 全局共享对象 |
| `@ApplicationContext` | 限定符：应用级 Context | `DataStoreModule` |

---

## 4. 三种注入方式

| 方式 | 写法 | 适用场景 | 推荐度 |
|------|------|----------|--------|
| **构造注入** | `class X @Inject constructor(val dep: Y)` | 自己能控制构造的类（ViewModel/Repository/UseCase） | ⭐⭐⭐ 最推荐 |
| **字段注入** | `@Inject lateinit var dep: Y` | 系统创建的类（Activity/Fragment/Service） | ⭐⭐ |
| **方法注入** | `@Inject fun init(dep: Y)` | 后置处理、可选依赖 | ⭐ 少用 |

> 为什么 Activity 用字段注入？因为 Activity 是**安卓系统 `new` 出来的**，Hilt 拦不住构造过程，只能事后填字段，所以用 `lateinit`。

---

## 5. `@Provides` 与 `@Binds`

**判断标准：创建对象需不需要写代码。**

- `@Binds`：对象能靠 `@Inject constructor` 自动创建，只做"接口→实现"映射。
- `@Provides`：对象不能靠 `@Inject constructor` 创建（第三方库/系统类/需配置），亲手写创建逻辑。

### `@Binds`

```kotlin
// RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl   // 参数 = 实现（有 @Inject constructor）
    ): UserProfileRepository              // 返回 = 接口
}
```

特点：抽象方法无方法体，Module 是 `abstract class`，只做映射。

### `@Provides`

```kotlin
// DataStoreModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.appDataStore

    @Provides @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }
}
```

特点：方法有方法体，Module 是 `object`，参数可继续被 Hilt 注入。

### 应用场景对照

| 场景 | 用哪个 | 例子 |
|------|--------|------|
| 自己写的接口 + 有 `@Inject constructor` 的实现 | `@Binds` | `UserProfileRepository → UserProfileRepositoryImpl` |
| 第三方库（Retrofit/OkHttp/Room） | `@Provides` | `OkHttpClient`、`Retrofit` |
| 系统类 / 需要 Context | `@Provides` | `DataStore`、`Context` |
| 需要额外配置的对象 | `@Provides` | `Json` 的序列化配置 |
| 一个类型多个来源 | 两者 + `@Qualifier`/`@Named` | 区分 app/activity Context |

> ⚠️ `@Provides` ≠ 单例。单例靠 `@Singleton` 作用域注解，与 `@Provides`/`@Binds` 无关。

---

## 6. 作用域与 `@InstallIn`

`@InstallIn` 决定绑定放进哪个容器（Component），即对象的生命周期与可见范围。

```
SingletonComponent（应用级，整个 app 存活）
   └─ ActivityRetainedComponent
        └─ ViewModelComponent
             └─ ActivityComponent / FragmentComponent
```

| 作用域 | 生命周期 | 说明 |
|--------|----------|------|
| `@Singleton` | 整个 app | 全局一份（DataStore/Repository/Analytics） |
| `@ActivityScoped` | 跟随 Activity | |
| `@ViewModelScoped` | 跟随 ViewModel | |
| `@FragmentScoped` | 跟随 Fragment | |

规则：上层对象可注入到下层，反之不行（避免"活得更短的对象被更长生命周期的对象引用"导致泄漏）。

---

## 7. 完整注入链路（项目实战）

```
@HiltAndroidApp (ChopCalApplication)          ← 容器根
        │
@AndroidEntryPoint (OnboardingActivity)        ← 入口组件
        │
@HiltViewModel (OnboardingViewModel)           ← 注入 ViewModel
        │ @Inject constructor 声明需要：
        │   UserProfileRepository（接口）
        │   CalculateBmiUseCase 等
        │
@Module + @Binds (RepositoryModule)            ← 接口 → UserProfileRepositoryImpl
@Module + @Provides (DataStoreModule)          ← 提供 DataStore / Json
```

**编译期**：扫描所有 `@Inject constructor` / `@Provides` / `@Binds` / `@InstallIn`，生成依赖图，并校验缺失（缺失编译报错）。

**运行时**：创建 `OnboardingViewModel` → 查图需要 `UserProfileRepository` → 找到 `@Binds` 用 `UserProfileRepositoryImpl` → 其构造又需要 `DataStore`、`Json` → 找到 `@Provides` 递归创建 → 组装完成。

---

## 8. 什么场景使用 DI / Hilt

### 适合用（推荐）

1. **多模块、分层架构**：domain（接口）/ data（实现）/ core / page，用 `@Binds` 解耦，换数据源只改 Module。
2. **需要单元测试 / 可替换实现**：注入接口后测试易替换 fake。
3. **共享单例 / 昂贵对象**：`DataStore`、`Json`、网络客户端、数据库、Analytics。
4. **依赖关系复杂、需集中管理**：依赖链越长，收益越大。
5. **团队协作、长期维护**：依赖写在构造函数签名里，一眼看清。

### 不必过度使用

1. 很小的脚本 / Demo，手动 new 更简单。
2. 纯工具类、无状态静态方法。
3. 数据模型 / POJO（`UserProfile`、`BmiResult` 等纯数据类）。

### 一句话判断

> 如果"创建对象"有讲究（选哪个实现、生命周期多长、共享给谁、便于测试），就用 DI；普通数据/工具直接 new。
