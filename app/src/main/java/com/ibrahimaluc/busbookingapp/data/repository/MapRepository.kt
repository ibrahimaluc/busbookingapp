package com.ibrahimaluc.busbookingapp.data.repository

import com.ibrahimaluc.busbookingapp.core.util.Resource
import com.ibrahimaluc.busbookingapp.data.MapService
import com.ibrahimaluc.busbookingapp.data.remote.Map
import com.ibrahimaluc.busbookingapp.data.remote.Trip
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MapRepository(
    private val mapService: MapService
) {
    fun getStationList(id: Int): Flow<Resource<Map>> = callbackFlow {
        try {
            trySend(Resource.Success(mapService.getStations(id)))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message.orEmpty()))
        }
        awaitClose { channel.close() }
    }

    fun sendBook(id: Int, stationId: Int, tripId: Int): Flow<Resource<Trip>> = callbackFlow {
        try {
            trySend(Resource.Success(mapService.sendBook(id, stationId, tripId)))
        } catch (e: Exception) {
            trySend(Resource.Error("error"))
        }
        awaitClose { channel.close() }
    }


}


