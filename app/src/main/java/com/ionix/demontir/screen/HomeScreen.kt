package com.ionix.demontir.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.LayoutInflater
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.ionix.demontir.R
import com.ionix.demontir.component.*
import com.ionix.demontir.snackbarListener
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.ui.theme.BlueQueen
import com.ionix.demontir.util.ListenAppBackHandler
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.HomeViewModel
import com.ionix.demontir.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
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
    if (viewModel.shouldGetPictures.value
        || viewModel.shouldGetProducts.value
    ) {
        LaunchedEffect(key1 = true) {
            viewModel.resetBottomSheetCondition.value = true
        }
    }
    snackbarListener("Tidak bisa chat diri sendiri", viewModel.showCouldntChatYourself)

    /**Content*/
    AnimatedVisibility(
        visible = mainViewModel.showDashboardItem.value,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(listOf(BluePrussian, BlueQueen))),
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                contentScale = ContentScale.FillWidth,
                model = R.drawable.ic_home_img,
                contentDescription = "Img"
            )
        }
    }
    HomeScreenContent(
        viewModel = viewModel,
        searchField = {
            HomeScreenContentSearchField(
                viewModel = viewModel, mainViewModel = mainViewModel
            )
        },
        mainViewModel = mainViewModel,
        bottomSheetState = bottomSheetState,
        navController = navController
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreenContent(
    searchField: @Composable () -> Unit,
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    bottomSheetState: BottomSheetState,
    navController: NavController
) {
    HomeScreenContentMap(
        viewModel = viewModel,
        mainViewModel = mainViewModel,
        bottomSheetState = bottomSheetState,
        navController = navController
    )

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        HomeScreenContentDashboard(viewModel = viewModel, mainViewModel = mainViewModel)
        searchField()
    }
}

