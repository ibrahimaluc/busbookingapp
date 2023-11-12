package com.ibrahimaluc.busbookingapp.ui.screen.trip

import com.ibrahimaluc.busbookingapp.data.remote.Trip

data class ListUiState(
    val isLoading: Boolean,
    var trip: Trip? = null,
    val message:String?=null
)
