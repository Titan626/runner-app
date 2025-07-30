package com.example.Runner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Runner.data.model.Route
import com.example.Runner.domain.usecase.DeleteRouteUseCase
import com.example.Runner.domain.usecase.GetFavoriteRoutesUseCase
import com.example.Runner.domain.usecase.ToggleRouteFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteRoutesUseCase: GetFavoriteRoutesUseCase,
    private val toggleRouteFavoriteUseCase: ToggleRouteFavoriteUseCase,
    private val deleteRouteUseCase: DeleteRouteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    val favoriteRoutes: StateFlow<List<Route>> = flow {
        emitAll(getFavoriteRoutesUseCase())
    }.catch { exception ->
        _uiState.value = _uiState.value.copy(
            error = "Failed to load favorites: ${exception.message}"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Favorites are loaded via the StateFlow above
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load favorites: ${e.message}"
                )
            }
        }
    }

    fun removeFromFavorites(routeId: String) {
        viewModelScope.launch {
            toggleRouteFavoriteUseCase(routeId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(error = null)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to remove from favorites: ${error.message}"
                    )
                }
            )
        }
    }

    fun deleteRoute(routeId: String) {
        viewModelScope.launch {
            deleteRouteUseCase(routeId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(error = null)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to delete route: ${error.message}"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)