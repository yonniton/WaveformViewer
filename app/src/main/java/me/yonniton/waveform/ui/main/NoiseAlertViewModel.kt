package me.yonniton.waveform.ui.main

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.SimpleExoPlayer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.yonniton.waveform.R
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.properties.Delegates.observable
import kotlin.reflect.KProperty

class NoiseAlertViewModel : LifecycleObserver, ViewModel() {

    internal var noiseAlert: NoiseAlert? by observable(null) { _: KProperty<*>, _: NoiseAlert?, newValue: NoiseAlert? ->
        newValue?.also { newNoiseAlert ->
            disposable = newNoiseAlert.pollAudioInputAmplitude
                .map { amplitude ->
                    amplitude.roundToInt() to (newNoiseAlert.noiseThreshold)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { noiseVersusThreshold ->
                    soundLevel.set(noiseVersusThreshold)
                }
        }
    }

    val soundLevel = ObservableField(0 to (noiseAlert?.noiseThreshold ?: 0))
    val monitoringStatus = ObservableField("Stopped")

    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    override fun onCleared() {
        disposable = null
    }
}

class NoiseAlert(
    private val soundMeter: SoundMeter,
    private val mediaProvider: MediaProvider
) {

    companion object {
        private const val POLL_INTERVAL = 300L
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

class MediaProvider(
    private val context: Context,
    private val mp3Uri: Uri = Uri.fromFile(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
            .resolve("alert.mp3")
    )
) {

    var player: ExoPlayer? = null
        set(value) {
            field?.release()
            field = value
        }

    init {
        player = SimpleExoPlayer.Builder(context)
            .build()
    }

    fun play() {
        player
            ?.takeUnless {
                it.isPlaying
            } // ongoing playback uninterrupted
            ?.run {
                Util.getUserAgent(context, context.getString(R.string.app_name))
                    .let { userAgentString -> DefaultDataSourceFactory(context, userAgentString) }
                    .let { dataSourceFactory -> ProgressiveMediaSource.Factory(dataSourceFactory) }
                    .let { progressiveMediaSourceFactory -> progressiveMediaSourceFactory.createMediaSource(mp3Uri) }
                    .also { mediaSource -> prepare(mediaSource) }
                playWhenReady = true
            }
    }

    fun stop() {
        player?.stop()
    }
}

class NoiseAlertViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

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
