# ComposeForm Architecture

Oct 19, 2026 · Touhid Abir

## Summary

ComposeForm is organised as feature slices: each business feature owns its own `domain`, `data`, `ui` and `navigation` modules, on top of a thin shared `core`. The architecture is built for an app with hundreds of screens and hundreds of APIs, developed by several developers in parallel.

**Design goals**

- A developer working on one feature edits only that feature's folder, plus rare, deliberate changes to `core`.
- Screens never see DTOs, Retrofit or raw network errors; they see domain models and one shared `AppResult`.
- Every boundary is enforced by the Gradle dependency graph, so a shortcut fails the build instead of failing review.
- Compose screens, XML Activities and Fragments coexist, and a caller never needs to know which kind it is opening.

## Module layout

The repo has three kinds of module: shared `core` modules, one folder per business feature, and `:app` as the shell that assembles everything.

```
core/
  model/  network/  designsystem/  common/  navigation/     infrastructure
  lead/  session/                                          core slices (domain/ + data/)
feature/
  lead/           domain/  data/  ui/  navigation/
  acquisition/    domain/  data/  ui/  navigation/
  specificform/   domain/  data/  ui/
formbuilder/
app/
build-logic/
```

| Module | Owns | Does not own |
| --- | --- | --- |
| `core:model` | `AppResult`, `AppError`, generic value types | Business models, shared or not |
| `core:network` | The HTTP client, base-URL qualifier, auth and token refresh, `safeApiCall` | Feature endpoints, DTOs, repositories |
| `core:navigation` | `Destination`, `Navigator`, `DestinationHandler` | Any concrete destination |
| `core:designsystem` | All Material3 access, the app theme | Feature-specific compositions |
| `core:<topic>:domain` / `:data` | Data two or more features use, and app-wide data (session), split like a feature | UI; data only one feature uses |
| `feature:x:domain` | The feature's models and repository interfaces (pure Kotlin) | Android, Retrofit, DTOs |
| `feature:x:data` | The feature's Retrofit API, DTOs, mappers, repository implementations | Anything a screen touches directly |
| `feature:x:ui` | Screens, ViewModels, strings, its host Activity, handlers for its own destinations | Other features' screens |
| `feature:x:navigation` | Destination classes, result contracts | Screens or logic |
| `:app` | Assembly: DI root, `Navigator`, deep-link parsing, config into `core:network` | Feature logic |

**What counts as a feature:** a business area, not a screen; `feature:lead:ui` can hold twenty lead screens. A feature with no API is just a `ui` module, and a `navigation` module exists only when another module navigates in. `:formbuilder` stays a standalone library that features reuse.

## Dependency rules

Dependencies point one way: `ui` and `data` both depend on `domain`, `domain` depends only on `core:model`, and only `:app` sees everything.

**How slices relate**

[[DIAGRAM:slices.png|Figure 1. Two features sharing lead data through a core slice]]

Colour shows the layer: blue `ui`, green `domain`, orange `data`, purple `navigation`. Black arrows point from a module to what it depends on. Lead data that both features use lives in the `core:lead` slice (dotted arrows); lead-only data stays in `feature:lead`. The red arrow is the only link one feature may have into another: opening its screens through its `navigation` module. `:app` also depends on every `data` and `ui` module, for wiring.

**What each layer uses from core**

[[DIAGRAM:core.png|Figure 2. What each layer uses from core]]

Each layer depends only on the core modules it needs, the same in every feature and core slice. `ui` and `data` reach `core:model` through their own `domain`, whose public API exposes `AppResult`.

| Module | May depend on | Must never depend on |
| --- | --- | --- |
| `feature:x:domain` | `core:model`, core slices' `domain` | Android, any `data`/`ui`, `core:network`, other features' `domain` |
| `feature:x:data` | `feature:x:domain`, `core:model`, `core:network`, core slices' `domain` and `data` | Any `ui` or `navigation`, other features' `data` |
| `feature:x:ui` | `feature:x:domain`, its own `navigation`, `core:designsystem`, `core:common`, `core:navigation`, core slices' `domain`, other features' `navigation`, `:formbuilder` | Any `data`, `core:network`, Material3 directly, other features' `ui` and `domain` |
| `feature:x:navigation` | `core:navigation` | Everything else |
| `core:<topic>:domain` | `core:model`, other core slices' `domain` | Android, any `data`, any `feature` |
| `core:<topic>:data` | Its own `domain`, `core:model`, `core:network` | Any `feature`, any `ui` or `navigation` |
| Other `core:*` | Other `core` modules only, in one direction | Any `feature` |
| `:app` | Everything | Nothing depends on `:app` |

**Enforced by the build.** A forbidden project dependency fails the build (see Build logic), so a violation is caught on the developer's machine, not in review. `:app` depends on every `data` module only because Hilt must see each `@Binds`; no screen ever does.

## Domain layer

Each `domain` module is pure Kotlin and holds the feature's models and repository interfaces: its public contract. Models are shaped for the app, not the wire, and grow stricter over time: non-null where the app requires a value, parsed dates, enums instead of status strings.

