package com.smile.sniffer.mpesa

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://sandbox.safaricom.co.ke/"

    val instance: Mpesaservice by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Mpesaservice::class.java)
    }
}
