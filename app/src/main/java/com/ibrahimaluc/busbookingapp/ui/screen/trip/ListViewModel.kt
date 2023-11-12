package com.ibrahimaluc.busbookingapp.ui.screen.trip

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
class ListViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : BaseViewModel() {
    private val _state: MutableStateFlow<ListUiState> =
        MutableStateFlow(ListUiState(isLoading = true))
    val state: StateFlow<ListUiState> get() = _state

    fun sendBook(id: Int, stationId: Int, tripId: Int) {
        job = viewModelScope.launch {
            mapRepository.sendBook(id, stationId, tripId).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = ListUiState(isLoading = false, trip = result.data)
                    }

                    is Resource.Error -> {
                        _state.value = ListUiState(isLoading = false, message=result.message)

                    }

                    is Resource.Loading -> {
                        _state.value = ListUiState(isLoading = true)
                    }

                }
            }.launchIn(this)

        }
    }
}