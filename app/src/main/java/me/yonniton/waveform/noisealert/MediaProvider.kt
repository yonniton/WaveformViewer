package me.yonniton.waveform.noisealert

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import me.yonniton.waveform.R
import me.yonniton.waveform.common.AppSettings

class MediaProvider(
    private val context: Context,
    private val settings: AppSettings
) {

    private companion object {
        const val SETTING_ALERT_AUDIO_URI = "alert_audio_uri"
    }

    var player: ExoPlayer? = null
        set(value) {
            field?.release()
            field = value
        }

    private var uriAudio: Uri
        get() = settings.getString(SETTING_ALERT_AUDIO_URI)
            ?.let { Uri.parse(it) }
            ?: Uri.fromFile(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
                    .resolve("alert.mp3")
            )
        set(value) = settings.putString(SETTING_ALERT_AUDIO_URI, value.toString())

    init {
        this.player = SimpleExoPlayer.Builder(context)
            .build()
    }

    fun setMediaSource(uri: Uri) {
        uri.takeIf {
            context.contentResolver.getType(it)
                ?.matches(Regex("""audio/\w+""")) == true
        }?.also {
            uriAudio = it
        } ?: run {
            Toast.makeText(
                context,
                "could not recognise Uri[$uri] as audio",
                Toast.LENGTH_SHORT
            ).show()
        }
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
                    .let { progressiveMediaSourceFactory -> progressiveMediaSourceFactory.createMediaSource(uriAudio) }
                    .also { mediaSource -> prepare(mediaSource) }
                playWhenReady = true
            }
    }

    fun stop() {
        player?.stop()
    }
}
