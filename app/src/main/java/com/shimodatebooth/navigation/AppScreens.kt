package com.shimodatebooth.navigation

sealed class AppScreens(val route: String) {
    data object HomeScreen : AppScreens("home_screen")
    data object FaceTrackingScreen : AppScreens("face_tracking_screen")
    data object SearchVideoScreen : AppScreens("search_video_screen")
    data object VideoDetailScreen : AppScreens("video_detail_screen")
}