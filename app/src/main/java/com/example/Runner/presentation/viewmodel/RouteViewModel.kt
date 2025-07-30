package com.example.Runner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Runner.data.model.*
import com.example.Runner.domain.usecase.*
import com.example.Runner.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val generateRoutesUseCase: GenerateRoutesUseCase,
    private val saveRouteUseCase: SaveRouteUseCase,
    private val toggleRouteFavoriteUseCase: ToggleRouteFavoriteUseCase,
    private val filterRoutesUseCase: FilterRoutesUseCase,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes.asStateFlow()

    private val _selectedRoute = MutableStateFlow<Route?>(null)
    val selectedRoute: StateFlow<Route?> = _selectedRoute.asStateFlow()

    fun generateRoutes(distance: Double, units: DistanceUnit = DistanceUnit.KILOMETERS) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Get current location
            val locationResult = locationManager.getCurrentLocation()
            locationResult.fold(
                onSuccess = { location ->
                    val request = RouteRequest(
                        startLocation = LatLng(location.latitude, location.longitude),
                        targetDistance = distance,
                        units = units,
                        routeType = RouteType.LOOP
                    )

                    // Generate routes
                    val routesResult = generateRoutesUseCase(request)
                    routesResult.fold(
                        onSuccess = { generatedRoutes ->
                            _routes.value = generatedRoutes
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = null,
                                lastRequestDistance = distance,
                                lastRequestUnits = units
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to generate routes"
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Unable to get location: ${error.message}"
                    )
                }
            )
        }
    }

    fun selectRoute(route: Route) {
        _selectedRoute.value = route
    }

    fun clearSelectedRoute() {
        _selectedRoute.value = null
    }

    fun toggleRouteFavorite(routeId: String) {
        viewModelScope.launch {
            toggleRouteFavoriteUseCase(routeId).fold(
                onSuccess = {
                    // Update local state
                    _routes.value = _routes.value.map { route ->
                        if (route.id == routeId) {
                            route.copy(isFavorite = !route.isFavorite)
                        } else {
                            route
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to update favorite: ${error.message}"
                    )
                }
            )
        }
    }

    fun saveRoute(route: Route) {
        viewModelScope.launch {
            saveRouteUseCase(route).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to save route: ${error.message}"
                    )
                }
            )
        }
    }

    fun filterRoutes(filter: RouteFilter) {
        val currentRoutes = _routes.value
        val filteredRoutes = filterRoutesUseCase(currentRoutes, filter)
        _routes.value = filteredRoutes
        _uiState.value = _uiState.value.copy(activeFilter = filter)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshRoutes() {
        val state = _uiState.value
        if (state.lastRequestDistance > 0) {
            generateRoutes(state.lastRequestDistance, state.lastRequestUnits)
        }
    }
}

data class RouteUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastRequestDistance: Double = 0.0,
    val lastRequestUnits: DistanceUnit = DistanceUnit.KILOMETERS,
    val activeFilter: RouteFilter? = null
)