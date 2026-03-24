package com.berkeyilmaz.cardapp.presentation.scan_result.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.scan_result.model.Tag
import com.berkeyilmaz.cardapp.domain.scan_result.model.TagCategory
import com.berkeyilmaz.cardapp.domain.scan_result.model.getCategoryEnum
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsSection(
    tags: List<Tag>,
    onTagRemove: (Tag) -> Unit,
    onTagAdd: (Tag) -> Unit,
    onTagEdit: (old: Tag, new: Tag) -> Unit
) {
    var dialogTag by remember { mutableStateOf<Tag?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            TagChip(
                tag = tag,
                onRemove = { onTagRemove(tag) },
                onClick = { dialogTag = tag }
            )
        }
        IconButton(onClick = { showAddDialog = true }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_tag)
            )
        }
    }

    dialogTag?.let { tag ->
        TagEditDialog(
            title = stringResource(R.string.edit_tag),
            initialName = tag.name,
            initialCategory = tag.getCategoryEnum(),
            onConfirm = { newName, newCategory ->
                onTagEdit(tag, Tag(category = newCategory.name, name = newName))
                dialogTag = null
            },
            onDismiss = { dialogTag = null }
        )
    }

    if (showAddDialog) {
        TagEditDialog(
            title = stringResource(R.string.add_tag),
            initialName = "",
            initialCategory = TagCategory.PERSONAL,
            onConfirm = { newName, newCategory ->
                onTagAdd(Tag(category = newCategory.name, name = newName))
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagEditDialog(
    title: String,
    initialName: String,
    initialCategory: TagCategory,
    onConfirm: (name: String, category: TagCategory) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(stringResource(R.string.tag_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        TagCategory.entries.forEach { entry ->
                            DropdownMenuItem(
                                text = { Text(text = entry.displayName, color = entry.color) },
                                onClick = {
                                    category = entry
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isNotEmpty()) onConfirm(trimmed, category)
                }
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
