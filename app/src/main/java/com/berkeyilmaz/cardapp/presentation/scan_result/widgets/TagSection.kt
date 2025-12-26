package com.berkeyilmaz.cardapp.presentation.scan_result.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
import kotlin.collections.forEach

@Composable
fun TagsSection(
    tags: List<Tag>, onTagRemove: (Tag) -> Unit, onTagAdd: (Tag) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            TagChip(
                tag = tag, onRemove = { onTagRemove(tag) })
        }
    }
}