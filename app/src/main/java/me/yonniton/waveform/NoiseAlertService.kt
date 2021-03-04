package me.yonniton.waveform

import android.app.Service
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleService


class NoiseAlertService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show()
        return Service.START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show()
    }
}
