package de.kassel.cc22023.roadtrip.data.repository

import android.location.Location
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItemDao
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.data.network.OpenAiApi
import de.kassel.cc22023.roadtrip.data.network.model.RoadtripRequest
import de.kassel.cc22023.roadtrip.data.network.model.RoadtripRequestMessage
import de.kassel.cc22023.roadtrip.data.preferences.PreferenceStore
import de.kassel.cc22023.roadtrip.data.repository.database.NotificationType
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripActivity
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripActivityDao
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripAndLocationsAndList
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripData
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripLocation
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripLocationDao
import de.kassel.cc22023.roadtrip.data.sensors.SensorRepository
import de.kassel.cc22023.roadtrip.util.convertCleanedStringToTrip
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.createRoadtripPrompt
import de.kassel.cc22023.roadtrip.util.launch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RoadtripRepository {
    val allRoadtrips: Flow<List<RoadtripAndLocationsAndList>?>
    suspend fun createRoadtrip(
        startLocation: String,
        endLocation: String,
        startDate: String,
        endDate: String,
        transportation: String,
        onSuccess: (RoadtripAndLocationsAndList) -> Unit,
        onLoading: () -> Unit,
        onError: () -> Unit
    )
    suspend fun updateItem(item: PackingItem)
    suspend fun insertIntoList(item: PackingItem)
    fun insertNewRoadtrip(trip: RoadtripAndLocationsAndList) : Long
    fun getRoadtrip() : RoadtripAndLocationsAndList
    fun deleteItem(card: PackingItem)
    fun getPackingList() : List<PackingItem>
    fun getLocation() : Location?
    fun deleteAllTrips()
    fun deleteTrip(trip: RoadtripAndLocationsAndList)
}

class DefaultRoadtripRepository @Inject constructor(
    private val roadtripDataDao: RoadtripDataDao,
    private val roadtripLocationDao: RoadtripLocationDao,
    private val roadtripActivityDao: RoadtripActivityDao,
    private val packingItemDao: PackingItemDao,
    private val openAiApi: OpenAiApi,
    private val sensorRepository: SensorRepository,
    private val preferenceStore: PreferenceStore
) : RoadtripRepository {
    override val allRoadtrips: Flow<List<RoadtripAndLocationsAndList>>
        get() = roadtripDataDao.getAllRoadtripsAsFlow()

    override fun insertNewRoadtrip(trip: RoadtripAndLocationsAndList) : Long {
        val tripId = roadtripDataDao.insertRoadtripData(RoadtripData(0, trip.trip.startDate, trip.trip.endDate, trip.trip.startLocation, trip.trip.endLocation))

        val locations = roadtripLocationDao.insertLocations(
            trip.locations.map {loc ->
                RoadtripLocation(0, tripId, loc.location.lat, loc.location.lon, loc.location.name)
            }
        )

        trip.locations.forEachIndexed {i, loc ->
            val locationId = if (i < locations.size) locations[i] else 0
            roadtripActivityDao.insertActivities(
                loc.activities.map {
                    RoadtripActivity(0, locationId, it.name)
                }
            )
        }

        packingItemDao.insertPackingItems(
            trip.packingItems.map {
                PackingItem(
                    0,
                    tripId,
                    it.name,
                    NotificationType.NONE,
                    false,
                    null,
                    0.0,
                    0.0,
                    0.0
                )
            }
        )

        return tripId
    }

    override suspend fun createRoadtrip(
        startLocation: String,
        endLocation: String,
        startDate: String,
        endDate: String,
        transportation: String,
        onSuccess: (RoadtripAndLocationsAndList) -> Unit,
        onLoading: () -> Unit,
        onError: () -> Unit
    ) {
        val contentType = "application/json"
        val prompt = createRoadtripPrompt(startLocation, endLocation, startDate, endDate, transportation)
        val request = RoadtripRequest(messages = listOf(RoadtripRequestMessage(content = prompt)))
        openAiApi.getRoadtripAsync( contentType, request)
            .launch(
                onSuccess = {resp ->
                                val content = resp.choices.firstOrNull()?.message?.content
                                if (content != null) {
                                    val trip = convertCleanedStringToTrip(content)
                                    if (trip != null) {
                                        val combinedRoadtrip = convertRoadtripFromTestTrip(trip)
                                        onSuccess(combinedRoadtrip)
                                    } else {
                                        onError()
                                    }
                                } else {
                                    onError()
                                }
                            }, onLoading = {
                                onLoading()
                            }, onError = {
                                onError()
                            })
    }

    override fun getRoadtrip(): RoadtripAndLocationsAndList =
        roadtripDataDao.getRoadtripAndLocations()

    override fun deleteItem(card: PackingItem) =
        packingItemDao.deleteItem(card)

    override fun getPackingList(): List<PackingItem> =
        packingItemDao.getPackingItems()

    override fun getLocation(): Location? =
        sensorRepository.getLocation()

    override fun deleteAllTrips() {
        roadtripDataDao.nukeDB()
        roadtripLocationDao.nukeDB()
        roadtripActivityDao.nukeDB()
        packingItemDao.nukeDB()
    }

    override fun deleteTrip(trip: RoadtripAndLocationsAndList) {
        trip.packingItems.forEach { item ->
            //delete all connected packing items
            packingItemDao.deleteItem(item)
        }
        trip.locations.forEach { location ->
            //delete all connected activities
            location.activities.forEach { activity ->
                roadtripActivityDao.deleteActivity(activity)
            }
            //delete all connected locations
            roadtripLocationDao.deleteLocation(location.location)
        }
        //delete trip
        roadtripDataDao.deleteRoadtrip(trip.trip)
    }

    override suspend fun updateItem(item: PackingItem) =
        packingItemDao.updateItem(item)

    override suspend fun insertIntoList(item: PackingItem) =
        packingItemDao.insertIntoList(item)
}
