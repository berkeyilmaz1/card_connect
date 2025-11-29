package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.annotations.SerializedName

data class ContactRequest(
    @SerializedName("confirmedData") var confirmedData: ConfirmedData? = ConfirmedData(),
    @SerializedName("imageUrl") var imageUrl: String? = null,
    @SerializedName("rawText") var rawText: String? = null,
    @SerializedName("note") var note: String? = null,
)