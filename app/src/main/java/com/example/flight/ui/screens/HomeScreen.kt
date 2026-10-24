package com.example.flight.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FlightViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedAirport by viewModel.selectedAirport.collectAsState()
    val suggestions by viewModel.autocompleteSuggestions.collectAsState()
    val destinations by viewModel.destinationAirports.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Flight Search",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1965B0)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Enter departure airport") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic"
                    )
                },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFDCE8FD),
                    unfocusedContainerColor = Color(0xFFDCE8FD),
                    disabledContainerColor = Color(0xFFDCE8FD),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            when {
                suggestions.isNotEmpty() && selectedAirport == null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(suggestions, key = { it.id }) { airport ->
                            SuggestionRow(
                                airport = airport,
                                onSelect = { viewModel.selectAirport(airport) }
                            )
                        }
                    }
                }

                selectedAirport != null -> {
                    Text(
                        text = "Flights from ${selectedAirport?.iataCode}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(destinations, key = { it.id }) { dest ->
                            val isFav = favorites.any {
                                it.departureCode == selectedAirport?.iataCode && it.destinationCode == dest.iataCode
                            }
                            FlightCard(
                                departCode = selectedAirport?.iataCode ?: "",
                                departName = selectedAirport?.name ?: "",
                                arriveCode = dest.iataCode,
                                arriveName = dest.name,
                                isFavorite = isFav,
                                onFavoriteToggle = {
                                    viewModel.toggleFavorite(selectedAirport?.iataCode ?: "", dest.iataCode)
                                }
                            )
                        }
                    }
                }

                searchQuery.isBlank() -> {
                    if (favorites.isNotEmpty()) {
                        Text(
                            text = "Favorite routes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(favorites, key = { it.id }) { fav ->
                                FlightCard(
                                    departCode = fav.departureCode,
                                    departName = "",
                                    arriveCode = fav.destinationCode,
                                    arriveName = "",
                                    isFavorite = true,
                                    onFavoriteToggle = {
                                        viewModel.toggleFavorite(fav.departureCode, fav.destinationCode)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

