package de.kassel.cc22023.roadtrip.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import de.kassel.cc22023.roadtrip.R

@Composable
fun NoTripScreen() {
    val compositionSad by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sad))
    val progressSad by animateLottieCompositionAsState(composition = compositionSad, iterations = LottieConstants.IterateForever)
    val compositionNoData by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data))
    val progressNoData by animateLottieCompositionAsState(composition = compositionNoData, iterations = LottieConstants.IterateForever)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp).padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LottieAnimation(
                composition = compositionNoData,
                progress = { progressNoData },
                modifier = Modifier.padding(horizontal = 32.dp).scale(1.35f,1.35f)
            )

            Text("Looks like you haven't created a trip yet...")

            LottieAnimation(
                composition = compositionSad,
                progress = { progressSad },
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

    }
}