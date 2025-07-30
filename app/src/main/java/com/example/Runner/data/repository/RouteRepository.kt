package com.example.Runner.data.repository

import com.example.Runner.data.model.*
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    suspend fun generateRoutes(request: RouteRequest): Result<List<Route>>
    suspend fun saveRoute(route: Route): Result<Unit>
    suspend fun deleteRoute(routeId: String): Result<Unit>
    suspend fun getFavoriteRoutes(): Flow<List<Route>>
    suspend fun toggleRouteFavorite(routeId: String): Result<Unit>
    suspend fun getCachedRoutes(location: LatLng, distance: Double): List<Route>?
    suspend fun cacheRoutes(routes: List<Route>, location: LatLng, distance: Double)
}

class RouteRepositoryImpl(
    private val routeApi: RouteApiService,
    private val routeDao: RouteDao,
    private val claudeService: ClaudeService
) : RouteRepository {

    override suspend fun generateRoutes(request: RouteRequest): Result<List<Route>> {
        return try {
            // Check cache first
            val cachedRoutes = getCachedRoutes(request.startLocation, request.targetDistance)
            if (cachedRoutes != null && cachedRoutes.isNotEmpty()) {
                return Result.success(cachedRoutes)
            }

            // Generate routes from API
            val routes = routeApi.generateRoutes(request)
            
            // Enhance with Claude descriptions
            val enhancedRoutes = routes.map { route ->
                val claudeResponse = claudeService.generateRouteDescription(route)
                route.copy(
                    description = claudeResponse.description,
                    label = claudeResponse.label
                )
            }

            // Cache the results
            cacheRoutes(enhancedRoutes, request.startLocation, request.targetDistance)

            Result.success(enhancedRoutes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveRoute(route: Route): Result<Unit> {
        return try {
            routeDao.insertRoute(route.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRoute(routeId: String): Result<Unit> {
        return try {
            routeDao.deleteRoute(routeId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFavoriteRoutes(): Flow<List<Route>> {
        return routeDao.getFavoriteRoutes()
    }

    override suspend fun toggleRouteFavorite(routeId: String): Result<Unit> {
        return try {
            routeDao.toggleFavorite(routeId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCachedRoutes(location: LatLng, distance: Double): List<Route>? {
        val cacheKey = "${location.latitude},${location.longitude},${distance}"
        return routeDao.getCachedRoutes(cacheKey, System.currentTimeMillis() - 24 * 60 * 60 * 1000) // 24 hours
    }

    override suspend fun cacheRoutes(routes: List<Route>, location: LatLng, distance: Double) {
        val cacheKey = "${location.latitude},${location.longitude},${distance}"
        routeDao.cacheRoutes(routes.map { it.toEntity() }, cacheKey)
    }
}

// Extension function to convert Route to database entity
private fun Route.toEntity(): RouteEntity {
    // This would convert to your database entity
    // Implementation depends on your database schema
    return RouteEntity(
        id = id,
        distance = distance,
        duration = duration,
        elevation = elevation,
        difficulty = difficulty.name,
        coordinates = coordinates,
        startPoint = startPoint,
        endPoint = endPoint,
        isLoop = isLoop,
        description = description,
        label = label,
        createdAt = createdAt,
        isFavorite = isFavorite
    )
}

// Placeholder interfaces - to be implemented
interface RouteApiService {
    suspend fun generateRoutes(request: RouteRequest): List<Route>
}

interface RouteDao {
    suspend fun insertRoute(route: RouteEntity)
    suspend fun deleteRoute(routeId: String)
    suspend fun getFavoriteRoutes(): Flow<List<Route>>
    suspend fun toggleFavorite(routeId: String)
    suspend fun getCachedRoutes(cacheKey: String, maxAge: Long): List<Route>?
    suspend fun cacheRoutes(routes: List<RouteEntity>, cacheKey: String)
}

interface ClaudeService {
    suspend fun generateRouteDescription(route: Route): ClaudeResponse
}

data class ClaudeResponse(
    val description: String,
    val label: String
)

data class RouteEntity(
    val id: String,
    val distance: Double,
    val duration: Int,
    val elevation: Int,
    val difficulty: String,
    val coordinates: List<LatLng>,
    val startPoint: LatLng,
    val endPoint: LatLng,
    val isLoop: Boolean,
    val description: String?,
    val label: String?,
    val createdAt: Long,
    val isFavorite: Boolean
)