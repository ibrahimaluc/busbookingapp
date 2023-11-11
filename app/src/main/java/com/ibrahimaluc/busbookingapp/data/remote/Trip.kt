package com.ibrahimaluc.busbookingapp.data.remote


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    @SerializedName("bus_name")
    val busName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("time")
    val time: String?
):Parcelable

