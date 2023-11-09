package com.ibrahimaluc.busbookingapp.data.remote


import com.google.gson.annotations.SerializedName

data class Trip(
    @SerializedName("bus_name")
    val busName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("time")
    val time: String?
)

