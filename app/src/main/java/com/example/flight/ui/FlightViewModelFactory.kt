package com.example.flight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.flight.data.AirportDao
import com.example.flight.data.FavoriteDao
import com.example.flight.data.UserPreferencesRepository
import com.example.flight.ui.screens.FlightViewModel

class FlightViewModelFactory(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(FlightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightViewModel(airportDao, favoriteDao, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}