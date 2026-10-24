package com.example.flight.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flight.data.Airport
import com.example.flight.data.AirportDao
import com.example.flight.data.Favorite
import com.example.flight.data.FavoriteDao
import com.example.flight.data.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FlightRow(
    val departureAirport: Airport,
    val destinationAirport: Airport,
    val isFavorite: Boolean
)

class FlightViewModel(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val searchQuery: StateFlow<String> = userPreferencesRepository.searchQuery.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    val favorites: StateFlow<List<Favorite>> = favoriteDao.getAllFavorites().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val autocompleteSuggestions: StateFlow<List<Airport>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank() || _selectedAirport.value != null) {
                flowOf(emptyList())
            } else {
                airportDao.getAirportSuggestions(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val destinationAirports: StateFlow<List<Airport>> = _selectedAirport
        .flatMapLatest { airport ->
            if (airport == null) {
                flowOf(emptyList())
            } else {
                airportDao.getDestinationAirports(airport.iataCode)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _selectedAirport.value = null
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(query)
        }
    }

    fun selectAirport(airport: Airport) {
        _selectedAirport.value = airport
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(airport.iataCode)
        }
    }

    fun toggleFavorite(depCode: String, destCode: String) {
        viewModelScope.launch {
            val currentFavorites = favorites.value
            val existing = currentFavorites.find {
                it.departureCode == depCode && it.destinationCode == destCode
            }
            if (existing != null) {
                favoriteDao.deleteFavorite(depCode, destCode)
            } else {
                favoriteDao.insertFavorite(Favorite(departureCode = depCode, destinationCode = destCode))
            }
        }
    }
}