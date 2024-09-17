package com.smile.sniffer.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.smile.sniffer.viewmodel.TicketViewModel
import java.util.concurrent.Executors

class QRCodeScanner(private val ticketViewModel: TicketViewModel) {

    var isFlashlightOn = false
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraControl: CameraControl? = null
    private val scannedCodes = mutableListOf<String>()

    fun startScan(context: Context, previewView: PreviewView) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), QRCodeAnalyzer { qrCodeData ->
                        if (qrCodeData !in scannedCodes) { // Avoid duplicate entries
                            scannedCodes.add(qrCodeData)
                            ticketViewModel.validateTicket(qrCodeData)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val camera = cameraProvider?.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )

            cameraControl = camera?.cameraControl
        }, ContextCompat.getMainExecutor(context))
    }

    fun toggleFlashlight() {
        cameraControl?.enableTorch(!isFlashlightOn)
        isFlashlightOn = !isFlashlightOn
    }

    fun getScannedCodes(): List<String> = scannedCodes

    private class QRCodeAnalyzer(private val onQRCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            // Correct the image rotation
            val rotationDegrees = image.imageInfo.rotationDegrees
            val rotatedSource = getRotatedLuminanceSource(bytes, image.width, image.height, rotationDegrees)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(rotatedSource))

            try {
                val result = MultiFormatReader().decode(binaryBitmap)
                onQRCodeScanned(result.text)
            } catch (e: NotFoundException) {
                // Handle case when no QR code is found in the image
            } finally {
                image.close()
            }
        }

        private fun getRotatedLuminanceSource(bytes: ByteArray, width: Int, height: Int, rotationDegrees: Int): LuminanceSource {
            val source = PlanarYUVLuminanceSource(bytes, width, height, 0, 0, width, height, false)
            return when (rotationDegrees) {
                90 -> source.rotateCounterClockwise()
                180 -> rotate180(source)
                270 -> source.rotateCounterClockwise().rotateCounterClockwise().rotateCounterClockwise()
                else -> source // Default no rotation
            }
        }

        // Manually rotate 180 degrees since PlanarYUVLuminanceSource doesn't support rotate180 natively
        private fun rotate180(source: PlanarYUVLuminanceSource): PlanarYUVLuminanceSource {
            val rotatedData = ByteArray(source.matrix.size)
            val width = source.width
            val height = source.height

            for (y in 0 until height) {
                for (x in 0 until width) {
                    rotatedData[width * (height - 1 - y) + (width - 1 - x)] = source.matrix[width * y + x]
                }
            }

            return PlanarYUVLuminanceSource(rotatedData, width, height, 0, 0, width, height, false)
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }
}
