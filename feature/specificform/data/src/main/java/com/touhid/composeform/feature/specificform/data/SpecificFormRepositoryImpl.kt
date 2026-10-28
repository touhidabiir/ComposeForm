package com.touhid.composeform.feature.specificform.data

import com.touhid.composeform.feature.specificform.domain.SpecificForm
import com.touhid.composeform.feature.specificform.domain.SpecificFormRepository
import com.touhid.composeform.model.AppResult
import com.touhid.composeform.model.map
import com.touhid.composeform.network.safeAppCall
import javax.inject.Inject

internal class SpecificFormRepositoryImpl @Inject constructor(
    private val api: SpecificFormApi,
) : SpecificFormRepository {

    override suspend fun getSpecificForm(): AppResult<SpecificForm> =
        safeAppCall { api.getSpecificForm().data }.map { it.toDomain() }
}
