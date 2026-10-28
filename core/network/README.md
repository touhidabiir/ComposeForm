# `:core:network`

An Android library module (namespace `com.touhid.composeform.network`) owning OkHttp/Retrofit and the client around it: the main `Retrofit`, auth and token refresh, interceptors, debug mocks, and `safeAppCall`. It holds no feature endpoints. Each feature's (or core slice's) `data` module declares its own Retrofit interface and DTOs and creates its service from this module's main `Retrofit`.

## Network boundary (same pattern as the design system boundary)

`:core:network` depends on `okhttp` (+ `logging-interceptor`) and `retrofit2` (+ `converter-scalars`, `converter-gson`) as `implementation` (not `api`). A `data` module that declares a Retrofit interface adds `retrofit2` (and `gson`, for `@SerializedName`) itself as `implementation`, so neither ever reaches a `ui` module or `:app`. Only `data` modules, and `:app` to configure this module, may depend on `:core:network`; `build-logic`'s dependency-rules plugin fails the build otherwise.

## Internal structure

```
core/network/src/main/java/com/touhid/composeform/network/
├── api/                     # RefreshTokenApiService (internal) - the only endpoint this module owns
├── model/                   # its request/response classes (RefreshTokenModels.kt)
├── auth/                    # TokenProvider (contract) / AuthInterceptor / TokenAuthenticator / AuthSessionEvents
├── interceptor/             # RequestIdInterceptor / AppVersionInterceptor / ErrorInterceptor
├── mock/                    # MockDataInterceptor / MockJson - temporary scaffolding, deleted
│                            # together once a real backend exists
├── qualifier/               # @BaseUrl, @AppVersionCode (public), @RefreshRetrofit (internal)
├── RetrofitFactory.kt       # internal - builds one OkHttpClient/Retrofit pair
├── NetworkModule.kt         # internal - logging interceptor, the main Retrofit, the refresh client
├── SafeAppCall.kt           # safeAppCall - the public safe-call entry point, returns AppResult
├── NetworkResult.kt / NetworkError.kt / SafeApiCall.kt   # internal exception mapping behind it
```

- `@BaseUrl` (`qualifier/BaseUrl.kt`): a Hilt qualifier `:app` binds to a `String` (`di/AppNetworkModule.kt`), so this module never hardcodes an environment's base URL. Retrofit requires a trailing slash (`https://api.example.com/`, not `.../com`). `RetrofitFactory` normalizes a missing one, but supply it correctly regardless. `:app` reads it once per process from the shared `SharedPreferences`, falling back to `https://api.composeform.dummy/` when unset or blank. Never let the fallback be `""`, which Retrofit rejects with "Expected URL scheme 'http' or 'https'".
- **The main `Retrofit`** (unqualified, `NetworkModule.provideRetrofit`): every `data` module creates its service from it in its own Hilt module (`retrofit.create(LeadApi::class.java)`), so all endpoints share the same auth, token refresh, error mapping and debug mocks. A second, unrelated backend gets its own public `@XBaseUrl` qualifier, an internal `@XRetrofit` qualifier and a `@Provides` here calling `RetrofitFactory.create()` with exactly the interceptors it needs. For example, a third-party backend that must never see our bearer token gets `authInterceptor = null`. Never reuse `@BaseUrl` for an unrelated backend.
- `safeAppCall { ... }` (`SafeAppCall.kt`): runs a suspending Retrofit call on `Dispatchers.IO` and returns `:core:model`'s `AppResult`, either `Success` or `Failure(AppError.NoConnection|Timeout|Unauthorized|Server|Unknown)`. Internally, `safeApiCall` maps `ErrorInterceptor`'s `ApiException`/`HttpException`/`SocketTimeoutException`/`IOException`/anything else into the internal `NetworkResult`/`NetworkError`, which is then mapped once into `AppError`. HTTP 401 becomes `Unauthorized`. `retrofit2`/`okhttp3`/`java.io` exception types never cross the module boundary. `CancellationException` is rethrown, not wrapped.
- `ErrorInterceptor` (`interceptor/ErrorInterceptor.kt`, `internal`): detects errors purely by HTTP status (`!response.isSuccessful`). A body field like the app API's `is_error` isn't a reliable discriminator on this backend, so it isn't used. On a non-2xx response it reads the error body's `{message, status}` via `peekBody`, without consuming the body Retrofit's own converter still needs. It then throws `ApiException(code, status, rawBody, message)` carrying them, so callers see the backend's own message instead of a generic HTTP one. It is attached to the main `@BaseUrl` client only.
- `auth/`: `TokenProvider` is the contract, with storage deferred to `:app`: `getToken`/`setToken`, `getRefreshToken`/`setRefreshToken` and a default `clear()`. `AuthInterceptor` (`internal`) attaches `Authorization: Bearer <token>`. `:app` binds `TokenProvider` to `di/EncryptedTokenProvider.kt`, backed by the one shared `EncryptedSharedPreferences` instance `di/AppPreferencesModule.kt` provides (file `"app_prefs"`). The base URL is read from that same instance.
- **Refresh tokens**: `:core:session:data`'s `SessionRepositoryImpl` stores both tokens from a successful login via `TokenProvider`. `TokenAuthenticator` (`internal`, an OkHttp `Authenticator`) is attached to the main client only. When a request that carried a token gets a 401, it:
  1. calls `RefreshTokenApiService` (`POST v1/auth/refresh`, body `{refreshToken}`, response `{token, refreshToken}`);
  2. saves both new tokens;
  3. retries the original request once, with the new token and a fresh `X-Request-Id`.

  Parallel 401s share one refresh: the refresh is `synchronized`, and a request whose token another request already rotated just retries with the new one. The refresh endpoint has its own `@RefreshRetrofit` client (same base URL, but no `AuthInterceptor`, no `ErrorInterceptor` and no authenticator). Sharing the main client would cause three problems:
  - a Hilt dependency cycle;
  - `ErrorInterceptor` would turn a rejected refresh into an `IOException`, so the session would never end;
  - a 401 on the refresh call would re-enter the authenticator recursively.
- **Session expiry**: if there's no refresh token, or the refresh endpoint returns 401/403, `TokenAuthenticator` calls `TokenProvider.clear()` and emits on `AuthSessionEvents.sessionExpired` (a public `@Singleton` `SharedFlow<Unit>`). Reacting to it is the consuming module's job; nothing collects it yet, since no sign-in screen exists. A connectivity failure or 5xx from the refresh endpoint keeps the session, and the caller just sees the original 401.
- `RetrofitFactory` (`internal`): `create(baseUrl, authInterceptor: Interceptor?, interceptors: List<Interceptor> = emptyList(), authenticator: Authenticator? = null)`. Each client gets its own `OkHttpClient` and connection pool. `RequestIdInterceptor` (`X-Request-Id`) and `AppVersionInterceptor` (`X-Version: android/<versionCode>`) are added to every client. The version code comes from `@AppVersionCode`, which `:app` binds from its own `BuildConfig.VERSION_CODE`, since library modules have no version code. The debug-only `MockDataInterceptor` is added **last**, after logging, because it answers mocked paths without calling `chain.proceed()`.

`:core:network` depends on `hilt-android`/`ksp(hilt-compiler)`/`kotlinx-coroutines-core` for its own `@Module`s and the safe-call layer, but does **not** apply the Hilt Gradle plugin. Only `:app`, which has `ComposeFormApplication` (the `@HiltAndroidApp` entry point), does.

For the broader module architecture and the other module boundaries, see [`CLAUDE.md`](../../CLAUDE.md).
