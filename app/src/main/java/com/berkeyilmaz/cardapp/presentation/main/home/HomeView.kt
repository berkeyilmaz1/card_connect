package com.berkeyilmaz.cardapp.presentation.main.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.presentation.main.home.models.HomeNotification
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.HomeUiState
import com.berkeyilmaz.cardapp.presentation.ui.theme.CardAppTheme

@Composable
fun HomeView(uiState: HomeUiState, onNotificationAction: (HomeNotification) -> Unit) {
    val notification = uiState.notificationList.firstOrNull()
    val quickActionOptions = mapOf(
        stringResource(R.string.add_new) to Icons.Outlined.Add,
        stringResource(R.string.history) to Icons.Outlined.History,
        stringResource(R.string.favorites) to Icons.Outlined.StarOutline,
        stringResource(R.string.settings) to Icons.Outlined.Settings,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(dimensionResource(R.dimen.padding_normal)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,

        ) {
        AnimatedVisibility(
            visible = notification != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            notification?.let {
                NotificationSection(
                    title = it.title,
                    subtitle = it.subtitle,
                    onCloseClick = { onNotificationAction(it) },
                    onButtonClick = { onNotificationAction(it) },
                    buttonText = it.buttonText
                )
            }
        }
        ProfileSection(uiState = uiState)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_lowNormal)))
        QuickActions(quickActionOptions)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_normal)))

        if (uiState.recentlyScannedCards.isEmpty()) {
            ScanPromptSection()
        } else {
            //recently scanned cards list
        }

    }
}

@Composable
fun ScanPromptSection() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.spacer_96))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Outlined.QrCodeScanner,
                contentDescription = stringResource(R.string.scanner_icon),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacer_48))
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
        Text(
            text = stringResource(R.string.scan_a_business_card),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xSmall)))
        Text(
            text = stringResource(R.string.scan_a_business_card_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_lowNormal)),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun QuickActions(options: Map<String, ImageVector>) {
    HomeSection(title = stringResource(R.string.quick_actions)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_lowNormal))
        ) {
            options.forEach { (title, icon) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_lowNormal)))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(dimensionResource(R.dimen.padding_small))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.spacer_32))
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        }
    }
}

@Composable
fun HomeSection(
    title: String, hasViewAll: Boolean = false, content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_small)
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        content()
    }
}

@Composable
fun NotificationSection(
    title: String,
    subtitle: String,
    buttonText: String,
    onCloseClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimensionResource(R.dimen.padding_lowNormal))
            )
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_lowNormal))
        ) {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = stringResource(R.string.email_icon),
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_2)))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
//                CustomAppButton(
//                    text = buttonText,
//                    onClick = onButtonClick,
//                    textButtonContentPadding = PaddingValues(0.dp),
//                    style = AppButtonStyle.TEXT,
//                )
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onCloseClick() })
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
    }
}


@Composable
fun ProfileSection(uiState: HomeUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (uiState.userName.isNullOrEmpty()) {
                    stringResource(R.string.scan_connect)
                } else {
                    stringResource(R.string.welcome_back)
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (uiState.userName.isNullOrEmpty()) {
                    stringResource(R.string.ready_to_scan_a_new_card)
                } else {
                    uiState.userName
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

        }
        Spacer(modifier = Modifier.weight(1f))

        if (!uiState.userProfileImageUrl.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacer_48))
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            ) {
                AsyncImage(
                    model = uiState.userProfileImageUrl,
                    contentDescription = stringResource(R.string.profile_image),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.spacer_64))
                        .clip(CircleShape)
                        .border(
                            width = dimensionResource(R.dimen.spacer_2),
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeViewPreview() {
    CardAppTheme {
        HomeView(
            uiState = HomeUiState(
                userName = "Berke Yılmaz", userProfileImageUrl = "https://example.com/profile.jpg"
            ), onNotificationAction = {})
    }
}


//val navBackStackEntry = remember { bottomNavController.currentBackStackEntry }
//val savedStateHandle = navBackStackEntry?.savedStateHandle
//
//LaunchedEffect(Unit) {
//    savedStateHandle?.getLiveData<String>("capturedPhotoUri")?.observe(lifecycleOwner) { uri ->
//        uri?.let {
//            Log.d("HomeView", "Captured photo URI: $it")
//            // burada URI’yi görüntüleyebilir veya işleyebilirsin
//        }
//    }
//}