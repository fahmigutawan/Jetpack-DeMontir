package com.ionix.demontir.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ionix.demontir.component.AppButtonField
import com.ionix.demontir.component.AppTextInputField
import com.ionix.demontir.component.AppTopBarMidTitle
import com.ionix.demontir.model.api.response.ChatResponse
import com.ionix.demontir.model.api.response.UserInfoResponse
import com.ionix.demontir.ui.theme.BluePowder
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.ChatDetailViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatDetailScreen(
    uid_1: String,
    uid_2: String,
    order_id: String = "",
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<ChatDetailViewModel>()
    val otherUser = viewModel.otherUser.collectAsState()
    val myUser = viewModel.myUser.collectAsState()

    /**Function*/
    LaunchedEffect(key1 = true) {
        viewModel.getOtherUserInfoById(uid1 = uid_1, uid2 = uid_2)
    }
    LaunchedEffect(key1 = true) {
        viewModel.getAvailableChatRoom(
            possibleChannel1 = "$uid_1-$uid_2",
            possibleChannel2 = "$uid_2-$uid_1",
            user_1 = uid_1,
            user_2 = uid_2,
            onSuccess = { channel_id -> viewModel.channel_id = channel_id },
            onFailed = { /*TODO*/ }
        )
    }
    if (viewModel.channel_id.isNotEmpty()) {
        viewModel.listenToChatByChannelId(
            channel_id = viewModel.channel_id,
            onDataChange = { viewModel.chatRoomSnapshot.value = it },
            onCancelled = { /*TODO*/ }
        )
    }

    /**Content*/
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBarMidTitle(
                onBackClicked = { navController.popBackStack() },
                title = otherUser.value?.data?.name ?: "Loading..."
            )
        },
        bottomBar = {
            BottomBar(
                viewModel = viewModel,
                order_id = order_id,
                myUser = myUser,
                otherUser = otherUser
            )
        }
    ) {
        ChatDetailContent(
            viewModel = viewModel,
            myUser = myUser,
            otherUser = otherUser
        )
    }
}

@Composable
private fun ChatDetailContent(
    myUser: State<Resource<UserInfoResponse>?>,
    otherUser: State<Resource<UserInfoResponse>?>,
    viewModel: ChatDetailViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item{
            Spacer(modifier = Modifier.height(16.dp))
        }

        viewModel.chatRoomSnapshot.value?.let {
            items(it.children.toList()) { item ->
                item?.let { chatRef ->
                    val chat = chatRef.getValue(ChatResponse::class.java)

                    if (myUser.value is Resource.Success
                        && otherUser.value is Resource.Success
                    ) {
                        chat?.sender?.let { senderUid ->
                            when {
                                (senderUid.equals(myUser.value?.data?.uid ?: "")) -> {
                                    ChatBubbleMe(
                                        chat = chat.chat ?: "",
                                        pic_url = myUser.value?.data?.profile_picture ?: ""
                                    )
                                }
                                (senderUid.equals(otherUser.value?.data?.uid ?: "")) -> {
                                    ChatBubbleOther(
                                        chat = chat.chat ?: "",
                                        pic_url = otherUser.value?.data?.profile_picture ?: ""
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item{
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ChatBubbleMe(chat: String, pic_url: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bubble
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .background(BluePowder), contentAlignment = Alignment.Center
            ) {
                Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), text = chat, color = Color.Black)
            }

            // Profile Pic
            AsyncImage(
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Crop,
                model = pic_url,
                contentDescription = "Profile Pic"
            )
        }
    }
}

@Composable
private fun ChatBubbleOther(chat: String, pic_url: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profile Pic
            AsyncImage(
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Crop,
                model = pic_url,
                contentDescription = "Profile Pic"
            )

            // Bubble
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .background(BluePowder), contentAlignment = Alignment.Center
            ) {
                Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), text = chat, color = Color.Black)
            }
        }
    }
}

@Composable
private fun BottomBar(
    viewModel: ChatDetailViewModel,
    order_id: String,
    myUser: State<Resource<UserInfoResponse>?>,
    otherUser: State<Resource<UserInfoResponse>?>
) {
    val orderDetail = viewModel.orderDetail.collectAsState()
    if (order_id.isNotEmpty()) {
        LaunchedEffect(key1 = true) {
            viewModel.getOrderDetailByOrderId(order_id)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (order_id.isNotEmpty()) {
            when (orderDetail.value) {
                is Resource.Error -> { /*TODO*/
                }
                is Resource.Loading -> { /*TODO*/
                }
                is Resource.Success -> {
                    BottomAppBar(backgroundColor = BluePrussian) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (i in 1..3) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Circle,
                                        contentDescription = "State",
                                        tint = if ((orderDetail.value?.data?.order_status
                                                ?: 1) <= i
                                        ) Color.Red else Color.Gray
                                    )
                                    Text(
                                        text = when ((orderDetail.value?.data?.order_status ?: 1)) {
                                            1 -> "Order Diterima"
                                            2 -> "Dalam Perjalanan"
                                            3 -> "Sampai"
                                            else -> ""
                                        },
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                null -> { /*TODO*/
                }
            }
        }

        BottomAppBar(backgroundColor = Color.White) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTextInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeHolderText = "Ketik di sini",
                    valueState = viewModel.chatState
                )

                IconButton(onClick = {
                    if (viewModel.chatState.value.isNotEmpty()) {
                        viewModel.sendChat(
                            sender = myUser.value?.data?.uid ?: "",
                            receiver = otherUser.value?.data?.uid ?: "",
                            onSuccess = { /*TODO*/ },
                            onFailed = { /*TODO*/ }
                        )

                        viewModel.chatState.value = ""
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = BluePrussian
                    )
                }
            }
        }
    }
}