package com.us4ever.viderapro

import kotlinx.serialization.Serializable

data class VideoItem(
    val id: Long,
    val name: String,
    val duration: Long,
    val size: Long,
    val uri: String,
    val path: String? = null,
    val dateAdded: Long = 0,
    val isRemote: Boolean = false
)

@Serializable
data class OpenListResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)

@Serializable
data class OpenListData(
    val content: List<OpenListItem>? = null,
    val total: Int? = null,
    val name: String? = null,
    val size: Long? = null,
    val is_dir: Boolean? = null,
    val raw_url: String? = null
)

@Serializable
data class OpenListItem(
    val name: String,
    val size: Long,
    val is_dir: Boolean,
    val modified: String,
    val created: String,
    val sign: String,
    val thumb: String,
    val type: Int
)

@Serializable
data class OpenListListRequest(
    val path: String,
    val password: String = "",
    val page: Int = 1,
    val per_page: Int = 0,
    val refresh: Boolean = false
)

@Serializable
data class OpenListGetRequest(
    val path: String,
    val password: String = ""
)
