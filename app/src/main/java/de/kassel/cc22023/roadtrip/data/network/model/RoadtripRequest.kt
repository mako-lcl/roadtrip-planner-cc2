package de.kassel.cc22023.roadtrip.data.network.model

import com.squareup.moshi.Json

data class RoadtripRequest(
    val model: String = "gpt-3.5-turbo",
    val temperature: Int = 0,
    val messages: List<RoadtripRequestMessage>
)

data class RoadtripRequestMessage(
    val role: String = "system",
    val content: String
)