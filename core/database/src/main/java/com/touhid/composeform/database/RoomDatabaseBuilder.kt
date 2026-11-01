package com.touhid.composeform.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

// The database counterpart of :core:network's main Retrofit: every data module that keeps a Room
// database builds it through here, so all of them share the same file naming and debug logging.
// Returns the builder rather than the database, so each module still adds its own migrations:
//
//     buildRoomDatabase(context, LeadDatabase::class.java, "lead")
//         .addMigrations(LEAD_MIGRATION_1_2)
//         .build()
//
// name becomes the file composeform_<name>.db. Never change it once released: Room would open a
// new, empty file and the old one's data would be orphaned.
fun <T : RoomDatabase> buildRoomDatabase(context: Context, klass: Class<T>, name: String): RoomDatabase.Builder<T> =
    Room.databaseBuilder(context.applicationContext, klass, "composeform_$name.db").apply {
        // Same policy as :core:network's HttpLoggingInterceptor: log every query in debug builds only.
        if (BuildConfig.DEBUG) {
            setQueryCallback({ sql, args -> Log.d(TAG, "$sql $args") }, Dispatchers.IO.asExecutor())
        }
    }

private const val TAG = "RoomQuery"
