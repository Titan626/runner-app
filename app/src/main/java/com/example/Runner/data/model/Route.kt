package com.example.Runner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: String,
    val distance: Double, // in kilometers
    val duration: Int, // in minutes
    val elevation: Int, // in meters
    val difficulty: RouteDifficulty,
    val coordinates: List<LatLng>,
    val startPoint: LatLng,
    val endPoint: LatLng,
    val isLoop: Boolean = true,
    val description: String? = null,
    val label: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double
)

enum class RouteDifficulty {
    EASY, MODERATE, HARD
}

enum class RouteFilter {
    FLATTEST, SHORTEST, MOST_SCENIC, FASTEST
}

@Serializable
data class RouteRequest(
    val startLocation: LatLng,
    val targetDistance: Double, // in kilometers
    val units: DistanceUnit = DistanceUnit.KILOMETERS,
    val routeType: RouteType = RouteType.LOOP
)

enum class DistanceUnit {
    KILOMETERS, MILES
}

enum class RouteType {
    LOOP, POINT_TO_POINT
}

@Serializable
data class RouteGenerationResponse(
    val routes: List<Route>,
    val requestId: String,
    val generatedAt: Long = System.currentTimeMillis()
)