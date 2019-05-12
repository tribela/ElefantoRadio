package jarm.mastodon.radio.tasks

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class StreamingEndpointRetrievingTask: AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String?): String {
        val domain = params[0]

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://$domain/api/v1/instance")
            .build()
        val response = client.newCall(request).execute()
        val apiResult = JSONObject(response.body()?.string())
        val streamingUrl = apiResult.getJSONObject("urls").getString("streaming_api")

        return "$streamingUrl/api/v1/streaming/"
    }
}