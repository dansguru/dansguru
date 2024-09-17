package com.smile.sniffer.mpesa

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.Credentials

interface AuthService {
    @POST("oauth/v1/generate?grant_type=client_credentials")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String
    ): AccessTokenResponse
}

data class AccessTokenResponse(
    val access_token: String,
    val expires_in: Int
)

suspend fun getAccessToken(): String {
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", Credentials.basic(
                    "uBKMwus47OsFHGMcyL1OItI9QxGIWznvMRGj81F24ok3p7YE",
                    "BiwfXqKOfJ69hi8M58UpQyTHplPRTMg0FGgNsyESykW1GNKliIE1HQqH9kwC1kfh"
                ))
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://sandbox.safaricom.co.ke/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val authService = retrofit.create(AuthService::class.java)
    val response = authService.getAccessToken("client_credentials")
    return response.access_token
}
