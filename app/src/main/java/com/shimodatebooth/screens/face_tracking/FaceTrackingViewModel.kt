package com.shimodatebooth.screens.face_tracking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FaceTrackingViewModel : ViewModel() {

    private val _faceDetected = MutableStateFlow(false)
    val faceDetected: StateFlow<Boolean> = _faceDetected

    fun updateFaceDetected(detected: Boolean) {
        _faceDetected.value = detected
    }
}