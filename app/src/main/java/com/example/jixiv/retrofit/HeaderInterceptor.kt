package com.example.jixiv.retrofit

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val headers: Headers) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .headers(headers)
            .build()
        return chain.proceed(newRequest)
    }
}