# `:core:database`

An Android library module (namespace `com.touhid.composeform.database`) holding the Room infrastructure shared by every `data` module that stores data locally. It holds **no tables**, the same way `:core:network` holds no feature endpoints.

| `:core:network` | `:core:database` |
| --- | --- |
| the main `Retrofit` every `data` module creates its API from | `buildRoomDatabase(...)`, which every `data` module builds its database with |
| `safeApiCall { }` → `AppResult` | `safeDbCall { }` → `AppResult` |
| endpoints live in each `data` module | entities, DAOs and the `@Database` live in each `data` module |

## What's here

- `buildRoomDatabase(context, XDatabase::class.java, "name")` (`RoomDatabaseBuilder.kt`): returns a `RoomDatabase.Builder` for the file `composeform_<name>.db`, with every query logged under the `RoomQuery` tag in debug builds only. It returns the builder, not the database, so the module adds its own migrations before `build()`. Never rename a released database: Room would open a new, empty file.
- `safeDbCall { }` (`SafeDbCall.kt`): runs a DAO write or one-shot read on `Dispatchers.IO` and returns `:core:model`'s `AppResult`. Any failure becomes `AppError.Unknown` with the exception kept as `cause`; `CancellationException` is rethrown. Observing reads (a DAO's `Flow`) don't need it.

`:core:model` and `room-runtime` are `api` dependencies, since both appear in these signatures.

## Giving a `data` module its own database

```kotlin
// build.gradle.kts
plugins {
    id("composeform.android.library")
    id("composeform.hilt")
    id("composeform.android.room")   // Room + KSP compiler + schemas/ export + :core:database
}
```

Then, all `internal` in that module:

```kotlin
@Entity(tableName = "lead_note") internal data class LeadNoteEntity(@PrimaryKey val id: String, /* ... */)

@Dao internal interface LeadNoteDao { /* @Query / @Upsert / ... */ }

@Database(entities = [LeadNoteEntity::class], version = 1, exportSchema = true)
internal abstract class LeadDatabase : RoomDatabase() {
    abstract fun leadNoteDao(): LeadNoteDao
}

// its Hilt module
@Provides @Singleton
fun provideLeadDatabase(@ApplicationContext context: Context): LeadDatabase =
    buildRoomDatabase(context, LeadDatabase::class.java, "lead").build()

@Provides fun provideLeadNoteDao(db: LeadDatabase): LeadNoteDao = db.leadNoteDao()
```

Rules:

- **One `@Database` per module.** A new table goes into the existing one's `entities` (with a version bump and a migration), never a second database.
- **Commit `schemas/`.** Room writes the schema JSON there on every build; migrations are written against it. Never use `fallbackToDestructiveMigration` in release: it deletes users' data.
- **Tables two features need live in a core slice's `data` module**, like any shared data. Moving a table later means moving users' data between database files, so put user-entered data in a core slice from the start if it's likely to be shared.
- **Only `data` modules may depend on this module**, `:app` included. `build-logic`'s dependency-rules plugin fails the build otherwise.

For the broader module architecture, see [`CLAUDE.md`](../../CLAUDE.md).
