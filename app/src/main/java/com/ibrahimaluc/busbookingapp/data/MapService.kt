package com.ibrahimaluc.busbookingapp.data

import com.ibrahimaluc.busbookingapp.data.remote.Map
import com.ibrahimaluc.busbookingapp.data.remote.Trip
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MapService {
    @GET("case-study/{id}/stations/")
    suspend fun getStations(
        @Path("id") id: Int,
    ): Map

    @POST("case-study/{id}/stations/{station_id}/trips/{trip_id}")
    suspend fun sendBook(
        @Path("id") id: Int,
        @Path("station_id") stationId: Int,
        @Path("trip_id") tripId: Int
    ): Trip
}