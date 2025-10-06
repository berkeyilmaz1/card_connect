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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppButtonStyle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInUiEvent
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInViewModel
import com.berkeyilmaz.cardapp.presentation.ui.theme.CardAppTheme

@Composable
fun SignInView(
    onNavigateToSignUp: () -> Unit, viewModel: SignInViewModel = hiltViewModel<SignInViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignInUiEvent.ShowError -> TODO()
                is SignInUiEvent.Navigate -> {
                    onNavigateToSignUp()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(R.dimen.padding_normal))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(stringResource(R.string.welcome), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
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
                imeAction = ImeAction.Next,
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
                onClick = {},
                fullWidth = true,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(stringResource(R.string.dont_you_have_acc), color = MaterialTheme.colorScheme.onBackground)
            CustomAppButton(
                text = stringResource(R.string.sign_up),
                onClick = { viewModel.navigateToSignUp() },
                textButtonContentPadding = PaddingValues(0.dp),
                loading = uiState.isLoading,
                style = AppButtonStyle.TEXT,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInViewPreview() {
    CardAppTheme {
        SignInView(onNavigateToSignUp = { })
    }
}