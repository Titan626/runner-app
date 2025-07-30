package com.example.Runner.data.api

import com.example.Runner.BuildConfig
import com.example.Runner.data.model.*
import com.example.Runner.data.repository.RouteApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*
import kotlin.random.Random

interface GoogleDirectionsApi {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String? = null,
        @Query("mode") mode: String = "walking",
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}

@Serializable
data class DirectionsResponse(
    val routes: List<DirectionsRoute>,
    val status: String,
    @SerialName("error_message") val errorMessage: String? = null
)

@Serializable
data class DirectionsRoute(
    val legs: List<DirectionsLeg>,
    @SerialName("overview_polyline") val overviewPolyline: OverviewPolyline,
    val summary: String,
    val bounds: DirectionsBounds
)

@Serializable
data class DirectionsLeg(
    val distance: DirectionsDistance,
    val duration: DirectionsDuration,
    @SerialName("start_location") val startLocation: DirectionsLocation,
    @SerialName("end_location") val endLocation: DirectionsLocation,
    val steps: List<DirectionsStep>
)

@Serializable
data class DirectionsStep(
    val distance: DirectionsDistance,
    val duration: DirectionsDuration,
    @SerialName("start_location") val startLocation: DirectionsLocation,
    @SerialName("end_location") val endLocation: DirectionsLocation,
    val polyline: OverviewPolyline
)

@Serializable
data class DirectionsDistance(
    val text: String,
    val value: Int // meters
)

@Serializable
data class DirectionsDuration(
    val text: String,
    val value: Int // seconds
)

@Serializable
data class DirectionsLocation(
    val lat: Double,
    val lng: Double
)

@Serializable
data class OverviewPolyline(
    val points: String
)

@Serializable
data class DirectionsBounds(
    val northeast: DirectionsLocation,
    val southwest: DirectionsLocation
)

