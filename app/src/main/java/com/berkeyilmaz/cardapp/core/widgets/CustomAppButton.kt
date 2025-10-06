package com.berkeyilmaz.cardapp.core.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppButtonStyle {
    FILLED,      // Dolgulu buton
    OUTLINED,    // Çerçeveli buton
    TEXT,        // Sadece metin buton
    ELEVATED     // Yükseltilmiş buton
}

enum class AppButtonSize {
    SMALL, MEDIUM, LARGE
}

@Composable
fun CustomAppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    style: AppButtonStyle = AppButtonStyle.FILLED,
    size: AppButtonSize = AppButtonSize.MEDIUM,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant, //when enabled==false
    disabledContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shape: Shape = RoundedCornerShape(5.dp),
    elevation: Dp = 0.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = 1.dp,
    textButtonContentPadding: PaddingValues? = null,
    fullWidth: Boolean = false,
) {
    val (horizontalPadding, verticalPadding, iconSize, fontSize) = when (size) {
        AppButtonSize.SMALL -> ButtonSizeConfig(
            16.dp, 8.dp, 16.dp, MaterialTheme.typography.labelMedium
        )

        AppButtonSize.MEDIUM -> ButtonSizeConfig(
            24.dp, 12.dp, 20.dp, MaterialTheme.typography.labelLarge
        )

        AppButtonSize.LARGE -> ButtonSizeConfig(
            32.dp, 16.dp, 24.dp, MaterialTheme.typography.titleMedium
        )
    }

    val buttonModifier = if (fullWidth) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }

    when (style) {
        AppButtonStyle.FILLED -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled && !loading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    disabledContainerColor = disabledContainerColor,
                    disabledContentColor = disabledContentColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = elevation
                ),
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding, vertical = verticalPadding
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    iconSize = iconSize,
                    textStyle = fontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AppButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled && !loading,
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = containerColor,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = disabledContentColor
                ),
                border = BorderStroke(
                    borderWidth, if (enabled) borderColor else disabledContentColor
                ),
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding, vertical = verticalPadding
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    iconSize = iconSize,
                    textStyle = fontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AppButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled && !loading,
                shape = shape,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = containerColor,
                    disabledContentColor = disabledContentColor
                ),
                contentPadding = textButtonContentPadding ?: PaddingValues(
                    horizontal = horizontalPadding, vertical = verticalPadding
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    iconSize = iconSize,
                    textStyle = fontSize,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        AppButtonStyle.ELEVATED -> {
            ElevatedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled && !loading,
                shape = shape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    disabledContainerColor = disabledContainerColor,
                    disabledContentColor = disabledContentColor
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 6.dp, pressedElevation = 8.dp, disabledElevation = 0.dp
                ),
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding, vertical = verticalPadding
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    iconSize = iconSize,
                    textStyle = fontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    iconSize: Dp,
    textStyle: androidx.compose.ui.text.TextStyle,
    fontWeight: FontWeight
) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(iconSize),
            strokeWidth = 2.dp,
            color = LocalContentColor.current
        )
        Spacer(modifier = Modifier.width(8.dp))
    }

    leadingIcon?.let {
        Icon(
            imageVector = it, contentDescription = null, modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.width(8.dp))
    }

    Text(
        text = text, style = textStyle, fontWeight = fontWeight
    )

    trailingIcon?.let {
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = it, contentDescription = null, modifier = Modifier.size(iconSize)
        )
    }
}

private data class ButtonSizeConfig(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val iconSize: Dp,
    val textStyle: androidx.compose.ui.text.TextStyle
)

// Kullanım örnekleri
/*
// Basit kullanım
CustomAppButton(
    text = "Kaydet",
    onClick = { }
)

// Loading durumu
CustomAppButton(
    text = "Yükleniyor",
    onClick = { },
    loading = true
)

// İkonlu buton
CustomAppButton(
    text = "Profil",
    onClick = { },
    leadingIcon = Icons.Default.Person
)

// Outlined buton
CustomAppButton(
    text = "İptal",
    onClick = { },
    style = AppButtonStyle.OUTLINED
)

// Tam genişlik buton
CustomAppButton(
    text = "Giriş Yap",
    onClick = { },
    fullWidth = true,
    size = AppButtonSize.LARGE
)

// Özel renkli buton
CustomAppButton(
    text = "Sil",
    onClick = { },
    containerColor = MaterialTheme.colorScheme.error,
    contentColor = MaterialTheme.colorScheme.onError,
    leadingIcon = Icons.Default.Delete
)

// Text buton
CustomAppButton(
    text = "Daha Fazla",
    onClick = { },
    style = AppButtonStyle.TEXT,
    trailingIcon = Icons.Default.ArrowForward
)
*/

