package com.berkeyilmaz.cardapp.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class QrCodeScanner @Inject constructor() {

    private val scanner: BarcodeScanner by lazy {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAllPotentialBarcodes()
                .build()
        )
    }

    suspend fun scanForQrCodes(file: File, context: Context): List<String> {
        return try {
            val inputImage = InputImage.fromFilePath(context, Uri.fromFile(file))
            val barcodes = scanner.process(inputImage).await()
            barcodes.mapNotNull { it.rawValue }
        } catch (e: Exception) {
            Log.w("QrCodeScanner", "QR scan failed silently: ${e.message}")
            emptyList()
        }
    }
}