**Use cases only when needed.** ViewModels call repository interfaces directly. A use case is written only to combine two or more repositories, to hold a business rule more than one ViewModel applies, or to avoid copying an orchestration between screens; never to forward a single call.

Every repository returns `AppResult<T>` from `core:model`: either `Success` or `Failure` carrying an `AppError` (`NoConnection`, `Timeout`, `Unauthorized`, `Server`, `Unknown`). One shared type means network errors are mapped once and every screen handles them the same way. `core:model` holds only such generic types, never business models. Code: see Appendix.

## Data layer

`core:network` holds infrastructure only: the main `Retrofit` behind the `@BaseUrl` qualifier, the interceptors (auth, token refresh, request ID, error body, debug mock) and `safeApiCall`, which maps every exception to an `AppError`. Its only endpoint is the token refresh, which the token authenticator itself calls. A second, unrelated backend gets its own base-URL qualifier and client here, never a reuse of `@BaseUrl`.

Each `data` module holds its feature's Retrofit API, DTOs, mappers and repository implementations, all `internal`. It creates its API from the main `Retrofit` in its own Hilt module, so every endpoint shares the same auth, token refresh, error mapping and debug mocks. DTOs are mapped to domain models here, so malformed backend data never reaches a screen. Room DAOs and entities will follow the same shape once a database is added, with a `core:database` module holding only the database setup.

OkHttp, Retrofit and Gson stay `implementation` inside `core:network`. A `data` module that declares a Retrofit interface adds Retrofit (and Gson, for its annotations) itself, also as `implementation`, so none of it reaches `ui` or `:app`. Code: see Appendix.

## Sharing data between features

Features never depend on each other's `domain` or `data`: anything two features use moves into a core slice.

| Situation | Where it goes | Example |
| --- | --- | --- |
| Data only one feature uses | That feature's own `domain` and `data` | Acquisition endpoints in `feature:acquisition:data` |
| Data two or more features use | Moved, `domain` and `data` together, into `core:<topic>` | The lead closer, reviewer and rejection types (and their DTOs) both features show, in `core:lead` |
| App-wide data | A core slice per topic | Sign-in in `core:session` |
| Generic value types with no business meaning | `core:model` | `AppResult`, `AppError` |
| Logic combining data from several slices | A use case in the feature that needs the combination | Acquisition detail that also shows the lead's history |

- **Data moves to core on second use.** While only one feature uses it, `domain` and `data` stay in that feature. When a second feature needs it, both move into `core:<topic>` and no copy stays behind; the original feature keeps its `ui` and uses the core slice like any other feature.
- **The only cross-feature dependency is `navigation`**, for opening another feature's screens. A feature never depends on another feature's `domain`, `data` or `ui`.
- **One topic per core slice**, named after it (`core:lead`, `core:session`, never `core:shared`), with no `ui` module. A single catch-all `core:domain` + `core:data` is not allowed: it would grow without limit and recompile the whole app on every change.
- **A core slice holds only what is actually shared.** Endpoints and models that one feature uses stay in that feature, even when the rest of that topic lives in core. A core slice's `data` may hold only shared wire types and their mappers, when the shared part is a type both features' responses embed rather than an endpoint.

## Navigation

A screen opens another feature's screen by sending a destination to a `Navigator`; only the owning feature knows whether that destination is a Compose screen, an XML Activity or a Fragment-based flow.

1. **Inside a feature**, the feature decides: its own Compose `NavHost`, a Fragment nav graph, or plain Activities.
2. **Between features**, only destinations and the `Navigator` are used.

```kotlin
// feature:lead:navigation
data object LeadDashboardDestination : Destination

// :app (or any feature's ui)
navigator.navigate(LeadDashboardDestination)
```

The owning feature's `ui` registers a `DestinationHandler` for each destination; `:app`'s `Navigator` collects them through Hilt multibinding and dispatches by type. Full code: see Appendix.

| Target | The handler does |
| --- | --- |
| Activity (XML or Compose content) | `startActivity(...)` |
| Compose screen in its feature's host | `startActivity(...)` on the feature's own host Activity, passing any arguments; that Activity's `NavHost` (in `feature:x:ui`) routes to the screen |
| Fragment | Never a cross-feature entry point. A Fragment-based feature exposes an Activity (or its own nav graph) as its entry; its Fragments stay internal |

- **ViewModels never navigate.** They emit a one-off event; the Activity, Fragment or composable calls `navigator.navigate(...)`.
- **Results across features** use an `ActivityResultContract` published in the target's `navigation` module. That's why `navigation` modules are Android libraries, not pure Kotlin.
- **Deep links and push notifications** are parsed in `:app` into a `Destination` and sent through the same `Navigator`.
- **Each `ui` module has its own `AndroidManifest.xml`** declaring its Activities; `:app`'s manifest lists only its own launcher Activity.
- **Feature screens are `internal`**: only the feature's own host Activity renders them.

## Build logic

