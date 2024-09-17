package com.smile.sniffer.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeGenerator {

    fun generateQRCode(content: String, width: Int = 500, height: Int = 500): Bitmap {
        if (content.isBlank()) {
            throw IllegalArgumentException("Content for QR Code must not be empty")
        }

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()  // Black or white
                bitmap.setPixel(x, y, color)
            }
        }
        return bitmap
    }
}
