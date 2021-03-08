package me.yonniton.waveform.ui.main

import android.content.Context
import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.yonniton.waveform.NoiseAlertService
import kotlin.math.roundToInt
import kotlin.properties.Delegates.observable
import kotlin.reflect.KProperty

class NoiseAlertViewModel : LifecycleObserver, ViewModel() {

    var noiseAlert: NoiseAlert? by observable(null) { _: KProperty<*>, _: NoiseAlert?, newValue: NoiseAlert? ->
        newValue?.also { bindNoiseAlert(it) }
    }

    val isMonitoring = ObservableBoolean(false)
    val noiseThreshold = ObservableInt(5)
    val amplitude = ObservableInt(0)
    val amplitudeIcon = ObservableInt(android.R.drawable.presence_audio_online)

    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    override fun onCleared() {
        disposable = null
    }

    fun toggleMonitoring(context: Context, shouldEnable: Boolean) {
        noiseAlert?.run {
            val serviceIntent = Intent(context, NoiseAlertService::class.java)
            if (shouldEnable) {
                context.startService(serviceIntent)
            } else {
                context.stopService(serviceIntent)
                stop()
            }
        }
    }

    fun updateNoiseThreshold(progress: Int) {
        noiseThreshold.set(progress)
        noiseAlert?.noiseThreshold = progress
    }

    private fun bindNoiseAlert(noiseAlert: NoiseAlert) {
        disposable = noiseAlert.pollAudioInputAmplitude
            .map { amplitude ->
                amplitude.roundToInt() to (noiseAlert.noiseThreshold)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { noiseVersusThreshold ->
                amplitude.set(noiseVersusThreshold.first)
                if (noiseVersusThreshold.first > noiseVersusThreshold.second) {
                    android.R.drawable.presence_audio_busy
                } else {
                    android.R.drawable.presence_audio_online
                }.also { amplitudeIcon.set(it) }
            }
        isMonitoring.set(noiseAlert.isMonitoring)
        updateNoiseThreshold(noiseAlert.noiseThreshold)
    }
}
