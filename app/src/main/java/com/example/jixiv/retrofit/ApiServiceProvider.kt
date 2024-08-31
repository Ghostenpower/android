package com.example.jixiv.retrofit

object ApiServiceProvider {

    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}