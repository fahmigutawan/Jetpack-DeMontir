package com.ionix.demontir.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ionix.demontir.component.*
import com.ionix.demontir.component.helper.NoProductOnThisBengkel
import com.ionix.demontir.model.api.request.CreateOrderProductRequest
import com.ionix.demontir.model.api.request.OrderProductRequest
import com.ionix.demontir.model.api.response.BengkelProductResponse
import com.ionix.demontir.model.non_api.CustomOrder
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.snackbarListener
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.BengkelProductViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BengkelProductScreen(
    navController: NavController, bengkel_id: String
) {
    /**Attrs*/
    val viewModel = hiltViewModel<BengkelProductViewModel>()
    val bengkelProducts = viewModel.bengkelProducts.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading)

    /**Function*/
    BackHandler {
        if (viewModel.shouldShowBackAlertDialog()) viewModel.showBackAlertDialog = true
        else navController.popBackStack()
    }
    LaunchedEffect(key1 = true) {
        viewModel.getBengkelProductsByBengkelId(bengkel_id)
    }
    snackbarListener(
        "Gagal melakukan order. Coba lagi nanti",
        viewModel.showFailedOrderSnackbar
    )
    snackbarListener(
        "Pilih minimal satu item untuk melakukan order",
        viewModel.showShouldPickAtLeastOneSnackbar
    )
    snackbarListener(
        "Tidak bisa melakukan order dengan bengkel sendiri",
        viewModel.showCouldntOrderToSameUid
    )
    snackbarListener(
        "Tidak bisa melakukan chat dengan bengkel sendiri",
        viewModel.showCouldntChatToSameUid
    )
    if (viewModel.showCustomOrderDialog) {
        Dialog(onDismissRequest = { viewModel.showCustomOrderDialog = false }) {
            Box(
                modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppTextInputField(
                        placeHolderText = "Nama jasa yang diminta",
                        valueState = viewModel.tmpCustomOrderProductName
                    )
                    AppTextInputField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        placeHolderText = "Harga yang diminta",
                        valueState = viewModel.tmpCustomOrderProductPrice
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AppButtonField(
                            onClick = {
                                viewModel.showCustomOrderDialog = false
                                viewModel.tmpCustomOrderProductName.value = ""
                                viewModel.tmpCustomOrderProductPrice.value = ""
                            }, backgroundColor = Color.Red
                        ) {
                            Text(text = "Batal", color = Color.White)
                        }

                        AppButtonField(onClick = {
                            viewModel.showCustomOrderDialog = false
                            viewModel.customOrderList.add(
                                CustomOrder(
                                    viewModel.tmpCustomOrderProductName.value,
                                    viewModel.tmpCustomOrderProductPrice.value.toLong()
                                )
                            )
                            viewModel.tmpCustomOrderProductName.value = ""
                            viewModel.tmpCustomOrderProductPrice.value = ""
                        }) {
                            Text(text = "Tambahkan", color = Color.White)
                        }
                    }
                }
            }
        }
    }
    if (viewModel.showBackAlertDialog) {
        Dialog(onDismissRequest = { viewModel.showCustomOrderDialog = false }) {
            Box(modifier = Modifier.background(Color.White), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Yakin untuk kembali? Perubahan pada halaman ini akan hilang",
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AppButtonField(onClick = {
                            navController.popBackStack()
                            viewModel.showBackAlertDialog = false
                        }) {
                            Text(text = "Kembali", color = Color.White)
                        }

                        AppButtonField(
                            onClick = {
                                viewModel.showBackAlertDialog = false
                            }, backgroundColor = Color.Red
                        ) {
                            Text(text = "Batal", color = Color.White)
                        }
                    }
                }
            }
        }
    }
    if (viewModel.startOrderNowFlow) {
        LaunchedEffect(key1 = true) {
            viewModel.isLoading = true
            val listOfProduct = ArrayList<OrderProductRequest>()
            viewModel.customOrderList.toList().forEach {
                listOfProduct.add(
                    OrderProductRequest(
                        product_id = "custom-$bengkel_id-${it.name}",
                        quantity = 1,
                        sub_total_price = it.price.toString()
                    )
                )
            }
            viewModel.pickedProduct.toList().forEach {
                listOfProduct.add(
                    OrderProductRequest(
                        product_id = it.product_id ?: "",
                        quantity = 1,
                        sub_total_price = it.product_price ?: ""
                    )
                )
            }

            if ((viewModel.getCurrentUid() ?: "").equals(bengkel_id))
                viewModel.showCouldntOrderToSameUid.value = true
            else {
                if (listOfProduct.size == 0) viewModel.showShouldPickAtLeastOneSnackbar.value = true
                else {
                    viewModel.createNewOrder(
                        total_price = listOfProduct.sumOf { it.sub_total_price.toLong() }
                            .toString(),
                        user_long = "0",
                        user_lat = "0",
                        bengkel_id = bengkel_id,
                        onSuccess = { order_id ->
                            navController.navigate(
                                route = "${MainNavigation.ChatScreen.name}/${viewModel.getCurrentUid() ?: ""}/$bengkel_id/$order_id"
                            )
                        },
                        onFailed = {
                            viewModel.showFailedOrderSnackbar.value = true
                        },
                        listOfProduct = listOfProduct.toList()
                    )
                }
            }

            delay(2000)
            viewModel.isLoading = false
            viewModel.startOrderNowFlow = false
        }
    }

    /**Content*/
    SwipeRefresh(state = swipeRefreshState, onRefresh = { /*TODO*/ }) {
        Scaffold(topBar = {
            AppTopBarMidTitle(
                onBackClicked = {
                    if (viewModel.shouldShowBackAlertDialog()) viewModel.showBackAlertDialog = true
                    else navController.popBackStack()
                }, title = "Order"
            )
        }, bottomBar = {
            BottomBar(navController = navController, viewModel = viewModel, bengkel_id = bengkel_id)
        }) { paddingValues ->
            BengkelProductContent(
                viewModel = viewModel,
                paddingValues = paddingValues,
                bengkelProducts = bengkelProducts.value
            )
        }
    }
}

