package com.us4ever.viderapro

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(
    modifier: Modifier,
    video: VideoItem,
    onBack: () -> Unit
)
