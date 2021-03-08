package me.yonniton.waveform.noisealert

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class NoiseAlert(
    private val soundMeter: SoundMeter,
    internal val mediaProvider: MediaProvider
) {

    private companion object {
        const val POLL_INTERVAL = 300L
    }

    var isMonitoring = false

    var noiseThreshold = 5

    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    /** poll audio-input */
    internal val pollAudioInputAmplitude: Observable<Double>
        get() = Observable.interval(POLL_INTERVAL, TimeUnit.MILLISECONDS, Schedulers.io())
            .map { soundMeter.amplitude }

    internal fun cleanup() {
        mediaProvider.player = null
        disposable = null
    }

    fun start() {
        Log.i("NoiseAlert", "==== start ===")
        soundMeter.start()

        // start noise-monitoring
        disposable = pollAudioInputAmplitude
            .map { amplitude -> amplitude > noiseThreshold }
            .filter { it == true }
            .subscribe {
                Log.i("NoiseAlert", "==== Noise Threshold Exceeded ===")
                mediaProvider.play()
            }
    }

    fun stop() {
        Log.i("NoiseAlert", "==== Stop Noise Monitoring===")
        mediaProvider.stop()
        disposable = null
        soundMeter.stop()
        isMonitoring = false
    }
}
