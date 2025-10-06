package com.berkeyilmaz.cardapp.presentation.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.AppButtonStyle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.presentation.auth.signup.viewmodel.SignUpUiEvent
import com.berkeyilmaz.cardapp.presentation.auth.signup.viewmodel.SignUpViewModel

@Composable
fun SignUpView(
    onNavigateToSignIn: () -> Unit, viewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpUiEvent.ShowError -> TODO()
                is SignUpUiEvent.Navigate -> {
                    onNavigateToSignIn()
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
        IconButton(
            onClick = { onNavigateToSignIn() },
            modifier = Modifier.align(Alignment.TopStart),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                stringResource(R.string.create_your_account),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))
            CustomTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.changeFullName(it) },
                label = stringResource(R.string.full_name),
                singleLine = true,
                maxLines = 1,
                leadingIcon = Icons.Default.Person,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            )
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
            CustomTextField(
                value = uiState.phoneNumber,
                onValueChange = { viewModel.changePhoneNumber(it) },
                label = stringResource(R.string.phone_number),
                singleLine = true,
                maxLines = 1,
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            )
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))

            CustomAppButton(
                text = stringResource(R.string.sign_up),
                onClick = {},
                fullWidth = true,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = stringResource(R.string.already_have_an_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            CustomAppButton(
                text = stringResource(R.string.sign_up),
                onClick = { viewModel.navigateToSignIn() },
                textButtonContentPadding = PaddingValues(0.dp),
                loading = uiState.isLoading,
                style = AppButtonStyle.TEXT,
            )
        }
    }
}