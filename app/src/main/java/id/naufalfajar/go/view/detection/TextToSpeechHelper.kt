package id.naufalfajar.go.view.detection

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechHelper(private val context: Context) : TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeechHelper", "Language not supported")
            }
        } else {
            Log.e("TextToSpeechHelper", "Initialization failed")
        }
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}