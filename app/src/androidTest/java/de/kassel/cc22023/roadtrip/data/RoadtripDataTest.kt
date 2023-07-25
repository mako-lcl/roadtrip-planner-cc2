package de.kassel.cc22023.roadtrip.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.kassel.cc22023.roadtrip.data.local.database.AppDatabase
import de.kassel.cc22023.roadtrip.data.local.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.loadRoadtripFromAssets
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoadtripDataTest {
    private lateinit var roadtripDataDao: RoadtripDataDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        roadtripDataDao = db.roadtripDataDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val trip = loadRoadtripFromAssets(context)

        val roadtrip = convertRoadtripFromTestTrip(trip)

        val tripFromDB = roadtripDataDao.getRoadtripData()
        assertThat(tripFromDB, equalTo(roadtrip))
    }
}
