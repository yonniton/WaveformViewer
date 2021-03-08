package me.yonniton.waveform.noisealert

import android.media.MediaRecorder

/**
 * records audio
 *
 * source: [Android Example](https://androidexample.com/Detect_Noise_Or_Blow_Sound_-_Set_Sound_Frequency_Thersold/index.php?view=article_discription&aid=108&aaid=130)
 */
class SoundMeter {

    companion object {
        private const val EMA_FILTER = 0.6
    }

    private var recorder: MediaRecorder? = null
    private var ema = 0.0

    fun start() {
        if (recorder == null) {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile("/dev/null")

                runCatching {
                    prepare()
                }.onFailure { t ->
                    t.printStackTrace()
                }
                start()
                ema = 0.0
            }
        }
    }

    fun stop() {
        recorder?.apply {
            stop()
            release()
            recorder = null
        }
    }

    val amplitude: Double
        get() = recorder?.let {
            it.maxAmplitude / 2700.0
        } ?: 0.0

    val amplitudeEMA: Double
        get() {
            val amp = amplitude
            ema = EMA_FILTER * amp + (1 - EMA_FILTER) * ema
            return ema
        }
}
