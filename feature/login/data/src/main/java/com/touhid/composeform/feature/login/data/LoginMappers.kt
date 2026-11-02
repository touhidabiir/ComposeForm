package com.touhid.composeform.feature.login.data

import com.touhid.composeform.session.domain.UserSession
import com.touhid.composeform.session.domain.UserType

// null for a role the app doesn't know.
internal fun LoginResponseDto.toUserSession(): UserSession? {
    val userType = when (role) {
        "approver" -> UserType.Approver
        "agent" -> UserType.Agent
        else -> return null
    }
    return UserSession(userId = userId, name = name, userType = userType)
}
