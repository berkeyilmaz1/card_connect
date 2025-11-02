package com.berkeyilmaz.cardapp.presentation.main.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.data.model.ApiResult
import com.berkeyilmaz.cardapp.domain.home.usecase.GetCurrentUserUseCase
import com.berkeyilmaz.cardapp.presentation.main.home.QuickActionOption
import com.berkeyilmaz.cardapp.presentation.main.home.models.HomeNotification
import com.berkeyilmaz.cardapp.presentation.main.home.models.NotificationID
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val userProfileImageUrl: String? = null,
    val notificationList: List<HomeNotification> = emptyList(),
    val recentlyScannedCards: List<Any> = emptyList() // Replace with actual data model
)

sealed class HomeInUiEvent {
    data class ShowError(val message: String) : HomeInUiEvent()
    data class Navigate(val route: String) : HomeInUiEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeInUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val quickActionOptions = listOf(
        QuickActionOption(
            title = context.getString(R.string.add_new),
            icon = Icons.Outlined.Add,
            route = Screen.Main.Scan.route
        ), QuickActionOption(
            title = context.getString(R.string.history), icon = Icons.Outlined.History, route = ""
        ), QuickActionOption(
            title = context.getString(R.string.favorites),
            icon = Icons.Outlined.StarOutline,
            route = "{}"
        ), QuickActionOption(
            title = context.getString(R.string.settings),
            icon = Icons.Outlined.Settings,
            route = Screen.Main.Settings.route
        )
    )


    init {
        viewModelScope.launch {
            getCurrentUser()
            checkUserIsVerified()
            checkUserProfileComplete()
        }
    }

    suspend fun checkUserIsVerified() {
        val isVerified = getCurrentUserUseCase().let { result ->
            if (result is ApiResult.Success) {
                Log.d("BerkeTag", "checkUserIsVerified result: ${result.data?.isEmailVerified}")
                result.data?.isEmailVerified ?: false
            } else {
                false
            }
        }

        if (!isVerified) {
            addNotification(
                HomeNotification(
                    id = NotificationID.EMAIL_VERIFICATION,
                    title = context.getString(R.string.notification_email_verification_title),
                    subtitle = context.getString(R.string.notification_email_verification_subtitle),
                    buttonText = context.getString(R.string.notification_email_verification_button),
                    onButtonClick = {})
            )
        }
    }

    suspend fun checkUserProfileComplete() {
        val isProfileComplete = getCurrentUserUseCase().let { result ->
            Log.d("BerkeTag", "checkUserProfileComplete result: $result")
            if (result is ApiResult.Success) {
                val user = result.data
                !user?.displayName.isNullOrEmpty() && !user.phoneNumber.isNullOrEmpty()// todo: check other profile fields when added
            } else {
                false
            }
        }
        if (!isProfileComplete) {
            addNotification(
                HomeNotification(
                    id = NotificationID.PROFILE_INCOMPLETE,
                    title = context.getString(R.string.notification_profile_incomplete_title),
                    subtitle = context.getString(R.string.notification_profile_incomplete_subtitle),
                    buttonText = context.getString(R.string.notification_profile_incomplete_button),
                    onButtonClick = {})
            )
        }
    }

    suspend fun getCurrentUser() {
        setLoading(true)
        val result = getCurrentUserUseCase()
        if (result is ApiResult.Error) return updateErrorState(
            context.getString(R.string.failed_to_load_user_data)
        )
        val user = (result as ApiResult.Success).data
        try {
            Log.d(
                "BerkeTag",
                "User data loaded name: ${user?.displayName}, photoUrl: ${user?.photoUrl}"
            )
            updateUserState(
                user?.displayName ?: "", user?.photoUrl?.toString() ?: ""
            )
        } catch (_: Exception) {
            updateErrorState(context.getString(R.string.failed_to_load_user_data))
        } finally {
            setLoading(false)
        }
    }

    fun addNotification(notification: HomeNotification) {
        _uiState.update {
            val updatedList = it.notificationList.toMutableList().apply {
                add(notification)
            }
            it.copy(notificationList = updatedList)
        }
    }

    fun removeNotification(notification: HomeNotification) {
        _uiState.update {
            it.copy(notificationList = it.notificationList.filter { n -> n.id != notification.id })
        }
    }

    private fun updateUserState(name: String, profileImageUrl: String) {
        _uiState.update {
            it.copy(
                userName = name, userProfileImageUrl = profileImageUrl
            )
        }
    }

    private fun updateErrorState(error: String) {
        viewModelScope.launch { _uiEvent.emit(HomeInUiEvent.ShowError(error)) }
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }
}