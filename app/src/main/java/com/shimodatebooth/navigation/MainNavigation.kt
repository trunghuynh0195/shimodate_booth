package com.shimodatebooth.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shimodatebooth.screens.FaceTrackingScreen
import com.shimodatebooth.screens.HomeScreen
import com.shimodatebooth.screens.SearchVideoScreen
import com.shimodatebooth.screens.VideoDetailScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

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

            composable(AppScreens.FaceTrackingScreen.route) {
                FaceTrackingScreen(navController)
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
}