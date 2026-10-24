package com.example.flight.ui
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flight.data.AppDatabase
import com.example.flight.data.UserPreferencesRepository
import com.example.flight.data.dataStore
import com.example.flight.ui.screens.FlightViewModel
import com.example.flight.ui.screens.HomeScreen


@PreviewScreenSizes
@Composable
fun FlightApp() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val dataStore = context.dataStore

    val viewModel: FlightViewModel = viewModel(
        factory = FlightViewModelFactory(
            airportDao = db.airportDao(),
            favoriteDao = db.favoriteDao(),
            preferencesRepository = UserPreferencesRepository(context.dataStore)
        )
    )

    HomeScreen(viewModel = viewModel)
}