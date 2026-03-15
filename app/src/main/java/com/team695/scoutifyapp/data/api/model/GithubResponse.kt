package com.team695.scoutifyapp.data.api.model

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String
)

data class GithubResponse(
    @SerializedName("tag_name")
    val tagName: String,
    val assets: List<Asset>
)