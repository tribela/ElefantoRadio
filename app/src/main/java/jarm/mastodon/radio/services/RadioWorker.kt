package jarm.mastodon.radio.services

import android.content.Context
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import jarm.mastodon.radio.tasks.StreamingEndpointRetrievingTask
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.util.*

class RadioWorker(
    private val context: Context,
    private val domain: String,
    private val accessToken: String,
    private var tts: TextToSpeech
) {

    // TODO("Make it list")
    private var websocket: WebSocket? = null
    private val client: OkHttpClient = OkHttpClient()
    private var request: Request? = null
    private val mainHandler = android.os.Handler(Looper.getMainLooper())

    init {
        val streamingUrl = StreamingEndpointRetrievingTask().execute(domain).get()
        Log.i("Elefanto", "Use streaming url: $streamingUrl")

        request = Request.Builder()
            .url("$streamingUrl?stream=public")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
    }

    fun run() {
        websocket = client.newWebSocket(request!!, StreamListener())
    }

    fun stop() {
        websocket?.close(1000, null)
    }

    private val reconnect = Runnable {
        websocket?.cancel()
        run()
    }

    fun fromHtml(html: String): String {
        val dom = Jsoup.parse(html)
        dom.getElementsByClass("invisible").remove()
        dom.select("br").after("\n")
        return dom.wholeText()
    }

    fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        mainHandler.post {
            Toast.makeText(context, msg, duration).show()
        }
    }

    inner class StreamListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d("Elefanto", "Websocket Open")
            showToast("Started", Toast.LENGTH_SHORT)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.e("Elefanto", "Websocket Fail $response $t")
            showToast(t.message!!)
            websocket?.cancel()
            mainHandler.postDelayed(reconnect, 3000)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            val response = JSONObject(text)
            val event = response.getString("event")

            when (event) {
                "update" -> handleUpdate(response)
            }
        }

        private fun handleUpdate(response: JSONObject) {
            val payload = JSONObject(response.getString("payload"))
            val content = payload.getString("content")

            val displayName = payload.getJSONObject("account").getString("display_name")
            val text = fromHtml(content)
            val lang = if (!payload.isNull("language")) payload.getString("language") else null
            val uri = payload.getString("uri")
            Log.d("Elefanto", content)
            Log.i("Elefanto", "$lang $displayName: $text")


            tts.language = tts.defaultVoice.locale
            if (lang != null) {
                val locale = Locale(lang)
                if (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                    tts.language = locale
                }
            }

            tts.speak(
                text,
                TextToSpeech.QUEUE_ADD,
                null,
                uri
            )
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("Elefanto", "Websocket Closed")
        }
    }
}