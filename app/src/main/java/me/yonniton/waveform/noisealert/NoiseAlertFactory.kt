package me.yonniton.waveform.noisealert

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.yonniton.waveform.common.AppSettings
import me.yonniton.waveform.noisealert.ui.NoiseAlertViewModel

class NoiseAlertFactory(private val application: Application) : ViewModelProvider.Factory {

    fun provideSoundMeter() = SoundMeter()

    fun provideAppSettings() = AppSettings(application.getSharedPreferences("settings", 0))

    fun provideMediaProvider() = MediaProvider(application)

    fun provideNoiseAlert() = NoiseAlert(
        provideSoundMeter(),
        provideAppSettings(),
        provideMediaProvider()
    )

    fun provideNoiseAlertViewModel() = NoiseAlertViewModel()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return provideNoiseAlertViewModel() as T
    }
}
