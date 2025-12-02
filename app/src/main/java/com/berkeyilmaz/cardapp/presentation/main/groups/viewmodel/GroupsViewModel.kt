package com.berkeyilmaz.cardapp.presentation.main.groups.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.contact.usecase.GetRemoteContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GroupsUiState {
    data object Idle : GroupsUiState()
    data object Loading : GroupsUiState()
    data class Success(
        val contacts: List<Contact>, val mainGroups: List<String>,    // Ana gruplar: İş, Okul vb.
        val selectedMainGroup: String,   // Kullanıcı hangi ana grupta
        val subGroups: List<String>      // Alt gruplar: MOVE ON, Google vb.
    ) : GroupsUiState()

    data object Error : GroupsUiState()
}


@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val getRemoteContacts: GetRemoteContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Idle)
    val uiState = _uiState.asStateFlow()


    fun fetchContacts() {
        viewModelScope.launch {
            try {
                _uiState.value = GroupsUiState.Loading

                val result = getRemoteContacts()


                val contacts = result.getOrNull()
                if (result.isFailure || contacts.isNullOrEmpty()) {
                    _uiState.value = GroupsUiState.Error
                    return@launch
                }
                Log.i("GroupsViewModel", "Fetched contacts: $contacts")
                // Tüm ana gruplar
                val mainGroups = extractMainGroups(contacts)
                Log.i("GroupsViewModel", "Extracted main groups: $mainGroups")

                // Varsayılan olarak en çok geçen grup seçilir
                val defaultMainGroup = getMostRepeatedMainGroup(contacts)
                Log.i("GroupsViewModel", "Default main group: $defaultMainGroup")

                // Eğer hiçbir grup yoksa boş state dön
                _uiState.value = GroupsUiState.Success(
                    contacts = contacts,
                    mainGroups = mainGroups,
                    selectedMainGroup = defaultMainGroup,
                    subGroups = extractSubGroups(contacts, defaultMainGroup)
                )
            } catch (e: Exception) {
                _uiState.value = GroupsUiState.Error
            }
        }

    }

    /** Tüm ana grupları (keys) döndür: İş, Okul, Etkinlik vb. */
    private fun extractMainGroups(contacts: List<Contact>): List<String> {
        return contacts.flatMap { it.groups.keys }.distinct()
    }

    /** En çok tekrar eden ana grup */
    private fun getMostRepeatedMainGroup(contacts: List<Contact>): String {
        return contacts.flatMap { it.groups.keys }.groupingBy { it }.eachCount()
            .maxByOrNull { it.value }?.key ?: ""
    }

    /** Bir ana gruba tıklandığında alt kategorileri çıkar */
    private fun extractSubGroups(
        contacts: List<Contact>, mainGroup: String
    ): List<String> {
        if (mainGroup.isBlank()) return emptyList()

        return contacts.flatMap { it.groups[mainGroup].orEmpty() }.distinct()
    }

    /** Kullanıcı main group seçtiğinde tetiklenir */
    fun onMainGroupSelected(mainGroup: String) {
        _uiState.update { state ->
            if (state is GroupsUiState.Success) {
                state.copy(
                    selectedMainGroup = mainGroup,
                    subGroups = extractSubGroups(state.contacts, mainGroup)
                )
            } else state
        }
    }
}


