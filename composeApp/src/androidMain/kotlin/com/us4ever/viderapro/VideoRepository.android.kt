package com.us4ever.viderapro

import android.content.Context
import android.content.ContentUris
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual fun getSetting(key: String, default: String): String {
    val context = AndroidContext.get()
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString(key, default) ?: default
}

actual fun saveSetting(key: String, value: String) {
    val context = AndroidContext.get()
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString(key, value).apply()
}

actual class VideoRepository actual constructor() {
    actual suspend fun fetchVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoItem>()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val context = AndroidContext.get()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED
        )

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getLong(sizeColumn)
                val path = cursor.getString(dataColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                videoList.add(VideoItem(id, name, duration, size, contentUri.toString(), path, dateAdded))
            }
        }
        videoList
    }
}
