package com.berkeyilmaz.cardapp.presentation.main.home.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateContactGroup
import com.berkeyilmaz.cardapp.domain.contact.model.DuplicateMatchReason
import com.berkeyilmaz.cardapp.domain.contact.model.InternalContact
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.DuplicateBottomSheetState

sealed class PrimarySelection {
    data class Internal(val contact: InternalContact) : PrimarySelection()
}

@Composable
fun DuplicateContactsBottomSheet(
    state: DuplicateBottomSheetState,
    onMerge: (DuplicateContactGroup, PrimarySelection) -> Unit,
    onSkip: () -> Unit,
    onDismissAll: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            is DuplicateBottomSheetState.Scanning -> ScanningContent()
            is DuplicateBottomSheetState.Found -> FoundContent(
                state = state,
                onMerge = onMerge,
                onSkip = onSkip,
                onDismissAll = onDismissAll
            )
            is DuplicateBottomSheetState.Error -> DuplicateErrorContent(
                message = state.message,
                onDismiss = onDismiss
            )
            is DuplicateBottomSheetState.Hidden -> Unit
        }
    }
}

@Composable
private fun ScanningContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.scanning_duplicates),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FoundContent(
    state: DuplicateBottomSheetState.Found,
    onMerge: (DuplicateContactGroup, PrimarySelection) -> Unit,
    onSkip: () -> Unit,
    onDismissAll: () -> Unit
) {
    val group = state.groups[state.currentIndex]
    var selectedPrimary by remember(state.currentIndex) {
        mutableStateOf<PrimarySelection?>(
            group.internalContacts.firstOrNull()?.let { PrimarySelection.Internal(it) }
        )
    }

    Text(
        text = stringResource(R.string.duplicate_contacts_found),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Progress indicator
    Text(
        text = stringResource(
            R.string.duplicate_group_progress,
            state.currentIndex + 1,
            state.groups.size
        ),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Match reason badge
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = when (group.matchReason) {
                DuplicateMatchReason.PHONE -> stringResource(R.string.duplicate_match_phone)
                DuplicateMatchReason.EMAIL -> stringResource(R.string.duplicate_match_email)
                DuplicateMatchReason.PHONE_AND_EMAIL -> stringResource(R.string.duplicate_match_phone) + " & " + stringResource(R.string.duplicate_match_email)
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Contacts list — sadece telefon rehberi kişileri gösterilir
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height((group.internalContacts.size * 120).coerceAtMost(320).dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(group.internalContacts) { internal ->
                InternalContactItem(
                    internal = internal,
                    isSelected = selectedPrimary is PrimarySelection.Internal &&
                            (selectedPrimary as PrimarySelection.Internal).contact.contactId == internal.contactId,
                    onSelect = { selectedPrimary = PrimarySelection.Internal(internal) }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Merge button
    Button(
        onClick = {
            selectedPrimary?.let { onMerge(group, it) }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = selectedPrimary != null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = stringResource(R.string.merge_contacts),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Skip button
    OutlinedButton(
        onClick = onSkip,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = stringResource(R.string.skip_group),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }

    if (state.groups.size > 1) {
        androidx.compose.material3.TextButton(
            onClick = onDismissAll,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.dismiss_all_duplicates),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun InternalContactItem(
    internal: InternalContact,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = internal.fullName.ifEmpty { stringResource(R.string.unknown_contact) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!internal.organization.isNullOrEmpty()) {
                    Text(
                        text = internal.organization,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (internal.phones.isNotEmpty()) {
                    Text(
                        text = internal.phones.first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!internal.emails.isNullOrEmpty()) {
                    Text(
                        text = internal.emails.first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = stringResource(R.string.internal_contact_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DuplicateErrorContent(
    message: String,
    onDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.analysis_error),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Cancel,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.close),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