Shared Gradle setup (SDK levels, Java version, Compose, Hilt) lives in convention plugins in `build-logic/`. Each module applies one plugin, such as `composeform.android.feature` or `composeform.jvm.library`, and states only its namespace and its own dependencies. A rules plugin, applied by every module's base plugin, fails the build on any dependency the Dependency rules table forbids. Plugin list and an example build file: see Appendix.

**Hilt in library modules.** Only `:app` applies the Hilt Gradle plugin. Library modules contribute `@Module`s, `@Inject` classes and `@HiltViewModel`s through the Hilt compiler alone, and an `@AndroidEntryPoint` Activity or Fragment in a feature module uses Hilt's long form, `@AndroidEntryPoint(ComponentActivity::class) class LeadActivity : Hilt_LeadActivity()`.

## Appendix: implementation examples

### Domain module

```kotlin
// feature:lead:domain
data class LeadListItem(val id: Long, val shopName: String, val status: LeadStatus, ...)
enum class LeadStatus { Pending, Approved, Rejected }

interface LeadRepository {
    suspend fun getLeads(status: String?, search: String?, pageNo: Int): AppResult<LeadDashboardPage>
    suspend fun submitEkyc(leadId: Long): AppResult<Unit>
}

// core:model
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

sealed interface AppError {
    val message: String
    data class NoConnection(...) : AppError
    data class Timeout(...) : AppError
    data class Unauthorized(...) : AppError
    data class Server(val code: Int, val status: String?, ...) : AppError
    data class Unknown(..., val cause: Throwable? = null) : AppError
}
```

### Data module

```kotlin
// feature:lead:data
internal interface LeadApi {
    @GET("v1/leads")
    suspend fun getLeads(
        @Query("status") status: String?,
        @Query("search") search: String?,
        @Query("page_no") pageNo: Int,
    ): LeadDashboardResponseDto
}

internal data class LeadListItemDto(
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("lead_closer") val leadCloser: LeadCloserDto,  // from core:lead:data
    ...
)
internal fun LeadListItemDto.toDomain() = LeadListItem(...)

internal class LeadRepositoryImpl @Inject constructor(private val api: LeadApi) : LeadRepository {
    override suspend fun getLeads(status: String?, search: String?, pageNo: Int) =
        safeApiCall { api.getLeads(status, search, pageNo).data }.map { it.toDomain() }
}

@Module @InstallIn(SingletonComponent::class)
internal abstract class LeadDataModule {
    @Binds abstract fun bindLeadRepository(impl: LeadRepositoryImpl): LeadRepository
    companion object {
        // The unqualified Retrofit is core:network's main @BaseUrl client.
        @Provides @Singleton
        fun provideLeadApi(retrofit: Retrofit): LeadApi = retrofit.create(LeadApi::class.java)
    }
}
```

### Navigation

```kotlin
// core:navigation
interface Destination
interface Navigator { fun navigate(destination: Destination) }
interface DestinationHandler<D : Destination> {
    val type: KClass<D>
    fun open(context: Context, destination: D)
}

// feature:lead:ui  (only this module knows it is an Activity)
internal class LeadDashboardHandler @Inject constructor() : DestinationHandler<LeadDashboardDestination> {
    override val type = LeadDashboardDestination::class
    override fun open(context: Context, destination: LeadDashboardDestination) {
        context.startActivity(LeadActivity.intent(context))
    }
}
// registered with @Binds @IntoSet in a SingletonComponent module

// :app, bound in ActivityComponent so handlers get an Activity context
class AppNavigator @Inject constructor(
    @ActivityContext private val context: Context,
    private val handlers: Set<@JvmSuppressWildcards DestinationHandler<*>>,
) : Navigator { ... }
```

### Build logic

```
build-logic/
  settings.gradle.kts            reads ../gradle/libs.versions.toml
  convention/
    build.gradle.kts             compileOnly AGP + Kotlin Gradle plugins
    src/main/kotlin/             one class per plugin below
```

| Plugin | Used by |
| --- | --- |
| `composeform.android.application` | `:app` |
| `composeform.android.library` | `data` and `navigation` modules, `core:network`, `core:navigation` |
| `composeform.android.library.compose` | `core:designsystem`, `core:common`, `:formbuilder` |
| `composeform.android.feature` | Every `ui` module |
| `composeform.jvm.library` | Every `domain` module, `core:model`, `core:lead:data` |
| `composeform.hilt` | `:app`, `core:network` and every module with Hilt bindings |
| `composeform.dependency.rules` | Every module, applied by the application, Android library and JVM library plugins |

The root `settings.gradle.kts` adds `pluginManagement { includeBuild("build-logic") }`. A feature's `ui` build file then needs only:

```kotlin
plugins { id("composeform.android.feature") }
android { namespace = "com.touhid.composeform.feature.acquisition.ui" }
dependencies {
    implementation(project(":feature:acquisition:domain"))
    implementation(project(":feature:acquisition:navigation"))
    implementation(libs.coil.compose)
}
```
