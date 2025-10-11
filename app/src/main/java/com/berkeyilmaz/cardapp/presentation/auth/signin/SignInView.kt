package com.berkeyilmaz.cardapp.presentation.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.core.widgets.AppButtonStyle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.core.widgets.OrDivider
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInUiEvent
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInViewModel
import com.berkeyilmaz.cardapp.presentation.auth.widgets.GoogleSignInButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInView(
    onNavigate: (route: String) -> Unit,
    onNavigateForgotPassword: () -> Unit,
    onNavigateBack: () -> Unit = {},
    viewModel: SignInViewModel = hiltViewModel<SignInViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val passwordFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignInUiEvent.ShowError -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }

                is SignInUiEvent.Navigate -> {
                    if (event.route == Screen.ForgotPassword.route) {
                        onNavigateForgotPassword()
                    } else {
                        onNavigate(event.route)
                    }
                }
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
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_normal)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    stringResource(R.string.welcome),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_xSmall)))
                Text(
                    stringResource(R.string.sign_in_subtext),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)))

                CustomTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.changeEmail(it) },
                    label = stringResource(R.string.email),
                    leadingIcon = Icons.Default.Email,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    onImeAction = { passwordFocusRequester.requestFocus() },
                )

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))

                CustomTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.changePassword(it) },
                    label = stringResource(R.string.password),
                    leadingIcon = Icons.Default.Lock,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = { viewModel.signInOrRegister() },
                    isPassword = true,
                    modifier = Modifier.focusRequester(passwordFocusRequester)
                )

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CustomAppButton(
                        text = stringResource(R.string.forgot_password),
                        onClick = { viewModel.navigateForgotPasswordView() },
                        textButtonContentPadding = PaddingValues(0.dp),
                        style = AppButtonStyle.TEXT,
                    )
                }

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_normal)))

                CustomAppButton(
                    text = stringResource(R.string.sign_in),
                    onClick = { viewModel.signInOrRegister() },
                    loading = uiState.isLoading,
                    fullWidth = true,
                )

                OrDivider()

                GoogleSignInButton(onClick = { coroutineScope.launch { viewModel.signInWithGoogle() } })
            }
        }
    }
}
