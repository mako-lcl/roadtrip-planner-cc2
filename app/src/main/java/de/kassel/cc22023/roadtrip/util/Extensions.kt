package de.kassel.cc22023.roadtrip.util

import androidx.compose.ui.graphics.Color
import de.kassel.cc22023.roadtrip.R
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripActivity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

suspend fun <T> Deferred<Response<T>>.launch(
    onLoading: (suspend () -> Unit)? = null,
    onSuccess: (suspend ((body: T) -> Unit))? = null,
    onError: (suspend ((String) -> Unit))? = null,
    loadingStateFlow: MutableStateFlow<Boolean>? = null
) {
    onLoading?.invoke()
    loadingStateFlow?.value = true
    withContext(Dispatchers.IO) {
        try {
            val response = this@launch.await()
            if (response.isSuccessful) {

                val body = response.body()
                if (body == null) {
                    withContext(Dispatchers.Main) {
                        onError?.invoke("empty body from response")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onSuccess?.invoke(body)
                    }
                }
            } else {
                Timber.e("OpenAi API Error", response.message())
                // TODO: We can attach the error code to the onError callback.
                withContext(Dispatchers.Main) {
                    onError?.invoke(response.message())
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError?.invoke(e.toString())
                Timber.e(e)
            }
        } finally {
            loadingStateFlow?.value = false
        }
    }

}

fun String.cleanJsonString(): String {
    // Replace newline characters, double quotes, and consecutive whitespaces with a single space
    return this.replace(Regex("\\\\n|\\\\(?=\")"), " ")
}

fun List<RoadtripActivity>.makeActivityList() : String {
    return this.map{ it.name }.joinToString("\n") {
        "• $it"
    }
}

fun String.toWeatherIcon() : Int {
    return when(this){
        "01d"  -> R.drawable.clear_sky_01d
        "01n" -> R.drawable.clear_night_01n
        "02d" -> R.drawable.partly_cloudy
        "02n" -> R.drawable.partly_cloudy_night
        "03d" -> R.drawable.cloudy
        "03n" -> R.drawable.cloudy
        "04d" -> R.drawable.cloudy
        "04n" -> R.drawable.cloudy
        "09d" -> R.drawable.rain
        "09n" -> R.drawable.rain
        "10d" -> R.drawable.rain
        "10n" -> R.drawable.rain
        "11d" -> R.drawable.thunderstorm
        "11n" -> R.drawable.thunderstorm
        "13d" -> R.drawable.snow
        "13n" -> R.drawable.snow
        "50d" -> R.drawable.mist
        "50n" -> R.drawable.mist
        else  -> R.drawable.cloudy

    }
}

fun Long.toDateString(): String {
    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MM/dd/y")
    return date.format(formatter)
}

fun Long.toTime(): String {
    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return date.format(formatter)
}

fun String.toWeatherColor() : Color {
    return when(this) {
        "01d" -> Color.Yellow
        else -> Color.White
    }

}