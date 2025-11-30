package com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel

import androidx.lifecycle.ViewModel
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.model.contacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class GroupsUiState {
    data object Idle : GroupsUiState()
    data object Loading : GroupsUiState()
    data class Success(
        val contacts: List<Contact>,
        val groups: List<String>,
        val selectedGroup: String
    ) : GroupsUiState()

    data object Error : GroupsUiState()
}

@HiltViewModel
class GroupsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        fetchContacts()
    }

    fun fetchContacts() {
        try {
            _uiState.value = GroupsUiState.Loading

            val fetchedContacts = contacts

            val groups = extractGroups(fetchedContacts)
            val defaultGroup = getMostRepeatedGroup(groups)

            _uiState.value = GroupsUiState.Success(
                contacts = fetchedContacts,
                groups = groups,
                selectedGroup = defaultGroup ?: ""
            )
        } catch (e: Exception) {
            _uiState.value = GroupsUiState.Error
        }

    }

    private fun extractGroups(contacts: List<Contact>): List<String> {
        return contacts
            .flatMap { it.tags.orEmpty().map { tag -> tag.name } }
            .distinct()
    }

    private fun getMostRepeatedGroup(groupNames: List<String>): String? {
        if (groupNames.isEmpty()) return null

        return groupNames
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }

    fun onGroupSelected(group: String) {
        _uiState.update { current ->
            if (current is GroupsUiState.Success)
                current.copy(selectedGroup = group)
            else current
        }
    }
}
