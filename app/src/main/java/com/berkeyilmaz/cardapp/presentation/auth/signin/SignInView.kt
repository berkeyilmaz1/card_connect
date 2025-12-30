package com.berkeyilmaz.cardapp.presentation.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInUiEvent
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInViewModel
import com.berkeyilmaz.cardapp.presentation.auth.signin.widgets.SignInContent
import com.berkeyilmaz.cardapp.presentation.auth.signin.widgets.TermsAndConditionSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInView(
    onNavigateToMain: () -> Unit,
    onNavigateForgotPassword: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel<SignInViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignInUiEvent.ShowError -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }
                is SignInUiEvent.NavigateToMain -> onNavigateToMain()
                is SignInUiEvent.NavigateToForgotPassword -> onNavigateForgotPassword()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.imePadding()
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(R.dimen.padding_normal))
        ) {
            SignInContent(
                email = uiState.email,
                password = uiState.password,
                isLoading = uiState.isLoading,
                isButtonsEnabled = viewModel.isButtonsEnabled(),
                onEmailChange = { viewModel.changeEmail(it) },
                onPasswordChange = { viewModel.changePassword(it) },
                onSignInClick = { viewModel.login() },
                onSignUpClick = { viewModel.showTermsAndConditionsSheet(true) },
                onForgotPasswordClick = { viewModel.navigateForgotPasswordView() },
                onGoogleSignInClick = { coroutineScope.launch { viewModel.signInWithGoogle() } }
            )
        }

        if (uiState.showTermsAndConditionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showTermsAndConditionsSheet(false) },
                sheetState = sheetState,
                modifier = Modifier.fillMaxSize()
            ) {
                TermsAndConditionSheet(
                    onDeclineClick = { viewModel.showTermsAndConditionsSheet(false) },
                    onAcceptClick = {
                        viewModel.signUp()
                        viewModel.showTermsAndConditionsSheet(false)
                    },
                    loadingState = uiState.isLoading,
                )
            }
        }
    }
}

