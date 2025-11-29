package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ScanResponse(
    @SerializedName("extractedData") var extractedData: ExtractedData? = ExtractedData(),
    @SerializedName("imageUrl") var imageUrl: String? = null,
    @SerializedName("rawText") var rawText: String? = null
) : Serializable
