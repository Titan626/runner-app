package com.example.Runner.di

import android.content.Context
import com.example.Runner.data.repository.RouteRepository
import com.example.Runner.data.repository.RouteRepositoryImpl
import com.example.Runner.location.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return LocationManager(context)
    }

    @Provides
    @Singleton
    fun provideRouteRepository(
        routeApi: com.example.Runner.data.repository.RouteApiService,
        claudeService: com.example.Runner.data.repository.ClaudeService
    ): RouteRepository {
        return RouteRepositoryImpl(
            routeApi = routeApi,
            routeDao = createMockRouteDao(),
            claudeService = claudeService
        )
    }

    // Mock implementations for services not yet implemented
    private fun createMockRouteDao(): com.example.Runner.data.repository.RouteDao {
        return object : com.example.Runner.data.repository.RouteDao {
            override suspend fun insertRoute(route: com.example.Runner.data.repository.RouteEntity) {}
            override suspend fun deleteRoute(routeId: String) {}
            override suspend fun getFavoriteRoutes(): kotlinx.coroutines.flow.Flow<List<com.example.Runner.data.model.Route>> {
                return kotlinx.coroutines.flow.flowOf(emptyList())
            }
            override suspend fun toggleFavorite(routeId: String) {}
            override suspend fun getCachedRoutes(cacheKey: String, maxAge: Long): List<com.example.Runner.data.model.Route>? = null
            override suspend fun cacheRoutes(routes: List<com.example.Runner.data.repository.RouteEntity>, cacheKey: String) {}
        }
    }

    private fun createMockClaudeService(): com.example.Runner.data.repository.ClaudeService {
        return object : com.example.Runner.data.repository.ClaudeService {
            override suspend fun generateRouteDescription(route: com.example.Runner.data.model.Route): com.example.Runner.data.repository.ClaudeResponse {
                // Mock Claude responses
                val labels = listOf("Scenic Loop", "Urban Explorer", "Park Trail", "City Sprint", "Quiet Streets")
                val descriptions = listOf(
                    "A beautiful route with varied terrain and interesting landmarks",
                    "Perfect for exploring the urban landscape with moderate elevation",
                    "Peaceful trail through local parks with minimal traffic",
                    "Quick and efficient route for those short on time",
                    "Quiet residential streets ideal for a relaxing run"
                )
                
                return com.example.Runner.data.repository.ClaudeResponse(
                    description = descriptions.random(),
                    label = labels.random()
                )
            }
        }
    }
}