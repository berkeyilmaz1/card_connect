package com.berkeyilmaz.cardapp.domain.scan_result.model

import androidx.compose.runtime.Composable

data class ScanResultItem(
    val title: String = "", val content: @Composable () -> Unit
)