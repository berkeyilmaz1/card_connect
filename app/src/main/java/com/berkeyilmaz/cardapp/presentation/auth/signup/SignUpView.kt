package com.berkeyilmaz.cardapp.presentation.auth.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.presentation.auth.signup.viewmodel.SignUpUiEvent
import com.berkeyilmaz.cardapp.presentation.auth.signup.viewmodel.SignUpViewModel

@Composable
fun SignUpView(
    onNavigateToSignIn: () -> Unit, viewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 4

    // Form validation states
    val isStepValid = remember(currentStep, uiState) {
        when (currentStep) {
            0 -> uiState.fullName.length >= 3
            1 -> uiState.email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email)
                .matches()

            2 -> uiState.password.length >= 8
            3 -> uiState.phoneNumber.length >= 8 && uiState.phoneNumber.length <= 15
            else -> false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpUiEvent.ShowError -> {
                    // Handle error - implement SnackBar or Toast
                }

                is SignUpUiEvent.Navigate -> {
                    onNavigateToSignIn()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(
                horizontal = dimensionResource(R.dimen.padding_normal)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top Bar with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (currentStep > 0) {
                            currentStep--
                        } else {
                            onNavigateToSignIn()
                        }
                    }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_24)))

            // Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacer_8))
            ) {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(dimensionResource(R.dimen.spacer_4))
                            .clip(
                                RoundedCornerShape(dimensionResource(R.dimen.spacer_2))
                            )
                            .background(
                                if (index <= currentStep) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_48)))

            // Content Area with Animation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentStep, transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith slideOutHorizontally { width -> width } + fadeOut()
                        }.using(
                            SizeTransform(clip = false)
                        )
                    }, label = "step_animation"
                ) { step ->
                    when (step) {
                        0 -> StepContent(
                            title = stringResource(R.string.create_your_account),
                            subtitle = stringResource(R.string.sign_up_subtext1),
                            content = {
                                CustomTextField(
                                    value = uiState.fullName,
                                    onValueChange = { viewModel.changeFullName(it) },
                                    label = stringResource(R.string.full_name),
                                    leadingIcon = Icons.Default.Person,
                                    singleLine = true,
                                    maxLines = 1,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                )

                                if (uiState.fullName.isNotBlank() && uiState.fullName.length < 3) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.sign_up_error1),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            })

                        1 -> StepContent(
                            title = stringResource(R.string.what_s_your_email),
                            subtitle = stringResource(R.string.sign_up_subtext2),
                            content = {
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

                                if (uiState.email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(
                                        uiState.email
                                    ).matches()
                                ) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.sign_up_error2),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            })

                        2 -> StepContent(
                            title = stringResource(R.string.create_a_password),
                            subtitle = stringResource(R.string.sign_up_subtext3),
                            content = {
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

                                if (uiState.password.isNotBlank() && uiState.password.length < 6) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.sign_up_error3),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            })

                        3 -> StepContent(
                            title = stringResource(R.string.what_s_your_phone_number),
                            subtitle = stringResource(R.string.sign_up_subtext4),
                            content = {
                                CustomTextField(
                                    value = uiState.phoneNumber,
                                    onValueChange = { viewModel.changePhoneNumber(it) },
                                    label = stringResource(R.string.phone_number),
                                    leadingIcon = Icons.Default.Phone,
                                    singleLine = true,
                                    maxLines = 1,
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done,
                                )

                                if (uiState.phoneNumber.isNotBlank() && uiState.phoneNumber.length < 10) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.sign_up_error4),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            })
                    }
                }
            }

            // Bottom Navigation Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Next/Finish Button
                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++
                        } else {
                            viewModel.signUp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isStepValid && !uiState.isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (currentStep < totalSteps - 1) stringResource(R.string.next) else stringResource(
                                R.string.create_account
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (currentStep < totalSteps - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Sign In Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.already_have_an_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    TextButton(
                        onClick = { viewModel.navigateToSignIn() },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.sign_in),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepContent(
    title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        content()
    }
}