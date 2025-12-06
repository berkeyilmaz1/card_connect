package com.berkeyilmaz.cardapp.core.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.core.common.UiEvent
import com.berkeyilmaz.cardapp.core.common.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * A generic base ViewModel class to manage UI state and events.
 *
 * @param T The type of data managed by the UI state.
 * @param EVENT The type of UI events.
 *
 * Usage:
 * ```
 * class MyViewModel : BaseViewModel<MyDataType, MyUiEvent>() {
 *    // ViewModel implementation
 *    }
 *    ```
 * @see UiState
 * @see UiEvent
 */
abstract class BaseViewModel<T : Any, EVENT : UiEvent> : ViewModel() {

    // UI Management
    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    // Event Management
    private val _eventFlow = Channel<EVENT>(Channel.BUFFERED)
    val eventFlow = _eventFlow.receiveAsFlow()

    /**
     * Current state value
     */
    // Gets the current UI state value
    protected val currentState: UiState<T>
        get() = uiState.value

    // Sets the UI state to Loading
    protected fun setLoading() {
        _uiState.value = UiState.Loading
    }

    // Sets the UI state to Success with the provided data
    protected fun setSuccess(data: T) {
        _uiState.value = UiState.Success(data)
    }

    // Sets the UI state to Error with the provided message resource
    protected fun setError(@StringRes message: Int) {
        _uiState.value = UiState.Error(message)
    }

    // Updates the current success state data with the provided transform function
    protected fun updateSuccessData(update: (T) -> T) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = UiState.Success(update(currentState.data))
        }
    }

    // Sends a UI event to the event flow
    protected fun sendEvent(event: EVENT) {
        viewModelScope.launch {
            _eventFlow.send(event)
        }
    }

    // Executes a block with loading state management and error handling
    protected fun executeWithLoading(
        @StringRes errorMessage: Int,
        block: suspend () -> T,
    ) {
        viewModelScope.launch {
            try {
                setLoading()
                val result = block()
                setSuccess(result)
            } catch (e: Exception) {
                setError(errorMessage)
            }
        }
    }
}