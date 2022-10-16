package com.ionix.demontir.screen

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ionix.demontir.R
import com.ionix.demontir.component.AppClickableInputField
import com.ionix.demontir.component.AppTextInputField
import com.ionix.demontir.component.HomeBengkelBottomSheet
import com.ionix.demontir.mainViewModel
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.ui.theme.BlueQueen
import com.ionix.demontir.util.ListenAppBackHandler
import com.ionix.demontir.util.MapperDomainToPresenter
import com.ionix.demontir.util.MapperResponseToDomain
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.HomeViewModel
import com.ionix.demontir.viewmodel.MainViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, mainViewModel: MainViewModel) {
    /**Attrs*/
    val viewModel = hiltViewModel<HomeViewModel>()
    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val coroutineScope = rememberCoroutineScope()

    /**Function*/
    ListenAppBackHandler {
        if (!mainViewModel.showDashboardItem.value) {
            if (bottomSheetState.isExpanded) {
                coroutineScope.launch {
                    bottomSheetState.collapse()
                }
            } else mainViewModel.showDashboardItem.value = true
        } else {
            if (navController.backQueue.size == 2) System.exit(0)
            else navController.popBackStack()
        }
    }

    /**Content*/
    AnimatedVisibility(
        visible = mainViewModel.showDashboardItem.value,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(listOf(BluePrussian, BlueQueen)))
        )
    }
    HomeScreenContent(
        viewModel = viewModel, searchField = {
            HomeScreenContentSearchField(
                viewModel = viewModel, mainViewModel = mainViewModel
            )
        }, mainViewModel = mainViewModel, bottomSheetState = bottomSheetState
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreenContent(
    searchField: @Composable () -> Unit,
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    bottomSheetState: BottomSheetState
) {
    HomeScreenContentMap(
        viewModel = viewModel, mainViewModel = mainViewModel, bottomSheetState = bottomSheetState
    )

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        HomeScreenContentDashboard(viewModel = viewModel, mainViewModel = mainViewModel)
        searchField()
    }
}

