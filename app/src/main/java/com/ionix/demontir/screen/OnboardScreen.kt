package com.ionix.demontir.screen

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.*
import com.ionix.demontir.R
import com.ionix.demontir.component.AppButtonField
import com.ionix.demontir.mainViewModel
import com.ionix.demontir.navigation.MainNavigation
import com.ionix.demontir.viewmodel.OnboardViewModel

//@OptIn(ExperimentalPagerApi::class)
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OnboardScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<OnboardViewModel>()
    val pagerState = rememberPagerState()

    /**Function*/

    /**Content*/
    BottomSheetScaffold(
        sheetElevation = 0.dp,
        sheetContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                AppButtonField(
                    enabled = viewModel.pageState.value > 0,
                    onClick = { viewModel.pageState.value-- }
                ) {
                    Text(text = "Sebelumnya", color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                AppButtonField(
                    onClick = {
                        if (viewModel.pageState.value < 2) viewModel.pageState.value++
                        else {
                            viewModel.saveIsFirstTimeEnteringApp()
                            navController.navigate(route = MainNavigation.HomeScreen.name){
                                popUpTo(route = MainNavigation.OnboardScreen.name){
                                    inclusive = true
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (viewModel.pageState.value < 2) "Selanjutnya" else "Menuju ke Beranda",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    ) {
        OnboardScreenContent(
            navController = navController,
            viewModel = viewModel,
            pagerState = pagerState
        )
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
private fun OnboardScreenContent(
    navController: NavController,
    viewModel: OnboardViewModel,
    pagerState: PagerState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        /*Lewati Btn*/
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Row(
                    modifier = Modifier
                        .clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = Color.Black),
                            onClick = {
                                viewModel.saveIsFirstTimeEnteringApp()
                                navController.navigate(route = MainNavigation.HomeScreen.name){
                                    popUpTo(route = MainNavigation.OnboardScreen.name){
                                        inclusive = true
                                    }
                                }
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowRight,
                        contentDescription = "Arrow Right",
                        tint = Color.Black
                    )
                    Text(text = "Lewati", color = Color.Gray)
                }
            }
        }

        /*Spacer*/
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        /*Pager*/
        item {
            AnimatedContent(targetState = viewModel.pageState.value) { page ->
                when (page) {
                    0 -> OnboardContentPage1()
                    1 -> OnboardContentPage2()
                    2 -> OnboardContentPage3()
                }
            }
        }
    }
}

@Composable
fun OnboardContentPage1() {
    /**Attrs*/
    val iconSize = LocalConfiguration.current.screenWidthDp / 5
    val vectorHeight = LocalConfiguration.current.screenHeightDp / 2.5

    /**Content*/
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        /*Top Icon*/
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier.size(iconSize.dp),
                model = R.drawable.ic_logo,
                contentDescription = "Icon"
            )
            Text(text = "DE MONTIR", fontSize = 24.sp)
        }

        /*Vector*/
        Spacer(modifier = Modifier.height(32.dp))
        AsyncImage(
            model = R.drawable.ic_img_onboard_1,
            contentDescription = "Onboard Vector",
            contentScale = ContentScale.FillWidth
        )

        /*Text Below*/
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                textAlign = TextAlign.Center,
                text = "Pelanggan bisa meminta montir untuk datang ke lokasi pilihan",
                fontSize = 32.sp
            )
        }
    }
}

@Composable
fun OnboardContentPage2() {
    /**Attrs*/
    val iconSize = LocalConfiguration.current.screenWidthDp / 5
    val vectorHeight = LocalConfiguration.current.screenHeightDp / 2.5

    /**Content*/
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        /*Top Icon*/
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier.size(iconSize.dp),
                model = R.drawable.ic_logo,
                contentDescription = "Icon"
            )
            Text(text = "DE MONTIR", fontSize = 24.sp)
        }

        /*Vector*/
        Spacer(modifier = Modifier.height(32.dp))
        AsyncImage(
            model = R.drawable.ic_img_onboard_2,
            contentDescription = "Onboard Vector",
            contentScale = ContentScale.FillWidth
        )

        /*Text Below*/
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                textAlign = TextAlign.Center,
                text = "Pelanggan bisa konsultasi dengan pihak bengkel",
                fontSize = 32.sp
            )
        }
    }
}

@Composable
fun OnboardContentPage3() {
    /**Attrs*/
    val iconSize = LocalConfiguration.current.screenWidthDp / 5
    val vectorHeight = LocalConfiguration.current.screenHeightDp / 2.5

    /**Content*/
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        /*Top Icon*/
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier.size(iconSize.dp),
                model = R.drawable.ic_logo,
                contentDescription = "Icon"
            )
            Text(text = "DE MONTIR", fontSize = 24.sp)
        }

        /*Vector*/
        Spacer(modifier = Modifier.height(32.dp))
        AsyncImage(
            model = R.drawable.ic_img_onboard_3,
            contentDescription = "Onboard Vector",
            contentScale = ContentScale.FillWidth
        )

        /*Text Below*/
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                textAlign = TextAlign.Center,
                text = "Maps akan memandu Anda untuk sampai ke lokasi bengkel",
                fontSize = 32.sp
            )
        }
    }
}