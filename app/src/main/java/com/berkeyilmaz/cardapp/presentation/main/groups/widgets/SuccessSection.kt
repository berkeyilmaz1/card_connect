package com.berkeyilmaz.cardapp.presentation.main.groups.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppTitle
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.scan_result.model.TagCategory
import kotlin.text.ifEmpty

@Composable
fun SuccessSection(
    mainGroups: List<String>,
    selectedMainGroup: String,
    subGroups: List<String>,
    selectedSubGroup: String?,
    contacts: List<Contact>,
    onMainGroupSelected: (String) -> Unit,
    onSubGroupSelected: (String?) -> Unit
) {
    val scrollState = rememberScrollState()
    var showSubGroups by remember { mutableStateOf(false) }

    // Ana grup değiştiğinde animasyonu tetikle
    LaunchedEffect(selectedMainGroup, subGroups) {
        showSubGroups = false
        kotlinx.coroutines.delay(150) // Animasyon için daha uzun delay
        showSubGroups = true
    }
    Column(modifier = Modifier.fillMaxSize()) {
        AppTitle(stringResource(R.string.groups))

        /** --- ANA GROUP CHIP ROW --- (İş, Okul, Etkinlik) - SÜREKLI DURUR */
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(bottom = 8.dp)
        ) {
            mainGroups.forEach { group ->
                val categoryColor = TagCategory.fromString(group).color

                FilterChip(
                    selected = selectedMainGroup == group,
                    onClick = { onMainGroupSelected(group) },
                    label = { Text(group) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = categoryColor,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = categoryColor.copy(alpha = 0.2f),
                        labelColor = categoryColor
                    )
                )
                Spacer(Modifier.width(8.dp))
            }
        }

        /** --- ALT GROUP CHIP ROW - ANİMASYONLU --- (MOVE ON, Google, Yazılım Ekibi) */
        AnimatedVisibility(
            visible = showSubGroups && subGroups.isNotEmpty(),
            enter = fadeIn(
                animationSpec = tween(durationMillis = 500, delayMillis = 100)
            ) + expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
                ), expandFrom = Alignment.Top
            ),
            exit = fadeOut(animationSpec = tween(250)) + shrinkVertically(animationSpec = tween(250))
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp)
            ) {
                // "Tümü" chip'i ekle
                val categoryColor = TagCategory.fromString(selectedMainGroup).color

                FilterChip(
                    selected = selectedSubGroup == null,
                    onClick = { onSubGroupSelected(null) },
                    label = { Text(stringResource(R.string.all)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = categoryColor,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = categoryColor.copy(alpha = 0.1f),
                        labelColor = categoryColor.copy(alpha = 0.7f)
                    )
                )
                Spacer(Modifier.width(8.dp))

                subGroups.forEach { sub ->
                    FilterChip(
                        selected = selectedSubGroup == sub, onClick = {
                            onSubGroupSelected(if (selectedSubGroup == sub) null else sub)
                        }, label = { Text(sub) }, colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = categoryColor.copy(alpha = 0.8f),
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                            containerColor = categoryColor.copy(alpha = 0.15f),
                            labelColor = categoryColor.copy(alpha = 0.9f)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
        }

        /** --- CONTACT LIST - İKİ SEVİYELİ FİLTRELEME --- */
        val filteredContacts = contacts.filter { contact ->
            val hasMainGroup = contact.tags.any { it.category == selectedMainGroup }

            if (selectedSubGroup == null) {
                // Alt grup seçilmemişse sadece ana grup filtresi
                hasMainGroup
            } else {
                // Alt grup seçiliyse hem ana grup hem alt grup filtresi
                hasMainGroup && contact.tags.any { it.category == selectedMainGroup && it.name == selectedSubGroup }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall))
        ) {
            items(filteredContacts) { contact ->
                ContactCard(contact = contact)
            }
        }
    }

}

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Handle click */ },
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_normal)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.elevation_xSmall)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_normal)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_normal))
        ) {
            // Avatar with gradient background
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacer_48))
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.fullName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Contact info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xxSmall))
            ) {
                Text(
                    text = contact.fullName.ifEmpty { stringResource(R.string.unnamed) },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}