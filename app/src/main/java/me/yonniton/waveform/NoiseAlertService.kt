package me.yonniton.waveform

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import me.yonniton.waveform.ui.main.NoiseAlert
import me.yonniton.waveform.ui.main.NoiseAlertViewModelFactory

class NoiseAlertService : LifecycleService() {

    private lateinit var noiseAlert: NoiseAlert

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return NoiseAlertServiceBinder(noiseAlert)
    }

    override fun onCreate() {
        super.onCreate()
        noiseAlert = NoiseAlertViewModelFactory(application)
            .provideNoiseAlert()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        with(noiseAlert) {
            if (!isMonitoring.get()) {
                isMonitoring.set(true)
                start()
            }
        }
        return Service.START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        noiseAlert.stop()
        super.onDestroy()
    }
}

class NoiseAlertServiceBinder(val noiseAlert: NoiseAlert) : Binder()
