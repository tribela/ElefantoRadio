package jarm.mastodon.radio.services

import android.speech.tts.TextToSpeech
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.util.*

class RadioWorker(domain: String, accessToken: String, private var tts: TextToSpeech) {

    // TODO("Make it list")
    private var websocket: WebSocket? = null
    private val client: OkHttpClient = OkHttpClient()
    // TODO("Read instance API and use stream url")
    private val request: Request = Request.Builder()
        .url("wss://$domain/api/v1/streaming/?stream=public")
        .addHeader("Authorization", "Bearer $accessToken")
        .build()

    fun run() {
        websocket = client.newWebSocket(request, StreamListener())
    }

    fun stop() {
        websocket?.close(1000, null)
    }

    inner class StreamListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d("Elefanto", "Websocket Open")
            tts.speak("Started", TextToSpeech.QUEUE_ADD, null, this.hashCode().toString())
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.e("Elefanto", "Websocket Fail $response $t")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            val response = JSONObject(text)
            val event = response.getString("event")

            when (event) {
                "update" -> {
                    val payload = JSONObject(response.getString("payload"))
                    val content = payload.getString("content")
                    val lang = payload.getString("language")
                    val uri = payload.getString("uri")
                    Log.i("Elefanto", "$lang $content")

                    try {
                        val locale = Locale(lang)
                        if (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                            tts.language = locale
                        } else {
                            tts.language = tts.defaultVoice.locale
                        }
                    } catch (e: MissingResourceException) {
                        tts.language = tts.defaultVoice.locale
                    }
                    tts.speak(
                        content,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        uri)
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("Elefanto", "Websocket Closed")
        }
    }
}