@Composable
private fun HomeScreenContentDashboard(viewModel: HomeViewModel, mainViewModel: MainViewModel) {
    AnimatedVisibility(
        visible = mainViewModel.showDashboardItem.value,
    ) {
        LazyColumn {
            /*Spacer*/
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            /*Top Bar*/
            item {
                HomeScreenTopBar(viewModel = viewModel)
            }

            /*Spacer*/
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            /*Name*/
            item {
                Text(
                    modifier = Modifier, text = "Halo, Fahmi", color = Color.White, fontSize = 32.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("InflateParams", "UseCompatLoadingForDrawables")
@Composable
private fun HomeScreenContentMap(
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    bottomSheetState: BottomSheetState
) {
    /**Attrs*/
    val context = LocalContext.current
    val location = viewModel.locationData.observeAsState()
    val nearestBengkel = viewModel.nearestBengkel.collectAsState()
    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val coroutineScope = rememberCoroutineScope()
    val polyline = Polyline()

    /**Function*/
    polyline.setPoints(viewModel.polylinesGeopoint)
    when (nearestBengkel.value) {
        is Resource.Error -> {
            /*TODO*/
        }
        is Resource.Loading -> {
            /*TODO*/
        }
        is Resource.Success -> {
            nearestBengkel.value.data?.let {
                if (viewModel.nearestBengkelOverlayList.size < it.size) {
                    it.forEach { bengkel ->
                        val item = OverlayItem(
                            bengkel.bengkel_name ?: "", bengkel.bengkel_name ?: "", GeoPoint(
                                (bengkel.bengkel_lat ?: "0").toDouble(),
                                (bengkel.bengkel_long ?: "0").toDouble()
                            )
                        )

                        item.setMarker(context.resources.getDrawable(R.drawable.ic_bengkel_marker))

                        viewModel.nearestBengkelOverlayList.add(item)
                    }
                }
            }
        }
    }
    val nearestBengkelOvelay = ItemizedOverlayWithFocus(context,
        viewModel.nearestBengkelOverlayList,
        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                if (bottomSheetState.isCollapsed) {
                    coroutineScope.launch {
                        bottomSheetState.expand()
                        bottomSheetState.expand()
                    }

                    nearestBengkel.value.data!!.let {
                        if (index < it.size) {
                            val tmp = viewModel.currentBengkelSelected.value

                            viewModel.currentBengkelSelected.value = it[index]

                            if (tmp != viewModel.currentBengkelSelected.value) {
                                viewModel.shouldGetPictures.value = true
                            }
                        }
                    }
                } else {
                    coroutineScope.launch {
                        bottomSheetState.collapse()
                    }

                    nearestBengkel.value.data!!.let {
                        if (index < it.size) {
                            coroutineScope.launch {
                                delay(500)
                                bottomSheetState.expand()
                            }

                            val tmp = viewModel.currentBengkelSelected.value

                            viewModel.currentBengkelSelected.value = it[index]

                            if (tmp != viewModel.currentBengkelSelected.value) {
                                viewModel.shouldGetPictures.value = true
                            }
                        }
                    }
                }

                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return true
            }

        })
    Configuration.getInstance().load(
        context, PreferenceManager.getDefaultSharedPreferences(context)
    )
    if (viewModel.longitudeState.value == null && viewModel.latitudeState.value == null) {
        viewModel.longitudeState.value = location.value?.longitude
        viewModel.latitudeState.value = location.value?.latitude
    } else {
        location.value?.let { recentLocation ->
            viewModel.longitudeState.value?.let { longState ->
                val longitudeChanges = Math.abs((recentLocation.longitude - longState))

                if (longitudeChanges >= 30) {
                    //Call Update for Nearest Bengkel
                    viewModel.longitudeState.value = recentLocation.longitude
                }
            }

            viewModel.latitudeState.value?.let { latState ->
                val latitudeChanges = Math.abs((recentLocation.latitude - latState))

                if (latitudeChanges >= 30) {
                    //Call Update for Nearest Bengkel
                    viewModel.latitudeState.value = recentLocation.latitude
                }
            }

            LaunchedEffect(key1 = true) {
                viewModel.getNearestBengkel(recentLocation.longitude, recentLocation.latitude)
            }
        }
    }

    /**Content*/
    AnimatedVisibility(visible = !mainViewModel.showDashboardItem.value) {
        BottomSheetScaffold(
            sheetContent = {
                HomeBengkelBottomSheet(
                    homeViewModel = viewModel,
                    shouldGetPictures = viewModel.shouldGetPictures
                )
            }, sheetPeekHeight = 0.dp, scaffoldState = bottomSheetScaffoldState
        ) {
            AndroidView(
                factory = { context ->
                    val view =
                        LayoutInflater.from(context).inflate(R.layout.layout_map, null, false)
                    val mapView = view.findViewById<MapView>(R.id.mapview)
                    val mapController = mapView.controller
                    val startPoint = GeoPoint(
                        -7.959, 112.609
                    )
                    val locationOverlay = MyLocationNewOverlay(mapView)
                    val marker = Marker(mapView)

                    marker.apply {
                        position = startPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        icon = context.resources.getDrawable(R.drawable.ic_map_marker, null)
                    }

                    mapController.apply {
                        setZoom(18.0)
                        setCenter(startPoint)
                    }

                    mapView.apply {
                        applicationWindowToken
                        setMultiTouchControls(true)
                        setBuiltInZoomControls(false)
                        overlays.add(marker)
                        overlays.add(nearestBengkelOvelay)
                        overlays.add(polyline)
                        setTileSource(TileSourceFactory.MAPNIK)
                    }

                    view
                },
                update = { view ->
                    val mapView = view.findViewById<MapView>(R.id.mapview)

                    mapView.apply {
                        applicationWindowToken
                        overlays.add(nearestBengkelOvelay)
                        overlays.add(polyline)
                        setTileSource(TileSourceFactory.MAPNIK)
                    }
                })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HomeScreenContentSearchField(viewModel: HomeViewModel, mainViewModel: MainViewModel) {
    /*Spacer if Dashboard Item disappeared*/
    Spacer(modifier = Modifier.height(16.dp))
    val focusManager = LocalFocusManager.current

    /*Search Field*/
    AnimatedContent(targetState = mainViewModel.showDashboardItem.value) { showDashboard ->
        if (showDashboard) {
            AppClickableInputField(placeHolderText = "Cari bengkel di sekitar sini",
                valueState = mainViewModel.searchState,
                endContent = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                },
                onClick = {
                    mainViewModel.showDashboardItem.value = !mainViewModel.showDashboardItem.value
                })
        } else {
            AppTextInputField(
                placeHolderText = "Cari bengkel di sekitar sini",
                valueState = mainViewModel.searchState,
                endContent = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (mainViewModel.searchState.value.isEmpty()) mainViewModel.showDashboardItem.value =
                        true
                    else {
                        focusManager.clearFocus(true)
                        /*TODO SEARCH HERE*/
                    }
                })
            )
        }
    }
}

@Composable
private fun HomeScreenTopBar(viewModel: HomeViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = Color.White
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }
    }
}