package com.berkeyilmaz.cardapp.presentation.main.more.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.berkeyilmaz.cardapp.R

data class MoreItem(
    val icon: ImageVector, val title: String, val onClick: () -> Unit
)

@Composable
fun MoreListItem(moreItem: MoreItem) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = moreItem.onClick),
        tonalElevation = dimensionResource(R.dimen.elevation_small),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = dimensionResource(R.dimen.padding_normal),
                vertical = dimensionResource(R.dimen.padding_lowNormal)
            ), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = moreItem.icon,
                contentDescription = moreItem.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacer_16)))
            Text(
                moreItem.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
            )
        }
    }
}