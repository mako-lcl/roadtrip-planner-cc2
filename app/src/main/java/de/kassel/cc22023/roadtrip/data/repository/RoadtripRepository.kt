package de.kassel.cc22023.roadtrip.data.repository

import android.location.Location
import de.kassel.cc22023.roadtrip.BuildConfig
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItem
import de.kassel.cc22023.roadtrip.data.repository.database.PackingItemDao
import de.kassel.cc22023.roadtrip.data.repository.database.RoadtripDataDao
import de.kassel.cc22023.roadtrip.data.network.OpenAiApi
import de.kassel.cc22023.roadtrip.data.network.UnsplashApi
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
import de.kassel.cc22023.roadtrip.util.DefaultImageURLs
import de.kassel.cc22023.roadtrip.util.convertCleanedStringToTrip
import de.kassel.cc22023.roadtrip.util.convertRoadtripFromTestTrip
import de.kassel.cc22023.roadtrip.util.createRoadtripPrompt
import de.kassel.cc22023.roadtrip.util.launch
import kotlinx.coroutines.flow.Flow
import java.lang.Exception
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
    suspend fun insertIntoList(item: PackingItem) : Long
    fun insertNewRoadtrip(trip: RoadtripAndLocationsAndList, fetchPhotos: (List<PackingItem>) -> Unit) : Long
    fun getRoadtrip() : RoadtripAndLocationsAndList
    fun deleteItem(card: PackingItem)
    fun getPackingList() : List<PackingItem>
    fun getLocation() : Location?
    fun deleteAllTrips()
    fun deleteTrip(trip: RoadtripAndLocationsAndList)

    fun updatePhotos(items: List<PackingItem>)

    fun getUnsplashImageUrl(name: String) : String?

    fun updateItems(items: List<PackingItem>)
}

class DefaultRoadtripRepository @Inject constructor(
    private val roadtripDataDao: RoadtripDataDao,
    private val roadtripLocationDao: RoadtripLocationDao,
    private val roadtripActivityDao: RoadtripActivityDao,
    private val packingItemDao: PackingItemDao,
    private val openAiApi: OpenAiApi,
    private val sensorRepository: SensorRepository,
    private val unsplashApi: UnsplashApi
) : RoadtripRepository {
    override val allRoadtrips: Flow<List<RoadtripAndLocationsAndList>>
        get() = roadtripDataDao.getAllRoadtripsAsFlow()

    override fun insertNewRoadtrip(trip: RoadtripAndLocationsAndList, fetchPhotos: (List<PackingItem>) -> Unit) : Long {
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

        val packingItems = trip.packingItems.map {
            PackingItem(
                0,
                tripId,
                it.name,
                NotificationType.NONE,
                false,
                null,
                0.0,
                0.0,
                0.0,
                DefaultImageURLs.getImageByName(it.name)
            )
        }

        packingItems.forEach {
            val id = packingItemDao.insertIntoList(it)
            it.id = id
        }

        fetchPhotos(
            packingItems.filter { it.image == null }
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
        openAiApi.getRoadtripAsync(contentType, request)
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

    override fun getUnsplashImageUrl(
        name: String,
    ) : String? {
        val clientId = BuildConfig.UNSPLASH_KEY

        try {
            val response = unsplashApi.getPhotos(clientId, name).execute()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val photo = body.results.firstOrNull()
                    if (photo != null) {
                        if (photo.urls.thumb.isEmpty())
                            return null
                        return photo.urls.thumb
                    }
                } else {
                    return null
                }
            }
        } catch (_: Exception) {
        }
        return null
    }

    override fun updatePhotos(items: List<PackingItem>) {
        val packingItems = mutableListOf<PackingItem>()

        items.forEach { item ->
            val url = getUnsplashImageUrl(item.name)
            if (url != null) {
                item.image = url
                packingItems.add(item)
            }
        }

        updateItems(packingItems)
    }

    override fun updateItems(items: List<PackingItem>) {
        packingItemDao.updateItems(items)
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
