package com.ionix.demontir.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.model.api.response.BengkelPicturesResponse
import com.ionix.demontir.model.api.response.BengkelProductResponse
import com.ionix.demontir.model.api.response.BengkelResponse
import com.ionix.demontir.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.OverlayItem
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    val longitudeState = mutableStateOf<Double?>(null)
    val latitudeState = mutableStateOf<Double?>(null)
    val nearestBengkelOverlayList = mutableListOf<OverlayItem>()
    val currentBengkelSelected = mutableStateOf<BengkelResponse?>(null)
    var shouldGetPictures = mutableStateOf(false)
    val shouldGetProducts = mutableStateOf(false)
    val resetBottomSheetCondition = mutableStateOf(false)
    val polylinesGeopoint = mutableStateListOf<GeoPoint>()
    val showLocationPermissionDeniedRationale = mutableStateOf(false)
    val showLocationPermissionDeniedNotRationale = mutableStateOf(false)
    val tmpUrlImageViewer = mutableStateOf("")
    val showImageViewer = mutableStateOf(false)

    private val _nearestBengkel =
        MutableStateFlow<Resource<List<BengkelResponse>>>(Resource.Loading())
    val nearestBengkel get() = _nearestBengkel

    private val _bengkelPictures =
        MutableStateFlow<Resource<List<BengkelPicturesResponse>>>(Resource.Loading())
    val bengkelPictures get() = _bengkelPictures

    private val _bengkelProducts =
        MutableStateFlow<Resource<List<BengkelProductResponse>>>(Resource.Loading())
    val bengkelProducts get() = _bengkelProducts

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

    fun getBengkelProductsByBengkelId(bengkel_id:String){
        viewModelScope.launch {
            repository.getBengkelProductsByBengkelId(bengkel_id).collect{
                it?.let {
                    _bengkelProducts.value = it
                }
            }
        }
    }
}