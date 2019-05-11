package jarm.mastodon.radio.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import jarm.mastodon.radio.R
import jarm.mastodon.radio.services.RadioService

class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var serviceButton: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        serviceButton = findViewById(R.id.switch_start_service)
        serviceButton.setOnCheckedChangeListener(this)

        updateServiceButton()
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
}
