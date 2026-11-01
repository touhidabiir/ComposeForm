package com.touhid.composeform.feature.specificform.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Plain blocking functions rather than suspend ones: Room 2.6.1's code generator mishandles
// suspend DAO functions under KSP2. The repository calls them through safeDbCall, which runs
// them on Dispatchers.IO.
@Dao
internal interface SavedSubmissionDao {

    @Query("SELECT * FROM saved_submission WHERE form_key = :formKey ORDER BY saved_at DESC")
    fun observeForForm(formKey: String): Flow<List<SavedSubmissionEntity>>

    @Query("SELECT * FROM saved_submission WHERE id = :id")
    fun getById(id: String): SavedSubmissionEntity?

    @Upsert
    fun upsert(entity: SavedSubmissionEntity)

    @Query("DELETE FROM saved_submission WHERE id = :id")
    fun delete(id: String)
}