@Composable
private fun HomeScreenContentDashboard(viewModel: HomeViewModel, mainViewModel: MainViewModel) {
    val userInfo = viewModel.userInfo.collectAsState()

    AnimatedVisibility(
        visible = mainViewModel.showDashboardItem.value
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
            when (userInfo.value) {
                is Resource.Error -> {
                    item {
                        Text(
                            modifier = Modifier,
                            text = "Halo, ...",
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    }
                }
                is Resource.Loading -> {
                    item {
                        Text(
                            modifier = Modifier.placeholder(
                                visible = true,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(8.dp),
                                highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                            ), text = "Halo, Fahmi", color = Color.White, fontSize = 32.sp
                        )
                    }
                }
                is Resource.Success -> {
                    item {
                        Text(
                            modifier = Modifier,
                            text = "Halo, ${userInfo.value?.data?.name}",
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    }
                }
                null -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@SuppressLint("InflateParams", "UseCompatLoadingForDrawables")
@Composable
private fun HomeScreenContentMap(
    navController: NavController,
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    bottomSheetState: BottomSheetState
) {
    /**Attrs*/
    val context = LocalContext.current
//    val location = viewModel.locationData.observeAsState()
    val nearestBengkel = viewModel.nearestBengkel.collectAsState()
    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    val coroutineScope = rememberCoroutineScope()
    val polyline = Polyline()
    val locationPermission =
        rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    val appSettingIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:" + context.getPackageName())
    )

    /**Function*/
    LaunchedEffect(key1 = true) {
        viewModel.getNearestBengkel(112.609, -7.959)
    }
    polyline.setPoints(viewModel.polylinesGeopoint)
    if(!mainViewModel.showPrototypeAlertDialog){
        if (viewModel.showLocationPermissionDeniedRationale.value) {
            AlertDialog(
                onDismissRequest = { /*TODO*/ },
                buttons = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pastikan anda telah mengizinkan akses lokasi pada aplikasi De Montir",
                            textAlign = TextAlign.Center
                        )
                        AppButtonField(
                            onClick = { locationPermission.launchPermissionRequest() }
                        ) {
                            Text(text = "Izinkan", color = Color.White)
                        }
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
        if (viewModel.showLocationPermissionDeniedNotRationale.value) {
            AlertDialog(
                onDismissRequest = { /*TODO*/ },
                buttons = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sepertinya sengaja/tidak sengaja, anda telah menolak permintaan izin lokasi. " +
                                    "\nUntuk melanjutkan, anda harus mengizinkan akses lokasi secara manual di pengaturan",
                            textAlign = TextAlign.Center
                        )
                        AppButtonField(
                            onClick = {
                                context.startActivity(appSettingIntent)
                            }) {
                            Text(text = "Buka Pengaturan", color = Color.White)
                        }
                    }

                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
        if (viewModel.showImageViewer.value) {
            AppImageViewerDialog(
                onDissmissRequest = { viewModel.showImageViewer.value = false },
                url = viewModel.tmpUrlImageViewer.value
            )
        }
        when (locationPermission.status) {
            is PermissionStatus.Denied -> {
                if (locationPermission.status.shouldShowRationale) {
                    viewModel.showLocationPermissionDeniedNotRationale.value = false
                    viewModel.showLocationPermissionDeniedRationale.value = true
                } else {
                    viewModel.showLocationPermissionDeniedRationale.value = false
                    viewModel.showLocationPermissionDeniedNotRationale.value = true
                }
            }
            PermissionStatus.Granted -> {
                viewModel.showLocationPermissionDeniedRationale.value = false
                viewModel.showLocationPermissionDeniedNotRationale.value = false
            }
        }
    }
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

                    nearestBengkel.value.data?.let {
                        if (index < it.size) {
                            val tmp = viewModel.currentBengkelSelected.value

                            viewModel.currentBengkelSelected.value = it[index]

                            if (tmp != viewModel.currentBengkelSelected.value) {
                                viewModel.shouldGetPictures.value = true
                                viewModel.shouldGetProducts.value = true
                            }
                        }
                    }
                } else {
                    coroutineScope.launch {
                        bottomSheetState.collapse()
                    }

                    nearestBengkel.value.data?.let {
                        if (index < it.size) {
                            coroutineScope.launch {
                                delay(500)
                                bottomSheetState.expand()
                            }

                            val tmp = viewModel.currentBengkelSelected.value

                            viewModel.currentBengkelSelected.value = it[index]

                            if (tmp != viewModel.currentBengkelSelected.value) {
                                viewModel.shouldGetPictures.value = true
                                viewModel.shouldGetProducts.value = true
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
//    if (viewModel.longitudeState.value == null && viewModel.latitudeState.value == null) {
//        viewModel.longitudeState.value = location.value?.longitude
//        viewModel.latitudeState.value = location.value?.latitude
//    } else {
//        location.value?.let { recentLocation ->
//            viewModel.longitudeState.value?.let { longState ->
//                val longitudeChanges = Math.abs((recentLocation.longitude - longState))
//
//                if (longitudeChanges >= 30) {
//                    //Call Update for Nearest Bengkel
//                    viewModel.longitudeState.value = recentLocation.longitude
//                }
//            }
//
//            viewModel.latitudeState.value?.let { latState ->
//                val latitudeChanges = Math.abs((recentLocation.latitude - latState))
//
//                if (latitudeChanges >= 30) {
//                    //Call Update for Nearest Bengkel
//                    viewModel.latitudeState.value = recentLocation.latitude
//                }
//            }
//
//            LaunchedEffect(key1 = true) {
//                viewModel.getNearestBengkel(recentLocation.longitude, recentLocation.latitude)
//            }
//        }
//    }

    /**Content*/
    AnimatedVisibility(visible = !mainViewModel.showDashboardItem.value) {
        BottomSheetScaffold(
            sheetContent = {
                HomeBengkelBottomSheet(
                    homeViewModel = viewModel,
                    shouldGetPictures = viewModel.shouldGetPictures,
                    shouldGetProducts = viewModel.shouldGetProducts,
                    shouldResetCondition = viewModel.resetBottomSheetCondition,
                    onPictureClicked = { url ->
                        viewModel.tmpUrlImageViewer.value = url
                        viewModel.showImageViewer.value = true
                    },
                    navController = navController
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

