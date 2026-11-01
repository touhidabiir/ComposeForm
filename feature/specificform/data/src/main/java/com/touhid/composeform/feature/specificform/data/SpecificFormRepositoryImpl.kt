package com.touhid.composeform.feature.specificform.data

import com.touhid.composeform.database.safeDbCall
import com.touhid.composeform.feature.specificform.data.local.SavedSubmissionDao
import com.touhid.composeform.feature.specificform.domain.FormSubmission
import com.touhid.composeform.feature.specificform.domain.SavedSubmission
import com.touhid.composeform.feature.specificform.domain.SpecificForm
import com.touhid.composeform.feature.specificform.domain.SpecificFormRepository
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

internal class SpecificFormRepositoryImpl @Inject constructor(
    private val api: SpecificFormApi,
    private val savedDao: SavedSubmissionDao,
) : SpecificFormRepository {

    override suspend fun getSpecificForm(): AppResult<SpecificForm> =
        safeApiCall { api.getSpecificForm().data }.map { it.toDomain() }

    override suspend fun submit(submission: FormSubmission): AppResult<Unit> =
        safeApiCall { api.submitSpecificForm(submission.toRequestDto()) }.map { }

    override suspend fun saveForLater(
        id: String?,
        title: String,
        submission: FormSubmission,
        formStateJson: String,
    ): AppResult<String> {
        val savedId = id ?: UUID.randomUUID().toString()
        val entity = submission.toEntity(savedId, title, formStateJson, savedAt = System.currentTimeMillis())
        return safeDbCall { savedDao.upsert(entity) }.map { savedId }
    }

    override fun observeSavedSubmissions(formKey: String): Flow<List<SavedSubmission>> =
        savedDao.observeForForm(formKey).map { list -> list.map { it.toDomain() } }

    override suspend fun getSavedSubmission(id: String): AppResult<SavedSubmission?> =
        safeDbCall { savedDao.getById(id)?.toDomain() }

    override suspend fun uploadSaved(id: String): AppResult<Unit> {
        val saved = when (val result = getSavedSubmission(id)) {
            is AppResult.Failure -> return result
            // Already gone (uploaded or deleted meanwhile): nothing left to do.
            is AppResult.Success -> result.data ?: return AppResult.Success(Unit)
        }
        return when (val result = submit(saved.submission)) {
            is AppResult.Success -> deleteSaved(id)
            is AppResult.Failure -> result
        }
    }

    override suspend fun deleteSaved(id: String): AppResult<Unit> = safeDbCall { savedDao.delete(id) }
}
