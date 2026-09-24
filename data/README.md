# `:data`

An Android library module (namespace `com.touhid.composeform.data`) owning the repository layer — `AppRepository`/`PaymentRepository`/`AnalyticsRepository`/`PartnerRepository`, one per `:network` base URL. `:app` depends on it via `implementation(project(":data"))`, alongside a **direct** `:network` dependency it keeps for unrelated config-ownership reasons (see [`network/README.md`](../network/README.md)).

## Why this module exists

Mirrors Google's "Now in Android" reference architecture: `:core:network` (raw Retrofit/DTOs) and `:core:data` (repositories) are separate modules, and features depend on `:core:data`, never `:core:network`, directly. Here, `:network` owns the raw transport — Retrofit service interfaces, request/response DTOs, interceptors, the safe-call layer — and has no notion of "the repository consumers should inject." `:data` is that repository layer.

The consequence worth knowing: `AppApiService`/`PaymentApiService`/`AnalyticsApiService`/`PartnerApiService` had to become **public** (they were `internal` to `:network` before this split existed) — a repository's `@Inject constructor(apiService: XApiService)` compiles inside `:data`, a separate compilation unit from `:network`, so `internal` can no longer hide the service interface from it. This is not a weaker boundary, it's the same one NIA itself relies on: enforcement moves from "Kotlin `internal` visibility" to "the Gradle dependency graph" — nothing except `:data` has a reason to import `network.api.*`, and the invariant that actually matters (raw `retrofit2`/`okhttp3` types never reaching `:app`) is untouched, since those stay `implementation`-only inside `:network` regardless.

## Internal structure

```
data/src/main/java/com/touhid/composeform/data/
├── repository/              # AppRepository / PaymentRepository / AnalyticsRepository / PartnerRepository
│                            # — interfaces, the module's actual public contract
│   └── impl/                # AppRepositoryImpl / PaymentRepositoryImpl / AnalyticsRepositoryImpl /
│                            # PartnerRepositoryImpl — the implementations, one package deeper
└── di/
    └── RepositoryModule.kt  # @Binds each *RepositoryImpl to its interface
```

Interfaces at the package root, implementations in `impl/` — a plain split so `repository/` reads as the module's actual public contract at a glance. Each `*RepositoryImpl` is bound to its interface via `@Binds` in `RepositoryModule.kt` (`internal abstract class`, `@Module @InstallIn(SingletonComponent::class)`) — this exists purely for testability, the same reason NIA declares its repositories as interfaces even though interface and implementation live in the same module: a ViewModel test can substitute a fake implementing the interface without constructing a real `AppApiService`/`TokenProvider`. Consumers only ever reference the interface name (`AppRepository`, never `AppRepositoryImpl`) — Hilt resolves the binding.

Each `*RepositoryImpl` wraps its `*ApiService` calls in `safeApiCall` (imported from `:network`, which stays the one place that maps `retrofit2`/`okhttp3` exceptions into `NetworkResult`); `AppRepositoryImpl` additionally stores the token via `TokenProvider` on a successful `login`, so callers never manage the token by hand — subsequent authenticated calls are authenticated for free. `AppRepository` is **the actual thing consumers should inject**, and the one that's actually injected today: `LeadDashboardViewModel`, `AcquisitionApprovalListViewModel`, `AcquisitionApprovalDetailViewModel`, and `SpecificFormViewModel` (all `@HiltViewModel`) inject it directly.

## Dependency on `:network`: `api`, not `implementation`

Deliberate, not an oversight: `AppRepository`'s own public methods return `NetworkResult<T>`, where `NetworkResult` and every `T` (the model classes) are `:network`-owned types — they're part of `:data`'s own public surface. Any consumer of `:data` needs those types resolvable on its own classpath just to use a repository's return value (e.g. `is NetworkResult.Success -> result.data`), the same reason NIA exposes `:core:model` as `api` from `:core:data`. `implementation` would still compile today only because `:app` happens to also depend on `:network` directly (for the config reasons `network/README.md` covers); a future module depending on `:data` alone (e.g. a `:feature:leaddashboard` with no reason to configure the network client) would fail to resolve `NetworkResult` without this.

This `api` re-export does **not** leak `retrofit2`/`okhttp3`/`gson` any further, though — those stay `implementation` inside `:network`'s own `build.gradle.kts`, and Gradle's `api`/`implementation` propagation only forwards a dependency that's `api` at *every* hop, so that chain dead-ends at `:network` regardless. `:data` itself has no other dependency beyond `hilt-android`/`ksp(hilt-compiler)` for its own `@Inject constructor`s — it does not depend on `okhttp`/`retrofit2`/`gson` directly, since it never builds a client or calls Gson itself.

`:data` depends on `hilt-android`/`ksp(hilt-compiler)` for `RepositoryModule` and its `@Inject constructor`s, but does **not** apply the Hilt Gradle plugin (`com.google.dagger.hilt.android`) — per Hilt's multi-module guidance, only `:app` (which has `ComposeFormApplication`, the `@HiltAndroidApp` entry point) applies that plugin; library modules just contribute injectable classes via the compiler dependency.

For the broader module architecture and the other module boundaries, see [`CLAUDE.md`](../CLAUDE.md).
