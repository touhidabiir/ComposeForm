# `:network`

An Android library module (namespace `com.touhid.composeform.network`) owning OkHttp/Retrofit and all raw API/network work — Retrofit service interfaces, request/response DTOs, interceptors, the safe-call layer. Deliberately has no notion of "the repository consumers should inject" — that's [`:data`](../data/README.md).

## Network boundary (same pattern as the design system boundary)

`:network` depends on `okhttp` (+ `logging-interceptor`) and `retrofit2` (+ `converter-scalars`, `converter-gson`, `adapter-rxjava2`) as `implementation` (not `api`). `:app` does not declare either itself, so `okhttp3.*`/`retrofit2.*` are not on its compile classpath — importing them there fails to compile. All API/network work belongs inside `:network`; the repository layer that's the actual thing `:app` calls lives in `:data` instead.

## Internal structure

```
network/src/main/java/com/touhid/composeform/network/
├── api/                     # Retrofit service interfaces — AppApiService, PaymentApiService,
│                            # AnalyticsApiService, PartnerApiService (all public — see :data/README.md)
├── model/                   # request/response data classes, Gson-reflected
├── auth/                    # TokenProvider (contract) / AuthInterceptor
├── interceptor/             # HeaderInterceptor / RequestIdInterceptor / ErrorInterceptor
├── mock/                    # MockDataInterceptor / MockJson — temporary scaffolding, deleted
│                            # together once a real backend exists
├── qualifier/                # every Hilt qualifier — the four @XBaseUrls + internal @XRetrofits
├── RetrofitFactory.kt        # internal - builds one OkHttpClient/Retrofit pair per base URL
├── NetworkModule.kt          # internal - the @Provides graph for all four base URLs
├── NetworkResult.kt / NetworkError.kt / SafeApiCall.kt   # the safe-call layer
```

There is no `repository/` package here — that moved to [`:data`](../data/README.md) in a deliberate NIA-style split (`:core:network` raw client vs. `:core:data` repositories).

- `@BaseUrl` (`qualifier/BaseUrl.kt`) — a Hilt qualifier a consuming module's own Hilt module binds to a `String` (e.g. `@Provides @BaseUrl fun provideBaseUrl(): String = "..."`) so `:network` never hardcodes an environment's base URL. Retrofit requires a trailing slash (`https://api.example.com/`, not `.../com`) — `NetworkModule` normalizes a missing one, but supply it correctly regardless. `:app` currently reads all four base URLs (`di/AppNetworkModule.kt`) from `SharedPreferences` (injected, via `di/AppPreferencesModule.kt`) — read once per process at Hilt's `SingletonComponent` resolution, so a value saved after that only takes effect on the next app restart; defaults to `""` when unset, which itself throws `IllegalArgumentException` the moment `Retrofit`/`OkHttpClient` actually get built from it.
- `AppApiService` (`api/AppApiService.kt`) — public (needed by `:data`'s `AppRepositoryImpl`), the main Retrofit service interface: `login`, `getManagerList`, `getAdminList`, `getAdminDetails`, `getSpecificForm`, `getLeadDashboard` + `submitEkyc`, `getAcquisitionList`/`getAcquisitionDetail`/`getAcquisitionReasons`/`submitAcquisitionDecision`. Bound as a Hilt-injectable singleton in `NetworkModule`. `:app` never references it directly — by convention, not compiler enforcement — go through `:data`'s repositories instead.
- `model/` — the request/response data classes, Gson-reflected. The simplest (`LoginRequest`/`LoginResponse`, `ManagerSummary`, `AdminSummary`, `AdminDetails`) rely on exact field-name matching with no serialization annotations; ones mirroring a real backend's JSON shape 1:1 use `@SerializedName` for snake_case keys. Grouped one file per feature/endpoint, not one file per class.
- `NetworkResult`/`NetworkError`/`safeApiCall` — the safe-call layer, public so `:data`'s repositories can call it. `safeApiCall { ... }` runs a suspending Retrofit call on `Dispatchers.IO` and maps `ErrorInterceptor`'s `ApiException`/`HttpException`/`SocketTimeoutException`/`IOException`/anything else into `NetworkResult.Success`/`NetworkResult.Failure(NetworkError.Http|Timeout|NoConnection|Unexpected)` — `retrofit2`/`okhttp3`/`java.io` exception types never cross the module boundary. `CancellationException` is rethrown, not wrapped.
- `ErrorInterceptor` (`interceptor/ErrorInterceptor.kt`) — `internal`. Detects errors purely by HTTP status (`!response.isSuccessful`) — a body field like the app API's `is_error` isn't a reliable discriminator on this backend, so it isn't used for detection. What it does is enrich a non-2xx response: reads the error body's `{message, status}` (via `peekBody`, without consuming the body Retrofit's own converter still needs) and throws `ApiException(code, status, rawBody, message)` carrying them, so `safeApiCall` sees the backend's own message instead of a generic HTTP one. Scoped to just the main `@BaseUrl` client, not every base URL.
- `auth/` — `TokenProvider` (contract; storage deferred to the consuming module) and `AuthInterceptor` (`internal`, attaches `Authorization: Bearer <token>`). `:app` binds `TokenProvider` to `di/EncryptedTokenProvider.kt`, backed by the one shared `EncryptedSharedPreferences` instance `di/AppPreferencesModule.kt` provides (file `"app_prefs"`) — the same instance the four base URLs are also read from.
- `RetrofitFactory` — `internal`, `create(baseUrl, authInterceptor: Interceptor?, interceptors: List<Interceptor> = emptyList())`. Each base URL gets its own `OkHttpClient`/connection pool, composed from this one shared factory.
- **Multiple base URLs**: beyond the main `@BaseUrl`/`AppApiService`, three more base-URL-scoped groups follow the identical pattern — `@PaymentBaseUrl`/`PaymentApiService`, `@AnalyticsBaseUrl`/`AnalyticsApiService`, `@PartnerBaseUrl`/`PartnerApiService` (their `*Repository` counterparts live in `:data`). `PartnerApiService`'s client is built with `authInterceptor = null` (a third-party backend must never see our bearer token) plus a `HeaderInterceptor` carrying its own API key instead.

`:network` depends on `hilt-android`/`ksp(hilt-compiler)`/`kotlinx-coroutines-core` for its own `@Module`s and the safe-call layer, but does **not** apply the Hilt Gradle plugin — only `:app` (which has `ComposeFormApplication`, the `@HiltAndroidApp` entry point) does; library modules just contribute `@Module`s via the compiler dependency.

For the broader module architecture and the other module boundaries, see [`CLAUDE.md`](../CLAUDE.md).
