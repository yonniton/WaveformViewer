package me.yonniton.waveform.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoiseAlertFactory(private val application: Application) : ViewModelProvider.Factory {

    fun provideSoundMeter() = SoundMeter()

    fun provideMediaProvider() = MediaProvider(application)

    fun provideNoiseAlert() = NoiseAlert(
        provideSoundMeter(),
        provideMediaProvider()
    )

    fun provideNoiseAlertViewModel() = NoiseAlertViewModel()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return provideNoiseAlertViewModel() as T
    }
}
