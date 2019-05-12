package jarm.mastodon.radio.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.EditText
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getMenuInflater()
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    fun openSettings(item: MenuItem) {
        val intent = Intent(this, AccountsActivity::class.java)
        startActivity(intent)
    }

    private fun updateServiceButton() {
        serviceButton.isChecked = RadioService.runningState
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            serviceButton.id -> {
                val serviceIntent = Intent(applicationContext, RadioService::class.java)
                if (isChecked) {
                    val domain = findViewById<EditText>(R.id.text_domain)?.text.toString()
                    val token = findViewById<EditText>(R.id.text_token)?.text.toString()
                    serviceIntent.putExtra(Constants.EXTRA_DOMAIN, domain)
                    serviceIntent.putExtra(Constants.EXTRA_TOKEN, token)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
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
                    serviceButton.isChecked = intent.getBooleanExtra(Constants.EXTRA_SERVICE_RUNNING, false)
                }
            }
        }
    }
}
