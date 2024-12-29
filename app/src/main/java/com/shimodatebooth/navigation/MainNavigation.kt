package com.shimodatebooth.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shimodatebooth.R
import com.shimodatebooth.screens.face_tracking.FaceTrackingScreen
import com.shimodatebooth.screens.face_tracking.FaceTrackingViewModel
import com.shimodatebooth.screens.home.HomeScreen
import com.shimodatebooth.screens.search_video.SearchVideoScreen
import com.shimodatebooth.screens.video_detail.VideoDetailScreen

@Composable
fun MainNavigation(
    vm: FaceTrackingViewModel = viewModel()
) {
    val navController = rememberNavController()

    // Trạng thái phát hiện khuôn mặt
    val faceDetected by vm.faceDetected.collectAsState()

    FaceTrackingScreen(navController)

    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        navController = navController,
        startDestination = AppScreens.HomeScreen.route,
        builder = {
            composable(AppScreens.HomeScreen.route) {
                HomeScreen(navController)
            }

            composable(AppScreens.SearchVideoScreen.route) {
                SearchVideoScreen(navController)
            }

            composable(
                route = "${AppScreens.VideoDetailScreen.route}/{url}",
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) {
                val url: String = it.arguments?.getString("url") ?: "";
                VideoDetailScreen(navController, url)
            }
        },
    )

    val previousRoute = navController.previousBackStackEntry?.destination?.route
    LaunchedEffect(faceDetected) {
        if (!faceDetected || previousRoute == null) {
            navController.navigate(AppScreens.HomeScreen.route)
        } else {
            navController.navigate(previousRoute)
        }
    }
}
