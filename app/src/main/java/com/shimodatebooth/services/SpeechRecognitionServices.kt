package com.shimodatebooth.services

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.camera.core.processing.util.GLUtils.TAG

class SpeechRecognitionServices {
    companion object {

        // function to start listening for speech input
        fun startListening(
            recognizer: SpeechRecognizer,
            onResult: (String) -> Unit,
            onError: (String) -> Unit,
        ) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!")
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000
                ); // 1 giây
            }

            recognizer.setRecognitionListener(object : android.speech.RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    // Ready for speech
                }

                override fun onBeginningOfSpeech() {
                    // Speech has started
                    println("Listening...");
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // This method is called when the volume of the speech changes
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // Buffer received
                }

                override fun onEndOfSpeech() {
                    println("End...");
                    // Speech has ended
                }

                override fun onError(error: Int) {
                    // Handle error
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client-side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service is busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Unknown error: $error"
                    }
                    onError(errorMessage)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        onResult(matches[0]) // Get the first match as the result
                    }
                }

                @SuppressLint("RestrictedApi")
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        onResult(matches[0]) // Get the first match as the result
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                }
            })

            recognizer.startListening(intent)


            // Xử lý dừng lắng nghe sau 2 giây để thu kết quả nhanh hơn
            Handler(Looper.getMainLooper()).postDelayed({
                recognizer.stopListening()
                Log.d("SpeechRecognizer", "Listening stopped after 2 seconds.")
            }, 2000) // 1000ms = 1
        }
    }
}