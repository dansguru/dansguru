package com.smile.sniffer.api

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smile.sniffer.model.Ticket
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID

class FirestoreApiService {

    private val db = FirebaseFirestore.getInstance()
    private val ticketCollection = db.collection("tickets")
    private val storage = FirebaseStorage.getInstance().reference

    // Fetch tickets from Firestore
    suspend fun getTickets(): List<Ticket> {
        return try {
            val snapshot = ticketCollection.get().await()
            snapshot.toObjects(Ticket::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreApiService", "Error fetching tickets: ${e.message}")
            emptyList()
        }
    }

    // Add a new ticket to Firestore and return the document ID
    suspend fun addTicket(ticket: Ticket): String? {
        return try {
            val documentReference = ticketCollection.add(ticket).await()
            documentReference.id
        } catch (e: Exception) {
            Log.e("FirestoreApiService", "Error adding ticket: ${e.message}")
            null
        }
    }

    // Validate a ticket by QR code data
    suspend fun validateTicket(qrCodeData: String): Boolean {
        return try {
            val snapshot = ticketCollection.whereEqualTo("qrCodeData", qrCodeData).get().await()
            snapshot.documents.isNotEmpty()
        } catch (e: Exception) {
            Log.e("FirestoreApiService", "Error validating ticket: ${e.message}")
            false
        }
    }

    // Upload illustration to Firebase Storage and associate with a ticket
    suspend fun uploadIllustration(uri: Uri, ticketId: String, expirationDate: LocalDate?): Boolean {
        return try {
            val fileName = "${ticketId}_${UUID.randomUUID()}.jpg"
            val fileReference = storage.child("illustrations/$fileName")
            fileReference.putFile(uri).await()

            // Save illustration URL and expiration date to Firestore
            val downloadUrl = fileReference.downloadUrl.await().toString()
            val data = mapOf(
                "illustrationUrl" to downloadUrl,
                "expirationDate" to expirationDate?.toString()
            )
            ticketCollection.document(ticketId).update(data).await()

            Log.d("FirestoreApiService", "Illustration uploaded successfully: $fileName")
            true
        } catch (e: Exception) {
            Log.e("FirestoreApiService", "Error uploading illustration: ${e.message}")
            false
        }
    }

    suspend fun uploadPictureWithExpiry(uri: Uri, expirationDateTime: LocalDateTime): Boolean {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val fileReference = storage.child("pictures/$fileName")
            fileReference.putFile(uri).await()

            // Save picture URL and expiration datetime to Firestore
            val downloadUrl = fileReference.downloadUrl.await().toString()
            val data = mapOf(
                "pictureUrl" to downloadUrl,
                "expirationDateTime" to expirationDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            // Assuming there is a collection named "pictures" where we save this data
            db.collection("pictures").add(data).await()

            Log.d("FirestoreApiService", "Picture uploaded successfully: $fileName")
            true
        } catch (e: Exception) {
            Log.e("FirestoreApiService", "Error uploading picture: ${e.message}")
            false
        }
    }


    // Delete a ticket from Firestore and its associated illustration from Firebase Storage
    suspend fun deleteTicket(ticketId: String): Boolean {
        return try {
            val illustrationRef = storage.child("illustrations/$ticketId")
            illustrationRef.delete().await()

            ticketCollection.document(ticketId).delete().await()
            Log.d("FirestoreApiService", "Successfully deleted ticket and illustration for ID: $ticketId")
            true
        } catch (e: Exception) {
            Log.e("FirestoreApiService", "Error deleting ticket or illustration: ${e.message}")
            false
        }
    }

    fun fetchPicturesData(onDataReceived: (List<PictureData>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("pictures")
            .get()
            .addOnSuccessListener { documents ->
                val picturesList = mutableListOf<PictureData>()
                for (document in documents) {
                    val pictureData = document.toObject(PictureData::class.java)
                    picturesList.add(pictureData)
                }
                onDataReceived(picturesList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching data: ", exception)
            }
    }

    suspend fun fetchPicturesData(): List<PictureData> {
        return try {
            val snapshot = db.collection("pictures").get().await()
            snapshot.toObjects(PictureData::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreApiService", "Error fetching pictures data: ${e.message}")
            emptyList()
        }
    }

}

// In FirestoreApiService
data class PictureData(
    val expirationDateTime: String = "",
    val pictureUrl: String = "",
    val bitmap: Bitmap? = null // Add a nullable bitmap field
)
