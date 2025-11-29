package com.berkeyilmaz.cardapp.core.extensions

fun String.toFirebaseStorageUrl(): String {
    return if (this.startsWith("gs://")) {
        this.replace("gs://", "https://storage.googleapis.com/")
    } else {
        this
    }
}

