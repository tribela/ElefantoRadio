package jarm.mastodon.radio.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import jarm.mastodon.radio.Constants
import jarm.mastodon.radio.R
import jarm.mastodon.radio.activities.MainActivity

class RadioService : Service() {

    companion object {
        const val CHANNEL_ID = "radio_service_channel"
        const val ACTION_SHOW = "Show"
        const val ACTION_STOP = "Stop"
        var runningState: Boolean = false
            private set
    }

    private var worker: RadioWorker? = null
    private var tts: TextToSpeech? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        Log.i("Elefanto", "Service Start")
        setServiceState(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Elefanto", "Service Stop")
        worker?.stop()
        tts?.stop()
        tts?.shutdown()
        setServiceState(false)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when(action) {
            ACTION_SHOW -> showMain()
            ACTION_STOP -> stopSelf()
            null -> {
                val domain = intent?.getStringExtra(Constants.EXTRA_DOMAIN)!!
                val token = intent.getStringExtra(Constants.EXTRA_TOKEN)!!
                tts = TextToSpeech(this, null)
                worker = RadioWorker(this, domain, token, tts!!)
                worker!!.run()
            }
        }
        return Service.START_NOT_STICKY
    }

    private fun setServiceState(running: Boolean) {
        runningState = running
        val intent = Intent()
        intent.action = Constants.BROADCAST_ACTION_SERVICE_RUNNING
        intent.putExtra(Constants.EXTRA_SERVICE_RUNNING, running)
        sendBroadcast(intent)
    }

    private fun showMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun startForegroundService() {
        val mainIntent = Intent(this, this::class.java)
        val stopIntent = Intent(this, this::class.java)
        mainIntent.action = ACTION_SHOW
        stopIntent.action = ACTION_STOP
        val pMainIntent = PendingIntent.getService(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notiChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.service_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notiChannel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.noti_title))
            .setContentText(getString(R.string.noti_content))
            .setContentIntent(pMainIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                getString(R.string.noti_action_stop),
                pStopIntent)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)
    }
}
