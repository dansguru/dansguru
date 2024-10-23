package com.smile.sniffer.aiFeature

// Necessary imports
import android.content.Context
import android.util.Log
import android.widget.Toast
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.google.gson.JsonObject

// Retrofit API Interface
interface StableDiffusionApi {
    @Headers("Content-Type: application/json")
    @POST("/api/v3/text2img")
    suspend fun generateImages(@Body jsonObject: JsonObject): ImageResponse
}

// Data class to handle the response
data class ImageResponse(val output: List<String>)

// Image Generator Class
class ImageGenerator(private val context: Context) {
    private val apiKey = "FBXnSlhX0g0m4ihTh7pqOVZRwFb41HDFnJfUKeyh3S9C6YUaURudNtkS7gfh"
    private val baseUrl = "https://stablediffusionapi.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(StableDiffusionApi::class.java)

    suspend fun generate(prompt: String, negativePrompt: String, width: Int, height: Int, count: Int, onLoaded: (List<String>) -> Unit) {
        val jsonRequest = JsonObject().apply {
            addProperty("key", apiKey)
            addProperty("prompt", prompt)
            addProperty("negative_prompt", negativePrompt)
            addProperty("width", width)
            addProperty("height", height)
            addProperty("samples", count)
            addProperty("num_inference_steps", "20")
            addProperty("guidance_scale", "7.5")
        }

        try {
            val response = api.generateImages(jsonRequest)

            // Log response for debugging
            Log.d("ImageGenerator", "API Response: $response")

            if (response.output.isNotEmpty()) {
                onLoaded(response.output)
            } else {
                showToast("No images generated. Please check your prompt.")
                Log.d("ImageGenerator", "No images found in the response.")
            }
        } catch (e: Exception) {
            showToast("There was an error while getting images: ${e.message}")
            Log.e("ImageGenerator", "Exception: ${e.message}", e)
        }
    }

    private fun showToast(message: String) {
        // Use Main context to show Toast
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
