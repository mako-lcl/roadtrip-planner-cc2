package de.kassel.cc22023.roadtrip.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.kassel.cc22023.roadtrip.util.loadRoadtripFromAssets
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadTestTripTest {
    @Test
    @Throws(Exception::class)
    fun loadTrip() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val trip = loadRoadtripFromAssets(context)

        assert(trip.packingList.contains("a"))
        print(trip)
    }
}