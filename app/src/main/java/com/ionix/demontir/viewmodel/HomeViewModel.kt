package com.ionix.demontir.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.model.api.response.BengkelPicturesResponse
import com.ionix.demontir.model.api.response.BengkelResponse
import com.ionix.demontir.model.domain.BengkelDomain
import com.ionix.demontir.util.LocationData
import com.ionix.demontir.util.LocationLiveData
import com.ionix.demontir.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.OverlayItem
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val locationData = LocationLiveData(context)
    val longitudeState = mutableStateOf<Double?>(null)
    val latitudeState = mutableStateOf<Double?>(null)
    val nearestBengkelOverlayList = mutableListOf<OverlayItem>()
    val currentBengkelSelected = mutableStateOf<BengkelResponse?>(null)
    var shouldGetPictures = mutableStateOf(false)
    val polylinesGeopoint = mutableStateListOf<GeoPoint>()

    private val _nearestBengkel =
        MutableStateFlow<Resource<List<BengkelResponse>>>(Resource.Loading())
    val nearestBengkel get() = _nearestBengkel

    private val _bengkelPictures =
        MutableStateFlow<Resource<List<BengkelPicturesResponse>>>(Resource.Loading())
    val bengkelPictures get() = _bengkelPictures

    fun getNearestBengkel(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            repository.getNearestBengkelByLonglat(longitude, latitude).collect {
                _nearestBengkel.value = it
            }
        }
    }

    fun getBengkelPicturesByBengkelId(bengkel_id: String) {
        viewModelScope.launch {
            repository.getBengkelPicturesByBengkelId(bengkel_id).collect {
                it?.let {
                    _bengkelPictures.value = it
                }
            }
        }
    }
}