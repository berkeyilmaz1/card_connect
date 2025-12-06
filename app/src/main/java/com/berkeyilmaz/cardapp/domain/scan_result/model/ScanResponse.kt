package com.berkeyilmaz.cardapp.domain.scan_result.model

import java.io.Serializable

data class ScanResponse(
    var extractedData: ExtractedData? = ExtractedData(),
    var imageUrl: String? = null,
    var rawText: String? = null
) : Serializable

