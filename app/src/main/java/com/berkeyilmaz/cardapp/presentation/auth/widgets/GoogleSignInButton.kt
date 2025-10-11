package com.berkeyilmaz.cardapp.presentation.auth.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.berkeyilmaz.cardapp.R

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(5.dp),
        color = Color.White,
        shadowElevation = dimensionResource(R.dimen.elevation_small),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = dimensionResource(R.dimen.padding_normal)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            // Google Logo
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = stringResource(R.string.sign_in_with_google),
                tint = Color.Unspecified,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))

            // Text
            Text(
                text = stringResource(R.string.sign_in_with_google),
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
