package com.shimodatebooth.screens

import android.Manifest
import android.content.Context
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.shimodatebooth.R
import com.shimodatebooth.models.VideoModel
import com.shimodatebooth.navigation.AppScreens
import com.shimodatebooth.services.SpeechRecognitionServices
import com.shimodatebooth.utils.CommonFunctions
import com.shimodatebooth.data.VideoUrls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchVideoScreen(navController: NavController) {
    val context = LocalContext.current;

    val recordAudioPermissionState =
        rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    PermissionRequired(
        permissionState = recordAudioPermissionState,
        permissionNotGrantedContent = {
            LaunchedEffect(Unit) {
                recordAudioPermissionState.launchPermissionRequest()
            }
        },
        permissionNotAvailableContent = {
            Text(text = "RECORD_AUDIO Permission Denied.")
        }
    ) {

        var isSearching by remember { mutableStateOf(false) }
        var searchingText by remember { mutableStateOf("") }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(LocalContext.current)

        val backgroundImage = painterResource(id = R.drawable.search_videos_background)

        // background image
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .requiredSizeIn(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // button to start speech recognition
                Button(
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonColors(
                        containerColor = Color(0xFFA594E8).copy(alpha = 0.6f),
                        contentColor = Color.Black,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = Color.Gray,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 28.dp),
                    onClick = {
                        onSearchTap(navController, recognizer, context) { searching, text ->
                            isSearching = searching
                            searchingText = text
                        }
                    }
                ) {
                    Text(
                        text = "キヌとカイに質問してみよう！",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // expanded space
                Spacer(modifier = Modifier.weight(1f))

                // video list
                LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ) {
                    items(5) { index ->
                        val item = VideoUrls.videos[index]
                        VideoItem(item, navController)
                    }
                }
            }

        }

        // Hiển thị popup đang tìm kiếm
        if (isSearching) SearchingPopup(searchingText)
    }

}

@Composable
fun VideoItem(
    item: VideoModel,
    navController: NavController
) {
    Button(
        shape = RoundedCornerShape(6.dp),
        colors = ButtonColors(
            containerColor = Color(0xFF7749F8),
            contentColor = Color.Black,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray,
        ),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            val encodedUrl =
                URLEncoder.encode(item.url, StandardCharsets.UTF_8.toString())
            navController.navigate("${AppScreens.VideoDetailScreen.route}/${encodedUrl}")
        }
    ) {
        Text(
            text = item.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

/// handle when start speech recognition
fun onSearchTap(
    navController: NavController,
    recognizer: SpeechRecognizer,
    context: Context,
    isSearching: (Boolean, String) -> Unit,
) {
    SpeechRecognitionServices.startListening(
        recognizer,
        onResult = { result ->
            CoroutineScope(Dispatchers.Main).launch {
                onSpeechRecogitionResult(result, context, navController) {
                    isSearching(it, result)
                }
            }
        },
        onError = { error ->
            Toast.makeText(context, "Error occurred: $error", Toast.LENGTH_SHORT).show()
        },
    )
}

/// handle search video when speech recogition result
suspend fun onSpeechRecogitionResult(
    result: String,
    context: Context,
    navController: NavController,
    isSearching: (Boolean) -> Unit,
) {

    var url = ""

    // hiển thị popup đang tìm kiếm
    isSearching(true)

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
    isSearching(false)

    if (url.isEmpty()) {
        Toast.makeText(context, "No match found", Toast.LENGTH_SHORT).show()
    } else {
        // encodeUrl để tránh kí tự đặc biệt dẫn đến truyền args sai
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
        navController.navigate("${AppScreens.VideoDetailScreen.route}/${encodedUrl}")
    }
    println(result)
}


@Composable
fun SearchingPopup(text: String) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = """Đang tìm kiếm "${text}"...""")
            }
        }
    }
}