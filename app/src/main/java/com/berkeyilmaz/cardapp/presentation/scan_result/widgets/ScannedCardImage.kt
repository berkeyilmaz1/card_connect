package com.berkeyilmaz.cardapp.presentation.scan_result.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.berkeyilmaz.cardapp.R
import java.io.File

@Composable
fun ScannedCardImage(imagePath: String) {
    Column {
        if (imagePath.isNotEmpty()) {
            SubcomposeAsyncImage(
                model = File(imagePath),
                contentDescription = stringResource(R.string.scanned_card),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    PlaceholderImage()
                })
        } else {
            PlaceholderImage()
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_8)))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_4)))
    }
}

@Composable
private fun PlaceholderImage() {
    Image(
        painter = painterResource(id = R.mipmap.ic_launcher),
        contentDescription = stringResource(R.string.scanned_card),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Fit
    )
}