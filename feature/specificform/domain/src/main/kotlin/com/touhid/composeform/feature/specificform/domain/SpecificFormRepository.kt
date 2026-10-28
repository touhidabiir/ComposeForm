package com.touhid.composeform.feature.specificform.domain

import com.touhid.composeform.model.AppResult

interface SpecificFormRepository {

    suspend fun getSpecificForm(): AppResult<SpecificForm>
}
