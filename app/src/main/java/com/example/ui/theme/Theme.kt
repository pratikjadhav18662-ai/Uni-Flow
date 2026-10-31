package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoOnContainerDark,
    inversePrimary = IndigoPrimaryLight,

    secondary = CyanSecondaryDark,
    onSecondary = CyanOnSecondaryDark,
    secondaryContainer = CyanContainerDark,
    onSecondaryContainer = CyanOnContainerDark,

    tertiary = VioletTertiaryDark,
    onTertiary = VioletOnTertiaryDark,
    tertiaryContainer = VioletContainerDark,
    onTertiaryContainer = VioletOnContainerDark,

    background = NeutralBackgroundDark,
    onBackground = NeutralOnBackgroundDark,
    surface = NeutralSurfaceDark,
    onSurface = NeutralOnSurfaceDark,
    surfaceVariant = NeutralSurfaceVariantDark,
    onSurfaceVariant = NeutralOnSurfaceVariantDark,

    outline = NeutralOutlineDark,
    outlineVariant = NeutralOutlineVariantDark,

    error = CrimsonErrorDark,
    onError = CrimsonOnErrorDark,
    errorContainer = CrimsonErrorContainerDark,
    onErrorContainer = CrimsonOnErrorContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = IndigoOnPrimaryLight,
    primaryContainer = IndigoContainerLight,
    onPrimaryContainer = IndigoOnContainerLight,
    inversePrimary = IndigoPrimaryDark,

    secondary = CyanSecondaryLight,
    onSecondary = CyanOnSecondaryLight,
    secondaryContainer = CyanContainerLight,
    onSecondaryContainer = CyanOnContainerLight,

    tertiary = VioletTertiaryLight,
    onTertiary = VioletOnTertiaryLight,
    tertiaryContainer = VioletContainerLight,
    onTertiaryContainer = VioletOnContainerLight,

    background = NeutralBackgroundLight,
    onBackground = NeutralOnBackgroundLight,
    surface = NeutralSurfaceLight,
    onSurface = NeutralOnSurfaceLight,
    surfaceVariant = NeutralSurfaceVariantLight,
    onSurfaceVariant = NeutralOnSurfaceVariantLight,

    outline = NeutralOutlineLight,
    outlineVariant = NeutralOutlineVariantLight,

    error = CrimsonErrorLight,
    onError = CrimsonOnErrorLight,
    errorContainer = CrimsonErrorContainerLight,
    onErrorContainer = CrimsonOnErrorContainerLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Preserve custom curated executive academic branding by default, with dynamic color support on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
