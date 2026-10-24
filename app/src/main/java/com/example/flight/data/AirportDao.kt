package com.example.flight.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("""
        SELECT * FROM airport 
        WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%' 
        ORDER BY passengers DESC
    """)
    fun getAirportSuggestions(query: String): Flow<List<Airport>>

    // Сонгосон буудлаас бусад бүх буудлуудыг авах (Нислэгийн жагсаалтад зориулж)
    @Query("SELECT * FROM airport WHERE iata_code != :departureCode")
    fun getDestinationAirports(departureCode: String): Flow<List<Airport>>
}