package com.berkeyilmaz.cardapp.presentation.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppButtonStyle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInUiEvent
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

@Composable
fun SignInView(
    onNavigate: (route: String, email: String) -> Unit,
    viewModel: SignInViewModel = hiltViewModel<SignInViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignInUiEvent.ShowError -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }

                is SignInUiEvent.Navigate -> {
                    onNavigate(event.route, event.email)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(
                horizontal = dimensionResource(R.dimen.padding_normal)
            )
    ) {
        SnackbarHost(
            hostState = snackBarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(R.string.welcome),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                stringResource(R.string.sign_in_subtext),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))
            CustomTextField(
                value = uiState.email,
                onValueChange = { viewModel.changeEmail(it) },
                label = stringResource(R.string.email),
                leadingIcon = Icons.Default.Email,
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )
            CustomTextField(
                value = uiState.password,
                onValueChange = { viewModel.changePassword(it) },
                label = stringResource(R.string.password),
                leadingIcon = Icons.Default.Lock,
                singleLine = true,
                maxLines = 1,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.signIn() },
                isPassword = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                CustomAppButton(
                    text = stringResource(R.string.forgot_password),
                    onClick = { },
                    textButtonContentPadding = PaddingValues(0.dp),
                    style = AppButtonStyle.TEXT,
                )

            }
            CustomAppButton(
                text = stringResource(R.string.sign_in),
                onClick = { viewModel.signIn() },
                loading = uiState.isLoading,
                fullWidth = true,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                stringResource(R.string.dont_you_have_acc),
                color = MaterialTheme.colorScheme.onBackground
            )
            CustomAppButton(
                text = stringResource(R.string.sign_up),
                onClick = { viewModel.navigateToSignUp() },
                textButtonContentPadding = PaddingValues(0.dp),
                style = AppButtonStyle.TEXT,
            )
        }
    }
}
