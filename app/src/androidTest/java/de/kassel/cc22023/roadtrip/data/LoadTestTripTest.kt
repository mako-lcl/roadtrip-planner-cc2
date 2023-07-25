package de.kassel.cc22023.roadtrip.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.kassel.cc22023.roadtrip.data.local.database.AppDatabase
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.util.loadRoadtripFromAssets
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

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