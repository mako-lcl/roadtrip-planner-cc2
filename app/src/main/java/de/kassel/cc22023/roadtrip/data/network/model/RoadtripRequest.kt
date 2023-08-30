package de.kassel.cc22023.roadtrip.data.network.model

data class RoadtripRequest(
    val model: String = "gpt-3.5-turbo",
    val temperature: Int = 0,
    val messages: List<RoadtripRequestMessage>
)

data class RoadtripRequestMessage(
    val role: String = "system",
    val content: String
)