package com.berkeyilmaz.cardapp.presentation.scan_result.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
import com.berkeyilmaz.cardapp.domain.scan_result.model.getCategoryEnum

@Composable
fun TagChip(
    tag: Tag, onRemove: () -> Unit
) {
    val tagCategory = tag.getCategoryEnum()

    AssistChip(onClick = { }, label = {
        Text(
            text = tag.name, style = MaterialTheme.typography.bodyLarge, color = tagCategory.color
        )
    }, trailingIcon = {
        IconButton(
            onClick = onRemove, modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.remove_tag, tag.name),
                modifier = Modifier.size(20.dp),
                tint = tagCategory.color
            )
        }
    })
}