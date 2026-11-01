package com.touhid.composeform.feature.specificform.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// This module's one database (file composeform_specificform.db). A new table goes into entities
// here, with a version bump and a migration; the exported schema lives in this module's schemas/.
@Database(entities = [SavedSubmissionEntity::class], version = 1, exportSchema = true)
internal abstract class SpecificFormDatabase : RoomDatabase() {
    abstract fun savedSubmissionDao(): SavedSubmissionDao
}
