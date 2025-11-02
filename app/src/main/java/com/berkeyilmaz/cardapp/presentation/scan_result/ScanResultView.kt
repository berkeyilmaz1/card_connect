package com.berkeyilmaz.cardapp.presentation.scan_result

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton
import com.berkeyilmaz.cardapp.core.widgets.CustomTextField
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResultItem
import com.berkeyilmaz.cardapp.presentation.scan_result.viewmodel.ScanResultViewModel


@Composable
fun ScanResultView() {

    val viewModel = hiltViewModel<ScanResultViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    val resultItems = listOf(
        ScanResultItem(
            title = stringResource(R.string.full_name), content = {
                CustomTextField(
                    value = uiState.fullName.orEmpty(),
                    onValueChange = { viewModel.updateFullName(it) },
                    leadingIcon = Icons.Default.Person,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultItem(
            title = stringResource(R.string.job_title), content = {
                CustomTextField(
                    value = uiState.jobTitle.orEmpty(),
                    onValueChange = { viewModel.updateJobTitle(it) },
                    leadingIcon = Icons.Default.Lock,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultItem(
            title = stringResource(R.string.company), content = {
                CustomTextField(
                    value = uiState.company.orEmpty(),
                    onValueChange = { viewModel.updateCompany(it) },
                    leadingIcon = Icons.Default.Business,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultItem(
            title = stringResource(R.string.phone_number), content = {
                CustomTextField(
                    value = uiState.phoneNumber.orEmpty(),
                    onValueChange = { viewModel.updatePhoneNumber(it) },
                    leadingIcon = Icons.Default.Phone,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultItem(
            title = stringResource(R.string.email), content = {
                CustomTextField(
                    value = uiState.email.orEmpty(),
                    onValueChange = { viewModel.updateEmail(it) },
                    leadingIcon = Icons.Default.Email,
                    singleLine = true,
                    maxLines = 1,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultItem(
            title = stringResource(R.string.address), content = {
                CustomTextField(
                    value = uiState.address.orEmpty(),
                    onValueChange = { viewModel.updateAddress(it) },
                    leadingIcon = Icons.Default.LocationOn,
                    singleLine = false,
                    maxLines = 3,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
            }),
        ScanResultItem(
            title = stringResource(R.string.notes), content = {
                CustomTextField(
                    value = uiState.notes.orEmpty(),
                    onValueChange = { viewModel.updateNotes(it) },
                    singleLine = false,
                    minLines = 5,
                    maxLines = 10,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                )
            }),
    )
    Scaffold(
        bottomBar = {
            CustomAppButton(
                text = "Save",
                onClick = { /* Kaydetme işlemi burada gerçekleştirilecek */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_normal),
                        vertical = dimensionResource(R.dimen.padding_small)
                    )
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_normal))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close, contentDescription = stringResource(
                                R.string.close
                            ), modifier = Modifier.clickable(enabled = true, onClick = {
                                    // Kapatma işlemi burada gerçekleştirilecek
                                })
                        )
                    }
                }
                item { ScannedCard() }

                items(resultItems) { item ->
                    ResultSection(title = item.title, content = item.content)
                }
            }

        }
    }
}


@Composable
fun ResultSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title, style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_4)))
        content()
    }
}

@Composable
fun ScannedCard() {
    val cardImage = ScanResultItem(
        content = {
            // Placeholder image - gerçek scan edilen kartın görseli burada gösterilecek
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.scanned_card),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        })

    Column {
        ResultSection(title = cardImage.title, content = cardImage.content)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_8)))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_4)))

    }
}

