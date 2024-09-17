package com.smile.sniffer.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smile.sniffer.api.FirestoreApiService
import com.smile.sniffer.api.PictureData
import com.smile.sniffer.model.AppNotificationManager
import com.smile.sniffer.model.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class TicketCreationState {
    object Loading : TicketCreationState()
    object Success : TicketCreationState()
    data class Error(val message: String) : TicketCreationState()
}

class TicketViewModel(private val context: Context) : ViewModel() {

    private val firestoreApiService = FirestoreApiService()
    private val _ticketList = MutableStateFlow<List<Ticket>>(emptyList())
    val ticketList: StateFlow<List<Ticket>> = _ticketList


    private val _validationStatus = MutableStateFlow<String?>(null)
    val validationStatus: StateFlow<String?> = _validationStatus

    private val _scannedCodes = MutableStateFlow<List<String>>(emptyList())
    val scannedCodes: StateFlow<List<String>> = _scannedCodes

    private val _ticketCreationState =
        MutableStateFlow<TicketCreationState>(TicketCreationState.Success)
    val ticketCreationState: StateFlow<TicketCreationState> = _ticketCreationState

    private val _pictureData = MutableStateFlow<PictureData?>(null)
    val pictureData: StateFlow<PictureData?> = _pictureData

    init {
        loadTickets()
    }

    fun addTicket(
        title: String,
        description: String,
        prize: String,
        eventTime: String,
        place: String,
        attireStyle: String,
        openTime: String,
        closeTime: String,
        ticketType: String,
        expirationDate: String,
        illustrationUri: Uri? = null,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            _ticketCreationState.value = TicketCreationState.Loading
            try {
                val qrCodeData = generateQrCodeData(title, description)
                val newTicket = Ticket(
                    title = title,
                    description = description,
                    prize = prize,
                    eventTime = eventTime,
                    place = place,
                    attireStyle = attireStyle,
                    openTime = openTime,
                    closeTime = closeTime,
                    ticketType = ticketType,
                    expirationDate = expirationDate,
                    qrCodeData = qrCodeData
                )
                val ticketId = firestoreApiService.addTicket(newTicket)
                if (ticketId != null) {
                    illustrationUri?.let {
                        submitIllustration(it, LocalDate.parse(expirationDate), ticketId, onResult)
                    }
                        ?: onResult(Result.success(Unit)) // Ensure onResult is called if no illustration
                    _ticketList.value += newTicket.copy(id = ticketId)
                    _ticketCreationState.value = TicketCreationState.Success
                    AppNotificationManager.sendNotification(
                        context,
                        "Ticket Created",
                        "The ticket \"$title\" has been successfully created."
                    )
                } else {
                    handleTicketError("Error creating ticket.", onResult)
                }
            } catch (e: Exception) {
                handleTicketError("Error creating ticket: ${e.message}", onResult, e)
            }
        }
    }

    private fun generateQrCodeData(title: String, description: String): String {
        return "$title - $description"
    }

    fun validateTicket(qrCodeData: String) {
        // Your validation logic here
        _validationStatus.value = "Validated: $qrCodeData"

        // Update scanned codes history
        viewModelScope.launch {
            _scannedCodes.value = _scannedCodes.value + qrCodeData
        }
    }
    // Function to remove a scanned code

    fun clearScannedCodes() {
        _scannedCodes.value = emptyList()
    }

    fun removeScannedCode(code: String) {
        _scannedCodes.value = _scannedCodes.value.filterNot { it == code }
    }

    fun loadTickets() {
        viewModelScope.launch {
            val tickets = firestoreApiService.getTickets()
            _ticketList.value = tickets
        }
    }

    private fun handleTicketError(
        message: String,
        onResult: (Result<Unit>) -> Unit,
        exception: Exception? = null
    ) {
        _ticketCreationState.value = TicketCreationState.Error(message)
        onResult(Result.failure(exception ?: Exception(message)))
    }

    fun submitIllustration(
        imageUri: Uri,
        expirationDate: LocalDate,
        ticketId: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firestoreApiService.uploadIllustration(imageUri, ticketId, expirationDate)
                onResult(Result.success(Unit))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun uploadPicture(
        imageUri: Uri?,
        expirationDate: LocalDate?,
        expirationTime: LocalTime?,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        viewModelScope.launch {
            if (imageUri != null && expirationDate != null && expirationTime != null) {
                try {
                    val expirationDateTime = expirationDate.atTime(expirationTime)
                    firestoreApiService.uploadPictureWithExpiry(imageUri, expirationDateTime)
                    onResult(true, null)
                } catch (e: Exception) {
                    onResult(false, e.message)
                }
            } else {
                onResult(false, "Image or expiration date/time missing.")
            }
        }
    }



    fun deleteTicket(ticketId: String, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                // Reference to the ticket document in Firestore
                val ticketRef = FirebaseFirestore.getInstance().collection("tickets").document(ticketId)

                // Delete the ticket document
                ticketRef.delete().await()

                // Optional: Delete related data if needed
                // Example: Delete related images stored in Firebase Storage
                val imageRef = FirebaseStorage.getInstance().getReference("images/$ticketId")
                imageRef.delete().await()

                // Example: Delete comments related to the ticket
                val commentsQuery = ticketRef.collection("comments")
                val comments = commentsQuery.get().await()
                comments.documents.forEach { comment ->
                    comment.reference.delete().await()
                }

                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }



}
