package com.berkeyilmaz.cardapp.presentation.auth.signin.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppButtonStyle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.core.widgets.OrDivider
import com.berkeyilmaz.cardapp.presentation.auth.widgets.GoogleSignInButton

@Composable
fun SignInContent(
    email: String,
    password: String,
    isLoading: Boolean,
    isButtonsEnabled: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val passwordFocusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SignInHeader()

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)))

        SignInForm(
            email = email,
            password = password,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            passwordFocusRequester = passwordFocusRequester
        )

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.spacer_4)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CustomAppButton(
                text = stringResource(R.string.forgot_password),
                onClick = onForgotPasswordClick,
                textButtonContentPadding = PaddingValues(0.dp),
                style = AppButtonStyle.TEXT,
            )
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.spacer_4)))

        SignInButtons(
            isLoading = isLoading,
            isButtonsEnabled = isButtonsEnabled,
            onSignInClick = onSignInClick,
            onSignUpClick = onSignUpClick
        )

        OrDivider()

        GoogleSignInButton(onClick = onGoogleSignInClick)
    }
}

@Composable
private fun SignInHeader() {
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
}

@Composable
private fun SignInForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    passwordFocusRequester: FocusRequester
) {
    CustomTextField(
        value = email,
        onValueChange = onEmailChange,
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
        value = password,
        onValueChange = onPasswordChange,
        label = stringResource(R.string.password),
        leadingIcon = Icons.Default.Lock,
        singleLine = true,
        maxLines = 1,
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
        isPassword = true,
        modifier = Modifier.focusRequester(passwordFocusRequester)
    )
}

@Composable
private fun SignInButtons(
    isLoading: Boolean,
    isButtonsEnabled: Boolean,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    CustomAppButton(
        text = stringResource(R.string.sign_in),
        onClick = onSignInClick,
        loading = isLoading,
        enabled = isButtonsEnabled,
        fullWidth = true,
    )
    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.spacer_4)))
    CustomAppButton(
        text = stringResource(R.string.sign_up),
        onClick = onSignUpClick,
        loading = isLoading,
        enabled = isButtonsEnabled,
        fullWidth = true,
    )
}

