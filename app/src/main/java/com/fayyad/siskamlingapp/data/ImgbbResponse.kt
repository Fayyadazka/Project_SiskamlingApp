package com.fayyad.siskamlingapp.data

import com.google.gson.annotations.SerializedName

data class ImgbbResponse(
    @SerializedName("data") val data: ImgbbData?
)

data class ImgbbData(
    @SerializedName("url") val url: String?,
    @SerializedName("display_url") val displayUrl: String? // Tambahan baru
)