@Composable
private fun BengkelProductContent(
    viewModel: BengkelProductViewModel,
    paddingValues: PaddingValues,
    bengkelProducts: Resource<List<BengkelProductResponse>>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = paddingValues.calculateBottomPadding()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        /*Custom Order Button*/
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                AppButtonField(
                    onClick = { viewModel.showCustomOrderDialog = true },
                    backgroundColor = Color.White,
                    rippleColor = Color.Black,
                    borderColor = Color.Black,
                    borderWidth = 1.dp
                ) {
                    Text(text = "Custom Order")
                }
            }
        }

        /*Title Custom Order*/
        item {
            AnimatedVisibility(visible = viewModel.customOrderList.size > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Custom Order", fontSize = 18.sp)
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                }
            }
        }

        /*List of Custom Order*/
        items(viewModel.customOrderList) { item: CustomOrder ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = item.name, fontSize = 18.sp)
                    Text(text = item.price.toString(), fontSize = 14.sp)
                }
                IconButton(onClick = { viewModel.customOrderList.remove(item) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }

        /*Title List Product*/
        item {
            if (viewModel.showTitleListOfProduct) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Produk di Bengkel Ini", fontSize = 18.sp)
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                }
            }
        }

        /*List Of Product*/
        when (bengkelProducts) {
            is Resource.Error -> {
                item {
                    AppErrorAlert()
                }
            }
            is Resource.Loading -> {
                items(20) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .placeholder(
                                    visible = true,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(Int.MAX_VALUE.dp),
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                                ), text = "Item Product Name"
                        )
                        Text(
                            modifier = Modifier.placeholder(
                                visible = true,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(Int.MAX_VALUE.dp),
                                highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                            ), text = "Price"
                        )
                    }
                }
            }
            is Resource.Success -> {
                bengkelProducts.data?.let {
                    if (it.isEmpty()) {
                        item {
                            NoProductOnThisBengkel()
                        }
                    } else {
                        viewModel.showTitleListOfProduct = true
                        items(it) { item ->
                            val isChecked = remember { mutableStateOf(false) }

                            if (isChecked.value) {
                                LaunchedEffect(key1 = true) {
                                    viewModel.pickedProduct.add(item)
                                }
                            } else {
                                LaunchedEffect(key1 = true) {
                                    viewModel.pickedProduct.remove(item)
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(text = item.product_name ?: "")
                                        Text(text = item.product_price ?: "")
                                    }

                                    AppCheckBox(boolState = isChecked)
                                }

                                Divider(
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                                    thickness = 2.dp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    navController: NavController,
    viewModel: BengkelProductViewModel,
    bengkel_id: String
) {
    BottomAppBar(backgroundColor = BluePrussian) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Chat btn
            AppButtonField(
                onClick = {
                    if ((viewModel.getCurrentUid() ?: "").equals(bengkel_id))
                        viewModel.showCouldntChatToSameUid.value = true
                    else {
                        navController.navigate(
                            route = "${MainNavigation.ChatScreen.name}/${viewModel.getCurrentUid() ?: ""}/$bengkel_id"
                        )
                    }
                }, backgroundColor = Color.White, rippleColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Navigate",
                    tint = Color.Black
                )
            }

            // Order btn
            AppButtonField(
                onClick = { viewModel.startOrderNowFlow = true },
                backgroundColor = Color.White,
                rippleColor = Color.Black
            ) {
                Text(text = "Order sekarang", color = Color.Black)
            }
        }
    }
}