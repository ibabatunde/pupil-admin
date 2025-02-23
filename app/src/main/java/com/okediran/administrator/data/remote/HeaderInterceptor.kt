package com.okediran.administrator.data.remote

import com.okediran.administrator.BuildConfig
import okhttp3.Interceptor
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Request-ID", BuildConfig.X_REQUEST_ID)
            .addHeader("User-Agent", BuildConfig.USER_AGENT)
            .build()
        return chain.proceed(request)
    }
}