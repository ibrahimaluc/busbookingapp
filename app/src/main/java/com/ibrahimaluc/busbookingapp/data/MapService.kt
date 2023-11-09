package com.ibrahimaluc.busbookingapp.data

import com.ibrahimaluc.busbookingapp.data.remote.Map
import retrofit2.http.GET
import retrofit2.http.Path

interface MapService {
    @GET("case-study/{id}/stations/")
    suspend fun getStations(
        @Path("id") id: Int,
    ): Map

}