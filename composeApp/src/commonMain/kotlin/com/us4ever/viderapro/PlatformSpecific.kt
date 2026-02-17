package com.us4ever.viderapro

import androidx.compose.runtime.Composable

@Composable
expect fun PermissionWrapper(
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
)

expect fun initImageLoader()

@Composable
expect fun PlatformImageLoaderConfig(content: @Composable () -> Unit)

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
