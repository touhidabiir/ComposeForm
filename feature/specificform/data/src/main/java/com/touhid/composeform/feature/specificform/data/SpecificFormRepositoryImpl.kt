package com.touhid.composeform.feature.specificform.data

import com.touhid.composeform.feature.specificform.domain.FormSubmission
import com.touhid.composeform.feature.specificform.domain.SpecificForm
import com.touhid.composeform.feature.specificform.domain.SpecificFormRepository
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.safeApiCall
import javax.inject.Inject

internal class SpecificFormRepositoryImpl @Inject constructor(
    private val api: SpecificFormApi,
) : SpecificFormRepository {

    override suspend fun getSpecificForm(): AppResult<SpecificForm> =
        safeApiCall { api.getSpecificForm().data }.map { it.toDomain() }

    override suspend fun submit(submission: FormSubmission): AppResult<Unit> =
        safeApiCall { api.submitSpecificForm(submission.toRequestDto()) }.map { }
}
