package com.ibrahimaluc.busbookingapp.ui.screen.map

import com.ibrahimaluc.busbookingapp.data.remote.MapItem

data class MapsUiState(
    val isLoading: Boolean,
    var station: List<MapItem>? = null
)