@Singleton
class GoogleDirectionsApiService @Inject constructor(
    private val directionsApi: GoogleDirectionsApi
) : RouteApiService {

    override suspend fun generateRoutes(request: RouteRequest): List<Route> {
        return withContext(Dispatchers.IO) {
            try {
                val routes = mutableListOf<Route>()
                
                // Generate 3 different route variations
                for (i in 0 until 3) {
                    val route = generateSingleRoute(request, i)
                    route?.let { routes.add(it) }
                }
                
                routes
            } catch (e: Exception) {
                // If API fails, return mock routes for development
                generateMockRoutes(request)
            }
        }
    }

    private suspend fun generateSingleRoute(request: RouteRequest, routeIndex: Int): Route? {
        try {
            val waypoints = generateWaypoints(request.startLocation, request.targetDistance, routeIndex)
            val waypointsString = waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
            
            val origin = "${request.startLocation.latitude},${request.startLocation.longitude}"
            val destination = origin // Loop route
            
            val response = directionsApi.getDirections(
                origin = origin,
                destination = destination,
                waypoints = "optimize:true|$waypointsString",
                mode = "walking",
                apiKey = BuildConfig.MAPS_API_KEY
            )

            if (response.isSuccessful && response.body()?.status == "OK") {
                val directionsRoute = response.body()?.routes?.firstOrNull()
                return directionsRoute?.let { convertToRoute(it, request, routeIndex) }
            }
            
            return null
        } catch (e: Exception) {
            return null
        }
    }

    private fun generateWaypoints(start: LatLng, targetDistance: Double, routeIndex: Int): List<LatLng> {
        val waypoints = mutableListOf<LatLng>()
        val numWaypoints = 3 + routeIndex // 3-5 waypoints for variety
        
        // Calculate approximate radius for waypoints (in degrees)
        val radiusKm = targetDistance / 4.0 // Rough estimate for loop
        val radiusDegrees = radiusKm / 111.0 // Rough conversion
        
        for (i in 0 until numWaypoints) {
            val angle = (2 * PI * i / numWaypoints) + (routeIndex * PI / 6) // Offset each route
            val randomOffset = 0.5 + Random.nextDouble() * 0.5 // 0.5 to 1.0 multiplier
            
            val lat = start.latitude + (radiusDegrees * randomOffset * cos(angle))
            val lng = start.longitude + (radiusDegrees * randomOffset * sin(angle))
            
            waypoints.add(LatLng(lat, lng))
        }
        
        return waypoints
    }

    private fun convertToRoute(directionsRoute: DirectionsRoute, request: RouteRequest, routeIndex: Int): Route {
        val totalDistance = directionsRoute.legs.sumOf { it.distance.value } / 1000.0 // Convert to km
        val totalDuration = directionsRoute.legs.sumOf { it.duration.value } / 60 // Convert to minutes
        
        // Decode polyline to get coordinates
        val coordinates = decodePolyline(directionsRoute.overviewPolyline.points)
        
        // Calculate rough elevation (mock data for now)
        val elevation = calculateMockElevation(coordinates, routeIndex)
        
        // Determine difficulty based on distance and elevation
        val difficulty = when {
            totalDistance < 2.0 && elevation < 50 -> RouteDifficulty.EASY
            totalDistance > 7.0 || elevation > 150 -> RouteDifficulty.HARD
            else -> RouteDifficulty.MODERATE
        }

        return Route(
            id = "route_${System.currentTimeMillis()}_$routeIndex",
            distance = totalDistance,
            duration = totalDuration,
            elevation = elevation,
            difficulty = difficulty,
            coordinates = coordinates,
            startPoint = request.startLocation,
            endPoint = request.startLocation, // Loop route
            isLoop = true,
            description = null, // Will be filled by Claude
            label = null, // Will be filled by Claude
            createdAt = System.currentTimeMillis(),
            isFavorite = false
        )
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val coordinates = mutableListOf<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var shift = 0
            var result = 0
            
            do {
                val b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlat = if ((result and 1) != 0) -(result shr 1) else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            
            do {
                val b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlng = if ((result and 1) != 0) -(result shr 1) else (result shr 1)
            lng += dlng

            coordinates.add(LatLng(lat / 1e5, lng / 1e5))
        }

        return coordinates
    }

    private fun calculateMockElevation(coordinates: List<LatLng>, routeIndex: Int): Int {
        // Mock elevation calculation - in real app, use Google Elevation API
        val baseElevation = 20 + (routeIndex * 30)
        val variability = Random.nextInt(50)
        return baseElevation + variability
    }

    // Mock routes for development when API is not available
    private fun generateMockRoutes(request: RouteRequest): List<Route> {
        return listOf(
            generateMockRoute(request, 0, "Scenic Park Loop"),
            generateMockRoute(request, 1, "Urban Explorer"),
            generateMockRoute(request, 2, "Quiet Streets")
        )
    }

    private fun generateMockRoute(request: RouteRequest, index: Int, label: String): Route {
        val start = request.startLocation
        val mockCoordinates = generateMockCoordinates(start, request.targetDistance, index)
        
        return Route(
            id = "mock_route_${System.currentTimeMillis()}_$index",
            distance = request.targetDistance,
            duration = (request.targetDistance * 8).toInt(), // ~8 min/km pace
            elevation = 50 + (index * 30),
            difficulty = when (index) {
                0 -> RouteDifficulty.EASY
                1 -> RouteDifficulty.MODERATE
                else -> RouteDifficulty.HARD
            },
            coordinates = mockCoordinates,
            startPoint = start,
            endPoint = start,
            isLoop = true,
            description = "A mock route for development purposes",
            label = label,
            createdAt = System.currentTimeMillis(),
            isFavorite = false
        )
    }

    private fun generateMockCoordinates(start: LatLng, distance: Double, routeIndex: Int): List<LatLng> {
        val coordinates = mutableListOf<LatLng>()
        val numPoints = (distance * 20).toInt() // ~20 points per km
        val radiusDegrees = distance / 111.0 / 4.0 // Rough conversion
        
        for (i in 0..numPoints) {
            val angle = (2 * PI * i / numPoints) + (routeIndex * PI / 3)
            val radius = radiusDegrees * (0.8 + 0.4 * sin(angle * 3)) // Vary radius for interesting shape
            
            val lat = start.latitude + radius * cos(angle)
            val lng = start.longitude + radius * sin(angle)
            
            coordinates.add(LatLng(lat, lng))
        }
        
        return coordinates
    }
}