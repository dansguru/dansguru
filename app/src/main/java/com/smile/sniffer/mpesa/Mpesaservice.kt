package com.smile.sniffer.mpesa

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Header
import retrofit2.http.POST

interface Mpesaservice {
    @POST("mpesa/stkpush/v1/processrequest")
    @Headers("Content-Type: application/json")
    suspend fun stkPushRequest(
        @Header("Authorization") authHeader: String,
        @Body request: StkPushRequest
    ): retrofit2.Response<StkPushResponse>  // Ensure it returns retrofit2.Response
}

data class StkPushRequest(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val TransactionType: String,
    val Amount: String,
    val PartyA: String,
    val PartyB: String,
    val PhoneNumber: String,
    val CallBackURL: String,
    val AccountReference: String,
    val TransactionDesc: String
)

data class StkPushResponse(
    val CheckoutRequestID: String,
    val ResponseCode: String,
    val ResponseDescription: String
)
