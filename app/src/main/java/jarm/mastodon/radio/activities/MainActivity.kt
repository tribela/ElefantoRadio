package jarm.mastodon.radio.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import jarm.mastodon.radio.Constants
import jarm.mastodon.radio.R
import jarm.mastodon.radio.services.RadioService

class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var serviceButton: ToggleButton
    private lateinit var serviceReceiver: ServiceReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        serviceButton = findViewById(R.id.switch_start_service)
        serviceButton.setOnCheckedChangeListener(this)

        serviceReceiver = ServiceReceiver()
        val filter = IntentFilter()
        filter.addAction(Constants.BROADCAST_ACTION_SERVICE_RUNNING)
        registerReceiver(serviceReceiver, filter)

        updateServiceButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(serviceReceiver)
    }

    private fun updateServiceButton() {
        serviceButton.isChecked = RadioService.runningState
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id) {
            serviceButton.id -> {
                val serviceIntent = Intent(applicationContext, RadioService::class.java)
                if (isChecked) {
                    startForegroundService(serviceIntent)
                } else {
                    stopService(serviceIntent)
                }
            }
        }
    }

    inner class ServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                Constants.BROADCAST_ACTION_SERVICE_RUNNING -> {
                    serviceButton.isChecked = intent.getBooleanExtra(Constants.EXTRA_SERVICE_RUNNING, false)!!
                }
            }
        }
    }
}
