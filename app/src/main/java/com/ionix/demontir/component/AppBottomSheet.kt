package com.ionix.demontir.component

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.ionix.demontir.component.helper.DummyReviewItem
import com.ionix.demontir.navigation.BranchNavigation
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.ui.theme.BluePowder
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.ui.theme.GreenCeleste
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeBengkelBottomSheet(
    homeViewModel: HomeViewModel,
    shouldGetPictures: MutableState<Boolean>,
    shouldGetProducts: MutableState<Boolean>,
    shouldResetCondition: MutableState<Boolean>,
    onPictureClicked: (String) -> Unit,
    navController: NavController
) {
    homeViewModel.currentBengkelSelected.value?.let { bengkel ->
        val bengkelPictures = homeViewModel.bengkelPictures.collectAsState()
        val bengkelProducts = homeViewModel.bengkelProducts.collectAsState()
        val horizontalViewPagerState = rememberPagerState()
        val localDensity = LocalDensity.current
        var imgWidth by remember { mutableStateOf(0.dp) }
        var imgHeight by remember { mutableStateOf(0.dp) }
        val coroutineScope = rememberCoroutineScope()
        var showDetail by remember { mutableStateOf(false) }

        if (shouldResetCondition.value) {
            LaunchedEffect(key1 = true) {
                showDetail = false
                shouldResetCondition.value = false
            }
        }
        if (shouldGetPictures.value) {
            LaunchedEffect(key1 = true) {
                homeViewModel.getBengkelPicturesByBengkelId(bengkel.bengkel_id ?: "")
            }
        }
        if (shouldGetProducts.value) {
            LaunchedEffect(key1 = true) {
                homeViewModel.getBengkelProductsByBengkelId(bengkel.bengkel_id ?: "")
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(BluePrussian)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(64.dp)
                                    .clip(RoundedCornerShape(Int.MAX_VALUE.dp))
                                    .background(Color.White)
                            )
                        }

                        // Bengkel Name
                        Text(
                            text = bengkel.bengkel_name.toString(),
                            fontSize = 20.sp,
                            color = Color.White
                        )

                        // Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "4.5 / 5", color = Color.White)
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating Icon",
                                tint = Color.Yellow
                            )
                            Text(text = "(596)", color = Color.White)
                        }

                        // Open status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Buka", color = BluePowder)
                            Text(
                                text = "Buka Senin - Jum'at pukul 07.00 - 16.00",
                                color = Color.White
                            )
                        }
                    }
                }

                // Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppButtonField(
                            onClick = { /*TODO*/ },
                            backgroundColor = Color.White,
                            rippleColor = Color.Black
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Route,
                                    contentDescription = "Navigate"
                                )
                                Text(text = "Rute")
                            }
                        }

                        AppButtonField(
                            onClick = { navController.navigate(route = "${BranchNavigation.OrderScreen.name}/${bengkel.bengkel_id ?: ""}") },
                            backgroundColor = Color.White,
                            rippleColor = Color.Black
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payment,
                                    contentDescription = "Navigate"
                                )
                                Text(text = "Pesan Montir")
                            }
                        }
                    }
                }

                // Chat Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppButtonField(
                            onClick = { navController.navigate(route = "${MainNavigation.ChatScreen.name}/${homeViewModel.getCurrentUid()}/${bengkel.bengkel_id}") },
                            backgroundColor = Color.White,
                            rippleColor = Color.Black
                        ) {
                            Icon(
                                imageVector = Icons.Default.Message, contentDescription = "Navigate"
                            )
                        }
                    }
                }

                // Image ViewPager
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White),
                            enabled = horizontalViewPagerState.currentPage != 0,
                            onClick = {
                                if (horizontalViewPagerState.currentPage > 0) {
                                    coroutineScope.launch {
                                        horizontalViewPagerState.animateScrollToPage(
                                            horizontalViewPagerState.currentPage - 1
                                        )
                                    }
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Default.NavigateBefore,
                                contentDescription = "Before",
                                tint = if (horizontalViewPagerState.currentPage != 0) Color.Black else Color.LightGray
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp)
                                .weight(1f)
                                .onSizeChanged {
                                    imgWidth = with(localDensity) {
                                        it.width.toDp() // Becaouse we have 16dp padding on each side
                                    }

                                    imgHeight = imgWidth * 9 / 16
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (bengkelPictures.value) {
                                is Resource.Error -> {
                                    shouldGetPictures.value = false
                                    Text(
                                        text = "Cek koneksi internet anda",
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                }
                                is Resource.Loading -> {
                                    HorizontalPager(count = 1) {
                                        Box(
                                            modifier = Modifier
                                                .size(
                                                    width = imgWidth, height = imgHeight
                                                )
                                                .placeholder(
                                                    visible = true,
                                                    color = Color.Gray,
                                                    highlight = PlaceholderHighlight.shimmer(
                                                        highlightColor = Color.White
                                                    )
                                                )
                                        )
                                    }
                                    HorizontalPagerIndicator(pagerState = horizontalViewPagerState)
                                }
                                is Resource.Success -> {
                                    shouldGetPictures.value = false
                                    bengkelPictures.value.data?.let { list ->
                                        if (list.size == 0) {
                                            Text(
                                                text = "Tidak ada gambar ditemukan",
                                                textAlign = TextAlign.Center,
                                                color = Color.White
                                            )
                                        } else {
                                            HorizontalPager(
                                                count = list.size, state = horizontalViewPagerState
                                            ) { index ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(
                                                            height = imgHeight, width = imgWidth
                                                        )
                                                        .background(Color.Black),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AsyncImage(
                                                        modifier = Modifier.clickable {
                                                            onPictureClicked(
                                                                list[index].picture_url ?: ""
                                                            )
                                                        },
                                                        model = list[index].picture_url ?: "",
                                                        contentDescription = "Bengkel Picture"
                                                    )
                                                }
                                            }

                                            HorizontalPagerIndicator(
                                                pagerState = horizontalViewPagerState,
                                                activeColor = Color.White,
                                                inactiveColor = Color.LightGray
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        IconButton(modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White),
                            enabled = horizontalViewPagerState.currentPage < horizontalViewPagerState.pageCount - 1,
                            onClick = {
                                val count = horizontalViewPagerState.pageCount

                                if (horizontalViewPagerState.currentPage < count) {
                                    coroutineScope.launch {
                                        horizontalViewPagerState.animateScrollToPage(
                                            horizontalViewPagerState.currentPage + 1
                                        )
                                    }
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Default.NavigateNext,
                                contentDescription = "Next",
                                tint = if (horizontalViewPagerState.currentPage < horizontalViewPagerState.pageCount - 1) Color.Black else Color.LightGray
                            )
                        }
                    }
                }

                // Button to Expand detail info
                item {
                    AnimatedContent(targetState = showDetail) { show ->
                        if (show) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.clickable { showDetail = false },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropUp,
                                        contentDescription = "Expand",
                                        tint = GreenCeleste
                                    )
                                    Text(text = "Sembunyikan Detail", color = GreenCeleste)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.clickable { showDetail = true },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "Lihat Detail", color = GreenCeleste)
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Expand",
                                        tint = GreenCeleste
                                    )
                                }
                            }
                        }
                    }
                }

                // All Detail info
                item {
                    AnimatedVisibility(visible = showDetail) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            //Products
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "Melayani:", fontSize = 20.sp, color = Color.White)
                                when (bengkelProducts.value) {
                                    is Resource.Error -> {
                                        shouldGetProducts.value = false
                                        /*TODO Should handle this later*/
                                    }
                                    is Resource.Loading -> {
                                        for (i in (0..5)) {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .placeholder(
                                                        visible = true,
                                                        shape = RoundedCornerShape(Int.MAX_VALUE.dp),
                                                        color = Color.LightGray,
                                                        highlight = PlaceholderHighlight.shimmer(
                                                            highlightColor = Color.White
                                                        )
                                                    ), text = "Loading Item Products"
                                            )
                                        }
                                    }
                                    is Resource.Success -> {
                                        shouldGetProducts.value = false
                                        bengkelProducts.value.data?.let {
                                            it.forEach {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = it.product_name ?: "",
                                                        color = Color.White
                                                    )
                                                    Text(
                                                        text = it.product_price ?: "",
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //Review
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "Ulasan:", fontSize = 20.sp, color = Color.White)
                                DummyReviewItem(
                                    dummyCharacter = "F",
                                    dummyName = "Farhan Gumintang",
                                    dummyReview = "Sangat cepat, saya suka"
                                )
                                DummyReviewItem(
                                    dummyCharacter = "A",
                                    dummyName = "Akbar Victory",
                                    dummyReview = "Pekerjaan rapih"
                                )
                                DummyReviewItem(
                                    dummyCharacter = "B",
                                    dummyName = "Bahar Sudrajat",
                                    dummyReview = "Pekerjaan cepat, semoga awet"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}