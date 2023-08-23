package de.kassel.cc22023.roadtrip.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class OpenAiResponse(
    var choices: List<Choice>,
)
data class Choice(
    val message: ChoiceMessage,
)

data class ChoiceMessage(
    val content: String
)

@JsonClass(generateAdapter = true)
data class OpenAiTrip(
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String,
    @Json(name = "start_location")
    val startLocation: String,
    @Json(name = "end_location")
    val endLocation: String,
    @Json(name = "packing_list")
    val packingList: List<String>,
    @Json(name = "locations")
    val locs: List<OpenAiLoc>
)

@JsonClass(generateAdapter = true)
data class OpenAiLoc(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @Json(name = "must_have")
    val mustHave: Boolean,
    val date: String,
    val activities: List<String>
)