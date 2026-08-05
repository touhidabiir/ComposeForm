package com.touhid.composeform.di

import com.touhid.composeform.network.auth.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryTokenProvider @Inject constructor() : TokenProvider {

    @Volatile
    private var token: String? = null

    override fun getToken(): String? = token

    override fun setToken(token: String?) {
        this.token = token
    }
}
