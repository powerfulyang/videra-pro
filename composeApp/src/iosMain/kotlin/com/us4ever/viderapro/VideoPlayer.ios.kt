package com.us4ever.viderapro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.play
import platform.AVFoundation.pause
import platform.Foundation.NSURL
import platform.UIKit.UIView
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    video: VideoItem,
    onBack: () -> Unit
) {
    val player = remember {
        val url = NSURL.URLWithString(video.uri)
        AVPlayer(uRL = url!!)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        UIKitView(
            factory = {
                val view = UIView()
                val playerLayer = AVPlayerLayer.playerLayerWithPlayer(player)
                view.layer.addSublayer(playerLayer)
                player.play()
                view
            },
            update = { view ->
                val playerLayer = view.layer.sublayers?.first() as? AVPlayerLayer
                if (playerLayer != null) {
                    CATransaction.begin()
                    CATransaction.setValue(true, kCATransactionDisableActions)
                    playerLayer.frame = view.bounds
                    CATransaction.commit()
                }
            },
            onRelease = {
                player.pause()
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}
