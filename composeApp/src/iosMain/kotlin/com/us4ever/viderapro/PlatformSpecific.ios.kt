package com.us4ever.viderapro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory

@Composable
actual fun PermissionWrapper(
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        onPermissionGranted()
    }
    content()
}

actual fun initImageLoader() {
}

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
}

@Composable
actual fun PlatformImageLoaderConfig(content: @Composable () -> Unit) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .build()
    }
    content()
}
