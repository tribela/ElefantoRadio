package jarm.mastodon.radio.services

import android.content.Context
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import com.sys1yagi.mastodon4j.api.method.Streaming
import okhttp3.OkHttpClient
import org.jsoup.Jsoup

class RadioWorker(
    private val context: Context,
    private val domain: String,
    private val accessToken: String,
    private var tts: TextToSpeech
) {

    // TODO("Make it list")
    private val streamHandler = StreamHandler()
    private val mainHandler = android.os.Handler(Looper.getMainLooper())
    private var shutdownable: Shutdownable? = null
    private var thread: Thread? = null

    fun run() {
        // First, Stop if running
        shutdownable?.shutdown()

        val client = MastodonClient.Builder(
            domain,
            OkHttpClient.Builder(),
            Gson()
        )
            .accessToken(accessToken)
            .useStreamingApi()
            .build()

        val streaming = Streaming(client)
        Log.d("Elefanto", "Starting stream $domain")
        thread = Thread(Runnable {
            try {
                shutdownable = streaming.federatedPublic(streamHandler)
            } catch (e: Mastodon4jRequestException) {
                // TODO: Stop service or something
                showToast(e.message!!)
            }
        })
        thread!!.start()
    }

    fun stop() {
        Log.d("Elefanto", "Stopping steram $domain")
        shutdownable?.shutdown()
        thread?.join()
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

    inner class StreamHandler : Handler {
        override fun onDelete(id: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.i("Elefanto", id.toString())
        }

        override fun onNotification(notification: Notification) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.i("Elefanto", notification.id.toString())
        }

        override fun onStatus(status: Status) {

            // TODO: Check if boost
            val acc = status.account?.userName!!
            val lang = status.language
            val content = status.content

            Log.i("Elefanto", "$acc $lang: $content")
            // TODO:("Speak to tts")
        }
    }
}