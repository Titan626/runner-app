package com.example.Runner.domain.usecase

import com.example.Runner.data.model.*
import com.example.Runner.data.repository.RouteRepository
import javax.inject.Inject

class GenerateRoutesUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(request: RouteRequest): Result<List<Route>> {
        return try {
            // Validate request
            if (request.targetDistance <= 0) {
                return Result.failure(IllegalArgumentException("Distance must be greater than 0"))
            }
            
            if (request.targetDistance > 50) {
                return Result.failure(IllegalArgumentException("Distance cannot exceed 50km"))
            }

            // Generate routes
            val result = routeRepository.generateRoutes(request)
            
            result.fold(
                onSuccess = { routes ->
                    // Filter and sort routes
                    val validRoutes = routes
                        .filter { it.coordinates.isNotEmpty() }
                        .take(3) // Limit to 3 routes as per PRD
                    
                    Result.success(validRoutes)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SaveRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(route: Route): Result<Unit> {
        return routeRepository.saveRoute(route)
    }
}

class GetFavoriteRoutesUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke() = routeRepository.getFavoriteRoutes()
}

class ToggleRouteFavoriteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(routeId: String): Result<Unit> {
        return routeRepository.toggleRouteFavorite(routeId)
    }
}

class DeleteRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(routeId: String): Result<Unit> {
        return routeRepository.deleteRoute(routeId)
    }
}

class FilterRoutesUseCase @Inject constructor() {
    operator fun invoke(routes: List<Route>, filter: RouteFilter): List<Route> {
        return when (filter) {
            RouteFilter.FLATTEST -> routes.sortedBy { it.elevation }
            RouteFilter.SHORTEST -> routes.sortedBy { it.distance }
            RouteFilter.FASTEST -> routes.sortedBy { it.duration }
            RouteFilter.MOST_SCENIC -> routes.sortedByDescending { 
                // Simple scoring based on elevation variety and distance from urban areas
                // This would be enhanced with actual scenic scoring logic
                it.elevation * 0.1 + it.distance * 0.2
            }
        }
    }
}