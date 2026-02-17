package com.us4ever.viderapro

import platform.Foundation.NSUserDefaults
import platform.Photos.PHAsset
import platform.Photos.PHFetchOptions
import platform.Photos.PHAssetMediaTypeVideo

actual fun getSetting(key: String, default: String): String {
    return NSUserDefaults.standardUserDefaults.stringForKey(key) ?: default
}

actual fun saveSetting(key: String, value: String) {
    NSUserDefaults.standardUserDefaults.setObject(value, key)
}

actual class VideoRepository actual constructor() {
    actual suspend fun fetchVideos(): List<VideoItem> {
        // iOS requires PHPhotoLibrary permission and PHAsset fetching
        // For now, returning an empty list. In a real app, use PHAsset.fetchAssetsWithMediaType
        return emptyList()
    }
}
