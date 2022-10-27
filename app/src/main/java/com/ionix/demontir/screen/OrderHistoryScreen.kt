package com.ionix.demontir.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ionix.demontir.R
import com.ionix.demontir.component.AppButtonField
import com.ionix.demontir.component.AppTopBarMidTitle
import com.ionix.demontir.model.api.response.OrderResponse
import com.ionix.demontir.model.api.response.UserInfoResponse
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.ui.theme.VeryLightGray
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.OrderHistoryViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OrderHistoryScreen(
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<OrderHistoryViewModel>()
    val orders = viewModel.orders.collectAsState()

    /**Function*/

    /**Content*/
    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            AppTopBarMidTitle(
                onBackClicked = { navController.popBackStack() }, title = "Riwayat Order"
            )
        }, backgroundColor = VeryLightGray
    ) {
        OrderHistoryContent(
            navController = navController, viewModel = viewModel, orders = orders
        )
    }
}

@Composable
private fun OrderHistoryContent(
    navController: NavController,
    viewModel: OrderHistoryViewModel,
    orders: State<Resource<List<OrderResponse>>?>
) {
    when (orders.value) {
        is Resource.Error -> {
        }
        is Resource.Loading -> {
        }
        is Resource.Success -> {
            orders.value?.data?.let {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(it) { item ->
                        val bengkelInfo = remember { mutableStateOf<UserInfoResponse?>(null) }
                        viewModel.getUserInfoByUid(
                            item.bengkel_id ?: ""
                        ) { bengkelInfo.value = it }

                        bengkelInfo.value?.let { user ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop,
                                        model = user.profile_picture ?: "",
                                        contentDescription = "Profile Picture"
                                    )

                                    Column {
                                        Text(
                                            text = "Diorder dari ${user.name}",
                                            style = MaterialTheme.typography.h6
                                        )

                                        Text(
                                            text = "Status : ${
                                                when (item.order_status) {
                                                    1 -> "Order diterima bengkel"
                                                    2 -> "Montir sedang perjalanan"
                                                    3 -> "Order selesai"
                                                    else -> ""
                                                }
                                            }",
                                            style = MaterialTheme.typography.body2.copy(
                                                fontFamily = FontFamily(
                                                    Font(R.font.poppins_semibold)
                                                )
                                            )
                                        )

                                        Text(
                                            text = "Total : Rp${item.total_price}",
                                            style = MaterialTheme.typography.caption,
                                            color = BluePrussian
                                        )

                                        AppButtonField(
                                            onClick = {
                                                navController.navigate(route = "${MainNavigation.ChatScreen.name}/${viewModel.getCurentUid()}/${item.bengkel_id ?: ""}/${item.order_id ?: ""}")
                                            },
                                            backgroundColor = Color.White,
                                            rippleColor = Color.Black,
                                            borderWidth = 1.dp,
                                            borderColor = Color.LightGray
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Message,
                                                contentDescription = "Navigate"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        null -> {

        }
    }

}