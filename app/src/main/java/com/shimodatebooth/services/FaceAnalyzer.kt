package com.shimodatebooth.services

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions


class FaceAnalyzer {
    companion object {

        // Phân tích hình ảnh để phát hiện khuôn mặt
        @OptIn(ExperimentalGetImage::class)
        fun analyzeImage(
            imageProxy: ImageProxy,
            context: Context,
            onResult: (Boolean) -> Unit,
        ) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val inputImage =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val faceMeshDetector = FaceMeshDetection.getClient(
                    FaceMeshDetectorOptions.Builder()
                        .build()
                )

                faceMeshDetector.process(inputImage)
                    .addOnSuccessListener { result ->
                        onResult(result.isNotEmpty())
                    }
                    .addOnFailureListener {
                        onResult(false)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                onResult(false)
            }
        }
    }
}