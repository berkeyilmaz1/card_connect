package com.berkeyilmaz.cardapp.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MainUiState(
    val isAiSheetOpen: Boolean = false, val isAiSheetExpanded: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    fun onAiFabClicked() {
        _uiState.update { it.copy(isAiSheetOpen = true) }
    }

    fun onSheetDismiss() {
        _uiState.update { MainUiState() }
    }

    fun expandAiSheet() {
        _uiState.update { it.copy(isAiSheetExpanded = true) }
    }
}
