package com.ionix.demontir.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.firebase.auth.UserInfo
import com.ionix.demontir.component.AppTopBarMidTitle
import com.ionix.demontir.model.api.response.GetChatDataFromFirestoreResponse
import com.ionix.demontir.model.api.response.UserInfoResponse
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.ChatViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatScreen(
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<ChatViewModel>()
    val listOfChat = viewModel.listOfChat

    /**Function*/

    /**Content*/
    Scaffold(
        topBar = {
            AppTopBarMidTitle(onBackClicked = { navController.popBackStack() }, title = "Chat")
        }
    ) {
        ChatContent(
            viewModel = viewModel, navController = navController, listOfChat = listOfChat
        )
    }
}

@Composable
private fun ChatContent(
    viewModel: ChatViewModel,
    navController: NavController,
    listOfChat: SnapshotStateList<GetChatDataFromFirestoreResponse>
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when {
            viewModel.listOfChatLoadingCount.value == 2 -> {
                listOfChat.let {
                    items(it) { item ->
                        viewModel.getCurrentUid()?.let { myUid ->
                            if ((item.user_1 ?: "").equals(myUid)) {
                                val otherUid = item.user_2
                                val userInfo = remember { mutableStateOf<UserInfoResponse?>(null) }
                                viewModel.getUserInfoByUid(
                                    uid = otherUid ?: "",
                                    onSuccess = {
                                        userInfo.value = it
                                    }, onFailed = {
                                        /*TODO*/
                                    }
                                )

                                userInfo.value?.let {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate(
                                                    route = "${MainNavigation.ChatScreen.name}/${viewModel.getCurrentUid() ?: ""}/${otherUid ?: ""}"
                                                )
                                            }
                                    ) {
                                        AsyncImage(
                                            modifier = Modifier.size(54.dp),
                                            contentScale = ContentScale.Crop,
                                            model = it.profile_picture ?: "",
                                            contentDescription = "Image"
                                        )

                                        Text(text = it.name ?: "Loading")
                                    }
                                }
                            } else {
                                val otherUid = item.user_1
                                val userInfo = remember { mutableStateOf<UserInfoResponse?>(null) }
                                viewModel.getUserInfoByUid(
                                    uid = otherUid ?: "",
                                    onSuccess = {
                                        userInfo.value = it
                                    }, onFailed = {
                                        /*TODO*/
                                    }
                                )

                                userInfo.value?.let {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate(
                                                    route = "${MainNavigation.ChatScreen.name}/${viewModel.getCurrentUid() ?: ""}/${otherUid ?: ""}"
                                                )
                                            }
                                    ) {
                                        AsyncImage(
                                            modifier = Modifier.size(54.dp),
                                            contentScale = ContentScale.Crop,
                                            model = it.profile_picture ?: "",
                                            contentDescription = "Image"
                                        )

                                        Text(text = it.name ?: "Loading")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                items(20) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .placeholder(
                                    visible = true,
                                    color = Color.LightGray,
                                    shape = CircleShape,
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                                )
                        )

                        Text(
                            modifier = Modifier.placeholder(
                                visible = true,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(4.dp),
                                highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                            ), text = "Loading........"
                        )
                    }
                }
            }
        }
    }
}