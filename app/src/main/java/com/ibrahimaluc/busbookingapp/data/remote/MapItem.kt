package com.ibrahimaluc.busbookingapp.data.remote


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class MapItem(
    @SerializedName("center_coordinates")
    val centerCoordinates: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("trips")
    val trips: List<Trip>?,
    @SerializedName("trips_count")
    val tripsCount: Int?
) : Parcelable