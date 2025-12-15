package com.berkeyilmaz.cardapp.presentation.chat.widgets.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.presentation.chat.widgets.text_bubbles.AITextBubble

@Composable
fun SingleContactResultCard(contact: Contact, shouldShowFakeAiText: Boolean = true) {
    if (shouldShowFakeAiText) {
        AITextBubble(stringResource(R.string.here_is_the_contact_i_found))

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_lowNormal)))
    }

    Card(
        modifier = Modifier
            .wrapContentWidth()
            .widthIn(
                min = dimensionResource(R.dimen.card_width_min),
                max = dimensionResource(R.dimen.card_width_max)
            ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.spacer_2)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_lowNormal))
        ) {
            // İsim
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_xxsmall)),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacer_8)))
                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Organizasyon
            if (contact.organizationName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_4)))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_xsmall)),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacer_8)))
                    Text(
                        text = contact.organizationName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Aksiyon butonları
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_8)))
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacer_8))
            ) {
                AssistChip(onClick = { /* call */ }, label = {
                    Text(
                        stringResource(R.string.call), style = MaterialTheme.typography.labelSmall
                    )
                }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_xsmall))
                    )
                })
                AssistChip(
                    onClick = { /* email */ },
                    label = { Text("Email", style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_xsmall))
                        )
                    })
            }
        }
    }
}


@Composable
fun MultipleContactsResultCard(contacts: List<Contact>) {
    var isExpanded by remember { mutableStateOf(false) }
    Column {
        AITextBubble(stringResource(R.string.found_multiple_contacts))

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_lowNormal)))

        SingleContactResultCard(contacts.first(),false)

        if (isExpanded) {
            contacts.drop(1).forEach { contact ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_8)))
                SingleContactResultCard(contact, false)
            }
        }
        ExpandableButton(
            isExpanded = isExpanded,
            remainingCount = contacts.size - 1,
            onClick = { isExpanded = !isExpanded })
    }
}

@Composable
private fun ExpandableButton(
    isExpanded: Boolean, remainingCount: Int, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(R.dimen.padding_normal),
                vertical = dimensionResource(R.dimen.padding_small)
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_small)),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacer_8)))
        Text(
            text = if (isExpanded) {
                stringResource(R.string.show_less)
            } else {
                stringResource(R.string.show_more, remainingCount)
            },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

