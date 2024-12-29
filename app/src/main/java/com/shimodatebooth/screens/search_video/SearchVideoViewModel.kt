package com.shimodatebooth.screens.search_video

import android.content.Context
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.shimodatebooth.data.VideoUrls
import com.shimodatebooth.navigation.AppScreens
import com.shimodatebooth.services.SpeechRecognitionServices
import com.shimodatebooth.utils.CommonFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class SearchVideoViewModel : ViewModel() {

    private val _searching = MutableStateFlow(false)
    val searching: StateFlow<Boolean> = _searching

    fun updateIsSearching(value: Boolean) {
        _searching.value = value
    }

    private val _searchingText = MutableStateFlow("")
    val searchingText: StateFlow<String> = _searchingText

    fun updateSearchingText(value: String) {
        _searchingText.value = value
    }


    /// handle when start speech recognition
    fun onSearchTap(
        navController: NavController,
        recognizer: SpeechRecognizer,
        context: Context,
    ) {
        SpeechRecognitionServices.startListening(
            recognizer,
            onResult = { result ->
                updateSearchingText(result)
                CoroutineScope(Dispatchers.Main).launch {
                    onSpeechRecogitionResult(result, context, navController)
                }
            },
            onError = { error ->
                Toast.makeText(context, "Error occurred: $error", Toast.LENGTH_SHORT).show()
            },
        )
    }


    /// handle search video when speech recogition result
    private suspend fun onSpeechRecogitionResult(
        result: String,
        context: Context,
        navController: NavController,
    ) {

        var url = ""

        // hiển thị popup đang tìm kiếm
        updateIsSearching(true)

        // tìm kiếm keyword từ danh sách video có sẵn
        for (video in VideoUrls.videos) {
            val isContains: Boolean = CommonFunctions.hasCommonWord(video.title, result)
            if (isContains) {
                url = video.url
                println(url)
            }
        }

        // delay 3 seconds
        delay(3000)

        // đóng popup
        updateIsSearching(false)

        if (url.isEmpty()) {
            Toast.makeText(context, "No match found", Toast.LENGTH_SHORT).show()
        } else {
            // encodeUrl để tránh kí tự đặc biệt dẫn đến truyền args sai
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            navController.navigate("${AppScreens.VideoDetailScreen.route}/${encodedUrl}")
        }
        println(result)
    }

}
