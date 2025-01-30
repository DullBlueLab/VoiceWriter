package com.dullbluelab.voicewriter3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dullbluelab.voicewriter3.ui.theme.VoiceWriter3Theme

class MainActivity : ComponentActivity() {

    private var speechRecognizer: ActivityResultLauncher<Intent>? = null
    private var flagSpeechNow: Boolean = false
    private var onResultSpeechRecognizer: ((ArrayList<String>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerSpeechRecognizer()
        enableEdgeToEdge()
        setContent {
            VoiceWriter3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VoiceWriter3App(
                        activity = this
                    )
                }
            }
        }
    }

    private fun registerSpeechRecognizer() {
        speechRecognizer = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            try {
                flagSpeechNow = false
                if (result.resultCode == Activity.RESULT_OK) {
                    val list: ArrayList<String>? =
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (list != null) {
                        onResultSpeechRecognizer?.let { it(list) }
                    }
                }
            }
            catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchSpeechRecognizer(onResult: (ArrayList<String>) -> Unit) {
        try {
            if (!flagSpeechNow) {
                onResultSpeechRecognizer = onResult
                speechRecognizer?.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
                flagSpeechNow = true
            }
        }
        catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun shareEditedText(text: String) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(intent)
        }
        catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        speechRecognizer?.unregister()
        speechRecognizer = null
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VoiceWriter3Theme {
        Greeting("Android")
    }
}