package com.berkeyilmaz.cardapp.presentation.contact_detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.scan_result.model.SocialMedia
import com.berkeyilmaz.cardapp.presentation.contact_detail.viewmodel.ContactDetailUiState
import com.berkeyilmaz.cardapp.presentation.main.contact.widgets.ContactAvatar
import com.berkeyilmaz.cardapp.presentation.scan_result.widgets.TagChip

@Composable
fun ContactDetailView(
    uiState: ContactDetailUiState,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is ContactDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ContactDetailUiState.Error -> {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(dimensionResource(R.dimen.padding_normal)),
                    textAlign = TextAlign.Center
                )
            }

            is ContactDetailUiState.Success -> {
                ContactDetailContent(contact = uiState.contact, onNavigateBack = onNavigateBack)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContactDetailContent(contact: Contact, onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ) {
        // Hero section
        item {
            HeroSection(
                contact = contact,
                onNavigateBack = onNavigateBack,
                onCallClick = {
                    contact.phones.firstOrNull()?.let { phone ->
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        )
                    }
                },
                onEmailClick = {
                    contact.emails.firstOrNull()?.let { email ->
                        context.startActivity(
                            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                        )
                    }
                }
            )
        }

        // Phones
        if (contact.phones.isNotEmpty()) {
            item {
                InfoCard(
                    icon = Icons.Outlined.Phone,
                    label = stringResource(R.string.phones)
                ) {
                    contact.phones.forEach { phone ->
                        ClickableInfoRow(
                            text = phone,
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                )
                            }
                        )
                    }
                }
            }
        }

        // Emails
        if (contact.emails.isNotEmpty()) {
            item {
                InfoCard(
                    icon = Icons.Outlined.Email,
                    label = stringResource(R.string.email)
                ) {
                    contact.emails.forEach { email ->
                        ClickableInfoRow(
                            text = email,
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                                )
                            }
                        )
                    }
                }
            }
        }

        // Address
        if (contact.address.isNotEmpty()) {
            item {
                InfoCard(
                    icon = Icons.Outlined.LocationOn,
                    label = stringResource(R.string.address)
                ) {
                    ClickableInfoRow(
                        text = contact.address,
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("geo:0,0?q=${Uri.encode(contact.address)}")
                                )
                            )
                        }
                    )
                }
            }
        }

        // Websites
        if (contact.websites.isNotEmpty()) {
            item {
                InfoCard(
                    icon = Icons.Outlined.Language,
                    label = stringResource(R.string.websites)
                ) {
                    contact.websites.forEach { url ->
                        ClickableInfoRow(
                            text = url,
                            onClick = {
                                val uri = if (url.startsWith("http")) Uri.parse(url)
                                else Uri.parse("https://$url")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        )
                    }
                }
            }
        }

        // Social Media
        if (contact.socialMedias.isNotEmpty()) {
            item {
                SocialMediaCard(
                    socialMedias = contact.socialMedias,
                    onUrlClick = { url ->
                        val uri = if (url.startsWith("http")) Uri.parse(url)
                        else Uri.parse("https://$url")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                )
            }
        }

        // Tags
        if (contact.tags.isNotEmpty()) {
            item {
                InfoCard(
                    icon = Icons.Outlined.Label,
                    label = stringResource(R.string.tags)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall))
                    ) {
                        contact.tags.forEach { tag ->
                            TagChip(tag = tag, onRemove = {})
                        }
                    }
                }
            }
        }

        // Notes
        if (contact.note.isNotEmpty()) {
            item {
                InfoCard(
                    icon = Icons.Outlined.Notes,
                    label = stringResource(R.string.note)
                ) {
                    Text(
                        text = contact.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large))) }
    }
}

@Composable
private fun HeroSection(
    contact: Contact,
    onNavigateBack: () -> Unit,
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(R.dimen.padding_normal),
                bottom = dimensionResource(R.dimen.padding_normal),
                start = dimensionResource(R.dimen.padding_normal),
                end = dimensionResource(R.dimen.padding_normal)
            )
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall))
        ) {
            ContactAvatar(name = contact.fullName, size = R.dimen.spacer_96)

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xSmall)))

            Text(
                text = contact.fullName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            if (contact.title.isNotEmpty()) {
                Text(
                    text = contact.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            if (contact.organization.isNotEmpty()) {
                Text(
                    text = contact.organization,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

            ActionRow(
                onCallClick = onCallClick,
                onEmailClick = onEmailClick,
                hasPhone = contact.phones.isNotEmpty(),
                hasEmail = contact.emails.isNotEmpty()
            )
        }
    }
}

@Composable
private fun ActionRow(
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    hasPhone: Boolean,
    hasEmail: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = Icons.Outlined.Phone,
            label = stringResource(R.string.call),
            enabled = hasPhone,
            onClick = onCallClick
        )
        ActionButton(
            icon = Icons.Outlined.Email,
            label = stringResource(R.string.email),
            enabled = hasEmail,
            onClick = onEmailClick
        )
        ActionButton(
            icon = Icons.Outlined.Share,
            label = stringResource(R.string.share),
            enabled = false,
            onClick = {}
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (enabled) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xxSmall)),
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.spacer_48))
                .clip(CircleShape)
                .background(
                    if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(dimensionResource(R.dimen.spacer_24))
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_normal)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.elevation_xSmall)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_normal)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
private fun ClickableInfoRow(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(R.dimen.padding_xxSmall))
    )
}

@Composable
private fun SocialMediaCard(
    socialMedias: List<SocialMedia>,
    onUrlClick: (String) -> Unit
) {
    InfoCard(
        icon = Icons.Outlined.AccountCircle,
        label = stringResource(R.string.social_media)
    ) {
        socialMedias.forEach { social ->
            if (!social.url.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUrlClick(social.url!!) }
                        .padding(vertical = dimensionResource(R.dimen.padding_xxSmall)),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = social.platform?.platformName ?: stringResource(R.string.other),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(dimensionResource(R.dimen.spacer_64))
                    )
                    Text(
                        text = social.url!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
