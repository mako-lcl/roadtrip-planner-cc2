package de.kassel.cc22023.roadtrip.data.network.model

import com.squareup.moshi.Json

data class OpenAiResponse(
    @Json(name = "model")
    var model: modelResponse,
    @Json(name = "content")
    var content: content,
)
data class content(
    var content: String?,
)
data class modelResponse(
    var model: String?,
)