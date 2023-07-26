package de.kassel.cc22023.roadtrip.data.network.model

import com.squareup.moshi.Json

data class OpenAiResponse(
    var choices: List<Choice>,
)
data class Choice(
    val message: ChoiceMessage,
)

data class ChoiceMessage(
    val content: String
)