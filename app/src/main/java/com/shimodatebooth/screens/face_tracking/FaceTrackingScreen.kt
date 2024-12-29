package com.shimodatebooth.screens.face_tracking

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.shimodatebooth.R
import com.shimodatebooth.navigation.AppScreens
import com.shimodatebooth.screens.search_video.SearchVideoScreen
import com.shimodatebooth.services.FaceAnalyzer

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceTrackingScreen(
    navController: NavController,
    vm: FaceTrackingViewModel = viewModel()
) {

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        permissionNotAvailableContent = {
            Column(
            ) {
                Text(text = "Camera Permission Denied.")
            }
        }
    ) {

        val context = LocalContext.current
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

        // Trạng thái phát hiện khuôn mặt
        val faceDetected by vm.faceDetected.collectAsState()

        // Camera Preview
        CameraPreview(
            onImageProxy = { imageProxy ->
                FaceAnalyzer.analyzeImage(imageProxy, context) { detected ->
                    vm.updateFaceDetected(detected)
                }
            }
        )

        // Xử lý hiển thị màn hình khi phát hiện khuôn mặt
        LaunchedEffect(faceDetected) {
            if (!faceDetected) {
                navController.navigate(AppScreens.SearchVideoScreen.route)
            }
        }
    }

}

@Composable
fun CameraPreview(onImageProxy: (ImageProxy) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener(
                    {
                        val cameraProvider = cameraProviderFuture.get()
                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                    onImageProxy(imageProxy)
                                }
                            }

                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            imageAnalyzer,
                        )
                    },
                    ContextCompat.getMainExecutor(ctx)
                )
            }
        }
    )
}
