package com.smile.sniffer.model


data class Ticket(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val qrCodeData: String = "",
    val prize: String = "",
    val eventTime: String = "",
    val place: String = "",
    val attireStyle: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val ticketType: String = "",
    val illustrationUrl: String = "",
    val pictureUrl: String = "",
    val expirationDateTime: String = "",
    val pictureId: String = "",
    val imageUrl: String = "",
    val expirationDate: String = "",
    val isValid: Boolean = true // Assume tickets are valid by default
)

