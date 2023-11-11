package com.ibrahimaluc.busbookingapp.ui.screen.map

import androidx.lifecycle.viewModelScope
import com.ibrahimaluc.busbookingapp.core.base.BaseViewModel
import com.ibrahimaluc.busbookingapp.core.util.Resource
import com.ibrahimaluc.busbookingapp.data.repository.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : BaseViewModel() {
    private val _state: MutableStateFlow<MapsUiState> =
        MutableStateFlow(MapsUiState(isLoading = true))
    val state: StateFlow<MapsUiState> get() = _state

    fun getMapList(id: Int) {
        job = viewModelScope.launch {
            mapRepository.getStationList(id).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = MapsUiState(isLoading = false, station = result.data)
                    }

                    is Resource.Error -> {
                        _state.value = MapsUiState(isLoading = false)
                    }

                    is Resource.Loading -> {
                        _state.value = MapsUiState(isLoading = true)
                    }
                }
            }.launchIn(this)
        }
    }
}