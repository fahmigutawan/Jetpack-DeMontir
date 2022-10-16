package com.ionix.demontir.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.ionix.demontir.ui.theme.BluePowder
import com.ionix.demontir.ui.theme.BluePrussian
import com.ionix.demontir.util.Resource
import com.ionix.demontir.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeBengkelBottomSheet(
    homeViewModel: HomeViewModel,
    shouldGetPictures: MutableState<Boolean>
) {
    homeViewModel.currentBengkelSelected.value?.let { bengkel ->
        val bengkelPictures = homeViewModel.bengkelPictures.collectAsState()
        val horizontalViewPagerState = rememberPagerState()
        val localDensity = LocalDensity.current
        var imgWidth by remember { mutableStateOf(0.dp) }
        var imgHeight by remember { mutableStateOf(0.dp) }
        val coroutineScope = rememberCoroutineScope()

        if (shouldGetPictures.value) {
            LaunchedEffect(key1 = true) {
                homeViewModel.getBengkelPicturesByBengkelId(bengkel.bengkel_id ?: "")
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(BluePrussian)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        Text(text = "Buka Senin - Jum'at pukul 07.00 - 16.00", color = Color.White)
                    }
                }

                // Button
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
                                imageVector = Icons.Default.Route, contentDescription = "Navigate"
                            )
                            Text(text = "Rute")
                        }
                    }

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
                                imageVector = Icons.Default.Payment, contentDescription = "Navigate"
                            )
                            Text(text = "Pesan Montir")
                        }
                    }
                }

                // Chat Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppButtonField(
                        onClick = { /*TODO*/ },
                        backgroundColor = Color.White,
                        rippleColor = Color.Black
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message, contentDescription = "Navigate"
                        )
                    }
                }

                // Image ViewPager
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
                            .padding(8.dp)
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
                                                highlight = PlaceholderHighlight
                                                    .shimmer(highlightColor = Color.White)
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
                                            count = list.size,
                                            state = horizontalViewPagerState
                                        ) { index ->
                                            Box(
                                                modifier = Modifier
                                                    .size(
                                                        height = imgHeight,
                                                        width = imgWidth
                                                    )
                                                    .background(Color.Black),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = list[index].picture_url ?: "",
                                                    contentDescription = "Bengkel Picture"
                                                )
                                            }
                                        }

                                        HorizontalPagerIndicator(pagerState = horizontalViewPagerState, activeColor = Color.White, inactiveColor = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }

                    IconButton(
                        modifier = Modifier
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
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = "Next",
                            tint = if (horizontalViewPagerState.currentPage < horizontalViewPagerState.pageCount - 1) Color.Black else Color.LightGray
                        )
                    }
                }

                // Button to Navigate to Detail Screen
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AppButtonField(
                        onClick = { /*TODO*/ },
                        backgroundColor = Color.White,
                        rippleColor = Color.Black
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Lihat Detail")
                        }
                    }
                }
            }
        }
    }
}