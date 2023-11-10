package com.ibrahimaluc.busbookingapp.ui.screen

import com.ibrahimaluc.busbookingapp.data.remote.MapItem

data class MapsUiState(
    val isLoading: Boolean,
    val station: List<MapItem>? = null
)
