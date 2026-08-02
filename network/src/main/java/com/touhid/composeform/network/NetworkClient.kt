package com.touhid.composeform.network

import retrofit2.Retrofit
import javax.inject.Inject

class NetworkClient @Inject internal constructor(private val retrofit: Retrofit) {
    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
