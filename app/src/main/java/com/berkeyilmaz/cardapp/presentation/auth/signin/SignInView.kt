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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.berkeyilmaz.cardapp.core.widgets.AppButtonStyle
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.core.widgets.OrDivider
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInUiEvent
import com.berkeyilmaz.cardapp.presentation.auth.signin.viewmodel.SignInViewModel
import com.berkeyilmaz.cardapp.presentation.auth.widgets.GoogleSignInButton
import kotlinx.coroutines.delay
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
    val passwordFocusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignInUiEvent.ShowError -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }

                is SignInUiEvent.NavigateToMain -> {
                    onNavigateToMain()

                }

                is SignInUiEvent.NavigateToForgotPassword -> {
                    onNavigateForgotPassword()

                }
            }
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState, modifier = Modifier.imePadding()
            )
        }, modifier = Modifier
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
                    isPassword = true,
                    modifier = Modifier.focusRequester(passwordFocusRequester)
                )

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.spacer_4)))

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    CustomAppButton(
                        text = stringResource(R.string.forgot_password),
                        onClick = { viewModel.navigateForgotPasswordView() },
                        textButtonContentPadding = PaddingValues(0.dp),
                        style = AppButtonStyle.TEXT,
                    )
                }

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.spacer_4)))

                CustomAppButton(
                    text = stringResource(R.string.sign_in),
                    onClick = { viewModel.login() },
                    loading = uiState.isLoading,
                    enabled = viewModel.isButtonsEnabled(),
                    fullWidth = true,
                )
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.spacer_4)))
                //TODO: ADD VİEWMODEL.SİGNUP TO DİALOG
                CustomAppButton(
                    text = stringResource(R.string.sign_up),
                    onClick = { viewModel.showTermsAndConditionsSheet(true) },
                    loading = uiState.isLoading,
                    enabled = viewModel.isButtonsEnabled(),
                    fullWidth = true,
                )

                OrDivider()

                GoogleSignInButton(onClick = { coroutineScope.launch { viewModel.signInWithGoogle() } })
            }
        }

        if (uiState.showTermsAndConditionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.showTermsAndConditionsSheet(false) },
                sheetState = sheetState,
                modifier = Modifier.fillMaxSize()
            ) {
                TermsAndConditionSheet(
                    onDeclineClick = {
                        viewModel.showTermsAndConditionsSheet(false)
                    },
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


@Composable
fun TermsAndConditionSheet(
    onDeclineClick: () -> Unit,
    onAcceptClick: () -> Unit,
    loadingState: Boolean = false,
) {
    val listState = rememberLazyListState()
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    var timerCompleted by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(10) }

    // Check if user has scrolled to bottom
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1 &&
                    lastVisibleItem.offset + lastVisibleItem.size <= listState.layoutInfo.viewportEndOffset
        }
    }

    // Update hasScrolledToBottom when user reaches bottom
    LaunchedEffect(isAtBottom) {
        if (isAtBottom && !hasScrolledToBottom) {
            hasScrolledToBottom = true
        }
    }

    // Start 10-second countdown timer
    LaunchedEffect(Unit) {
        repeat(10) {
            delay(1000L) // 1 second
            remainingSeconds -= 1
        }
        timerCompleted = true
    }

    // Accept button is enabled only when both conditions are met
    val canAccept = hasScrolledToBottom && timerCompleted

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        // Use a Box so the LazyColumn can scroll and buttons stay pinned to bottom
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_lowNormal)),
        ) {
            item {
                Text(
                    text = stringResource(R.string.eula_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        // Sticky buttons row - always visible at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_lowNormal)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            CustomAppButton(
                text = stringResource(R.string.decline),
                onClick = onDeclineClick,
                fullWidth = false,
                modifier = Modifier.weight(1f)
            )

            CustomAppButton(
                text = if (!timerCompleted) {
                    "${stringResource(R.string.accept)} (${remainingSeconds}s)"
                } else {
                    stringResource(R.string.accept)
                },
                onClick = onAcceptClick,
                loading = loadingState,
                enabled = canAccept,
                fullWidth = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
