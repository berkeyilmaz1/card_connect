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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import com.berkeyilmaz.cardapp.core.navigation.Screen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.photo.model.Photo
import com.berkeyilmaz.cardapp.presentation.main.home.models.HomeNotification
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.DuplicateBottomSheetState
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.HomeUiState
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.HomeViewModel
import com.berkeyilmaz.cardapp.presentation.main.home.widgets.DuplicateContactsBottomSheet
import com.berkeyilmaz.cardapp.presentation.profile.viewmodel.GreetingPeriod
import com.berkeyilmaz.cardapp.presentation.ui.theme.AppTheme

data class QuickActionOption(
    val title: String, val icon: ImageVector, val route: String
)

@Composable
fun quickActionOptions(): List<QuickActionOption> = listOf(
    QuickActionOption(
        title = stringResource(R.string.add_new),
        icon = Icons.Outlined.Add,
        route = Screen.Main.Scan.route
    ),
    QuickActionOption(
        title = stringResource(R.string.history),
        icon = Icons.Outlined.History,
        route = ""
    ),
    QuickActionOption(
        title = stringResource(R.string.favorites),
        icon = Icons.Outlined.StarOutline,
        route = "{}"
    ),
    QuickActionOption(
        title = stringResource(R.string.settings),
        icon = Icons.Outlined.Settings,
        route = Screen.Main.Settings.route
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    uiState: HomeUiState,
    onNotificationAction: (HomeNotification) -> Unit,
    onQuickOptionClick: (String) -> Unit = {},
    onContactClick: (String) -> Unit = {}
) {
    val notification = uiState.notificationList.firstOrNull()
    val viewModel = hiltViewModel<HomeViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar mesajını göster
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            QuickActions(quickActionOptions(), onClick = { onQuickOptionClick(it) })
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_normal)))

            if (uiState.recentlyScannedCards.isEmpty()) {
                ScanPromptSection()
            } else {
                RecentlyScannedCards(uiState.recentlyScannedCards, onContactClick = onContactClick)
            }

        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(dimensionResource(R.dimen.padding_normal))
        )
    }

    // Duplicate contacts bottom sheet
    val duplicateState = uiState.duplicateBottomSheetState
    if (duplicateState !is DuplicateBottomSheetState.Hidden) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissDuplicateBottomSheet() }
        ) {
            DuplicateContactsBottomSheet(
                state = duplicateState,
                onMerge = { group, primarySelection -> viewModel.onMergeApproved(group, primarySelection) },
                onSkip = { viewModel.onDuplicateGroupSkipped() },
                onDismiss = { viewModel.dismissDuplicateBottomSheet() }
            )
        }
    }
}

@Composable
fun RecentlyScannedCards(
    recentlyScannedCards: List<Any>,
    onContactClick: (String) -> Unit = {}
) {
    if (recentlyScannedCards.isEmpty()) return

    @Suppress("UNCHECKED_CAST")
    val cards = recentlyScannedCards as List<Pair<Contact, Photo>>

    HomeSection(title = stringResource(R.string.recently_scanned)) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_lowNormal)),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = dimensionResource(R.dimen.padding_xSmall)
            )
        ) {
            items(cards) { (contact, photo) ->
                RecentContactCard(
                    contact = contact,
                    modifier = Modifier.width(dimensionResource(R.dimen.card_width_min)),
                    photo = photo,
                    onContactClick = onContactClick
                )
            }
        }
    }
}

@Composable
fun RecentContactCard(
    contact: Contact,
    modifier: Modifier = Modifier,
    photo: Photo? = null,
    onContactClick: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(dimensionResource(R.dimen.spacer_96)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.elevation_xSmall)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { onContactClick(contact.contactId) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_lowNormal)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_lowNormal))
        ) {
            // Contact initial in circular icon
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.spacer_48))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.fullName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Contact info section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                // Contact name
                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                // Contact title/organization
                if (contact.title.isNotEmpty() || contact.organization.isNotEmpty()) {
                    Text(
                        text = contact.title.ifEmpty { contact.organization },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
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
fun QuickActions(options: List<QuickActionOption>, onClick: (String) -> Unit = {}) {
    HomeSection(title = stringResource(R.string.quick_actions)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_lowNormal))
        ) {
            options.forEach {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xSmall)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_lowNormal)))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .clickable(
                            enabled = true, onClick = { onClick(it.route) })
                ) {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.spacer_32))
                    )
                    Text(
                        text = it.title,
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
    val lottieRes = when (uiState.greetingPeriod) {
        GreetingPeriod.MORNING, GreetingPeriod.AFTERNOON -> R.raw.lottie_sun
        GreetingPeriod.EVENING, GreetingPeriod.NIGHT -> R.raw.lottie_moon
    }
    val greetingText = when (uiState.greetingPeriod) {
        GreetingPeriod.MORNING -> "Günaydın"
        GreetingPeriod.AFTERNOON -> "İyi günler"
        GreetingPeriod.EVENING -> "İyi akşamlar"
        GreetingPeriod.NIGHT -> "İyi geceler"
    }

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = greetingText + if (!uiState.userName.isNullOrEmpty()) ", ${uiState.userName}" else "",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        LottieAnimation(
            composition = lottieComposition,
            progress = { lottieProgress },
            modifier = Modifier.size(72.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeViewPreview() {
    AppTheme {
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