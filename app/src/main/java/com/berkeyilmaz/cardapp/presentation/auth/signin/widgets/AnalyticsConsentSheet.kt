package com.berkeyilmaz.cardapp.presentation.auth.signin.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.widgets.CustomAppButton

@Composable
fun AnalyticsConsentSheet(
    onDeclineClick: () -> Unit,
    onAcceptClick: () -> Unit,
    loadingState: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(dimensionResource(R.dimen.padding_lowNormal)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_normal))
    ) {
        Text(
            text = stringResource(R.string.analytics_consent_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.analytics_consent_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.analytics_consent_data_list),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            CustomAppButton(
                text = stringResource(R.string.decline),
                onClick = onDeclineClick,
                fullWidth = false,
                modifier = Modifier.weight(1f)
            )

            CustomAppButton(
                text = stringResource(R.string.accept),
                onClick = onAcceptClick,
                loading = loadingState,
                fullWidth = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
